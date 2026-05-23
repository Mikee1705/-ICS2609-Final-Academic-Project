package com.fap.util;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * DerbyReencrypt
 *
 * One-shot utility for migrating an existing USERS table when the AES
 * encryption key changes between machines/teammates.
 *
 * What it does:
 *   1. Connects to LoginDB on localhost:1527
 *   2. Reads every (USERNAME, PASSWORD) row
 *   3. Decrypts the PASSWORD using OLD_KEY
 *   4. Re-encrypts the plaintext using NEW_KEY
 *   5. Writes the new encrypted value back
 *
 * After running, every row is decryptable using the NEW_KEY — which
 * should match the EncryptionKey context-param in web.xml.
 *
 * ──────────────────────────────────────────────────────────────────
 * BEFORE RUNNING:
 *   1. Set OLD_KEY to the encryption key the original data was made with.
 *      (Must be exactly 16 ASCII chars.)
 *   2. Verify NEW_KEY matches the EncryptionKey in web/WEB-INF/web.xml.
 *   3. Make sure Derby is running and LoginDB is reachable.
 *   4. (Recommended) Back up the USERS table:
 *        CREATE TABLE USERS_BACKUP AS SELECT * FROM USERS WITH NO DATA;
 *        INSERT INTO USERS_BACKUP SELECT * FROM USERS;
 *
 * USAGE:
 *   Right-click this file in NetBeans → Run File.
 *   Watch the console for "Re-encrypted: <username>" lines.
 *   Final line prints "Done. N rows migrated."
 *
 * IDEMPOTENCY: do NOT run twice. The second run would try to decrypt
 * already-NEW-KEY data with the OLD_KEY and corrupt every row.
 * ──────────────────────────────────────────────────────────────────
 */
public class DerbyReencrypt {

    // ════════════════════════════════════════════════════════════════
    //  EDIT THESE TWO VALUES BEFORE RUNNING
    // ════════════════════════════════════════════════════════════════

    /** The 16-char AES key that the EXISTING rows are currently encrypted with. */
    private static final String OLD_KEY = "qzwxEcRbntYMlpiC";

    /** The 16-char AES key from your current web.xml EncryptionKey. */
    private static final String NEW_KEY = "0bj3Ct1f!c@t1on$";

    // ════════════════════════════════════════════════════════════════
    //  DERBY CONNECTION
    //  Match these to the derby.* params in web.xml.
    // ════════════════════════════════════════════════════════════════

    private static final String URL      = "jdbc:derby://localhost:1527/LoginDB";
    private static final String DB_USER  = "app";
    private static final String DB_PASS  = "app";

    // ----------------------------------------------------------------
    // MAIN
    // ----------------------------------------------------------------

    public static void main(String[] args) throws Exception {

        sanityCheckKeys();

        Class.forName("org.apache.derby.jdbc.ClientDriver");

        int updated = 0;
        try (Connection conn = DriverManager.getConnection(URL, DB_USER, DB_PASS)) {
            conn.setAutoCommit(false);  // single transaction — all-or-nothing

            try (Statement   read = conn.createStatement();
                 ResultSet   rs   = read.executeQuery("SELECT USERNAME, PASSWORD FROM USERS");
                 PreparedStatement upd = conn.prepareStatement(
                         "UPDATE USERS SET PASSWORD = ? WHERE USERNAME = ?")) {

                while (rs.next()) {
                    String username = rs.getString("USERNAME").trim();
                    String oldEnc   = rs.getString("PASSWORD").trim();

                    String plaintext;
                    try {
                        plaintext = aes(Cipher.DECRYPT_MODE, OLD_KEY, oldEnc);
                    } catch (Exception e) {
                        System.err.println("[FAIL] Could not decrypt user '" + username
                                + "' — wrong OLD_KEY? : " + e.getMessage());
                        conn.rollback();
                        return;
                    }

                    String newEnc = aes(Cipher.ENCRYPT_MODE, NEW_KEY, plaintext);

                    upd.setString(1, newEnc);
                    upd.setString(2, username);
                    int n = upd.executeUpdate();
                    if (n == 1) {
                        updated++;
                        System.out.println("Re-encrypted: " + username);
                    } else {
                        System.err.println("[WARN] No row updated for: " + username);
                    }
                }
            }

            conn.commit();
        }

        System.out.println();
        System.out.println("Done. " + updated + " rows migrated to NEW_KEY.");
        System.out.println();
        System.out.println("Next steps:");
        System.out.println("  - Try logging in with any existing user + their original plaintext password");
        System.out.println("  - If login fails, restore from USERS_BACKUP and double-check OLD_KEY");
    }

    // ----------------------------------------------------------------
    // HELPERS
    // ----------------------------------------------------------------

    private static String aes(int mode, String key, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(mode, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
        if (mode == Cipher.ENCRYPT_MODE) {
            return Base64.getEncoder().encodeToString(
                    cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } else {
            byte[] dec = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(dec, StandardCharsets.UTF_8);
        }
    }

    private static void sanityCheckKeys() {
        if (OLD_KEY.length() != 16) {
            throw new IllegalStateException("OLD_KEY must be exactly 16 chars. Current length: " + OLD_KEY.length());
        }
        if (NEW_KEY.length() != 16) {
            throw new IllegalStateException("NEW_KEY must be exactly 16 chars. Current length: " + NEW_KEY.length());
        }
        if (OLD_KEY.equals(NEW_KEY)) {
            throw new IllegalStateException("OLD_KEY and NEW_KEY are identical — nothing to migrate.");
        }
        if ("PUT_OLD_KEY_HERE".equals(OLD_KEY)) {
            throw new IllegalStateException("You forgot to set OLD_KEY at the top of the file.");
        }
    }
}
