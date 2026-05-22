package Servlets;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;

public class Security {

    public static String Encrypt(ServletContext context, String PW) {
        String keyString = context.getInitParameter("EncryptionKey");
        
        if (keyString == null || keyString.length() != 16) {
            return null; 
        }
        
        try {
            byte[] key = keyString.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(PW.getBytes(StandardCharsets.UTF_8));
            
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String Decrypt(ServletContext context, String EPW) {
        String keyString = context.getInitParameter("EncryptionKey");
        
        if (keyString == null || keyString.length() != 16) {
            return null; 
        }
        
        try {
            byte[] key = keyString.getBytes(StandardCharsets.UTF_8);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            
            
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decodedBytes = Base64.getDecoder().decode(EPW);

            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
           
            return new String(decryptedBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}