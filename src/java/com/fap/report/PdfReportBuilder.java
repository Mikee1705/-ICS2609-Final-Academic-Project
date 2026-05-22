package com.fap.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

/**
 * PdfReportBuilder
 *
 * Shared utility for building landscape PDF reports.
 *
 * Responsibilities:
 *   - Builds the filename in the required pattern:  REPORTNAME_yyyyMMddHHmmss.pdf
 *   - Sets the response headers so the browser DOWNLOADS the file
 *     (Content-Disposition: attachment ⇒ client-side download, not server-side)
 *   - Creates an A4 LANDSCAPE document
 *   - Attaches PageNumberEventHandler so every page has header/footer/Page X of Y
 *
 * Typical usage in a servlet:
 *
 *   PdfReportBuilder rpt = new PdfReportBuilder(getServletContext(), response,
 *           "COURSELIST", currentUsername);
 *   Document doc = rpt.startDocument();
 *   doc.add(rpt.title("Course List Report"));
 *   doc.add(myDataTable);
 *   rpt.close();
 */
public class PdfReportBuilder {

    private static final SimpleDateFormat FILENAME_FMT  = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat DISPLAY_FMT   = new SimpleDateFormat("MMMM d, yyyy hh:mm:ss a");

    public static final Font TITLE_FONT     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
    public static final Font SUBTITLE_FONT  = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.DARK_GRAY);
    public static final Font TABLE_HEAD     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    public static final Font TABLE_BODY     = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    public static final Font TABLE_BODY_BOLD= FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLACK);

    public static final BaseColor TABLE_HEAD_BG = new BaseColor(255, 163, 51);  // matches site theme

    private final ServletContext       context;
    private final HttpServletResponse  response;
    private final String               reportName;     // e.g. "COURSELIST"
    private final String               username;
    private final Date                 generatedAt;

    private Document  document;
    private PdfWriter writer;

    public PdfReportBuilder(ServletContext context,
                            HttpServletResponse response,
                            String reportName,
                            String username) {
        this.context     = context;
        this.response    = response;
        this.reportName  = reportName.toUpperCase();
        this.username    = username;
        this.generatedAt = new Date();
    }

    // ----------------------------------------------------------------
    // FILENAME — REPORTNAME_yyyyMMddHHmmss.pdf
    // ----------------------------------------------------------------

    public String buildFilename() {
        return reportName + "_" + FILENAME_FMT.format(generatedAt) + ".pdf";
    }

    // ----------------------------------------------------------------
    // DOCUMENT LIFECYCLE
    // ----------------------------------------------------------------

    /**
     * Configures the HTTP response for a client-side PDF download and
     * returns an open landscape Document ready for content.
     */
    public Document startDocument() throws Exception {
        String filename = buildFilename();

        // Response headers — forces browser to DOWNLOAD on client side
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");

        // LANDSCAPE — A4 rotated
        document = new Document(PageSize.A4.rotate(), 36, 36, 70, 50);
        OutputStream os = response.getOutputStream();
        writer = PdfWriter.getInstance(document, os);

        // Header / footer / Page X of Y / username / timestamp
        String hdr = context.getInitParameter("report.header");
        String ftr = context.getInitParameter("report.footer");
        writer.setPageEvent(new PageNumberEventHandler(
                hdr, ftr, username, DISPLAY_FMT.format(generatedAt)));

        document.open();
        return document;
    }

    public void close() {
        if (document != null && document.isOpen()) {
            document.close();
        }
    }

    // ----------------------------------------------------------------
    // CONVENIENCE FACTORIES
    // ----------------------------------------------------------------

    /** Title paragraph for use right after startDocument(). */
    public Paragraph title(String text) {
        Paragraph p = new Paragraph(text, TITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(8f);
        return p;
    }

    /** Subtitle paragraph e.g. "From Feb 4, 2026 to Feb 22, 2026". */
    public Paragraph subtitle(String text) {
        Paragraph p = new Paragraph(text, SUBTITLE_FONT);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(12f);
        return p;
    }

    /** Creates a table with the given column widths and header labels. */
    public PdfPTable createTable(float[] columnWidths, String[] headers) throws DocumentException {
        PdfPTable table = new PdfPTable(columnWidths.length);
        table.setWidthPercentage(100);
        table.setWidths(columnWidths);
        table.setSpacingBefore(8);
        table.setHeaderRows(1);

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, TABLE_HEAD));
            cell.setBackgroundColor(TABLE_HEAD_BG);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }
        return table;
    }

    /** Plain body cell. */
    public PdfPCell cell(String text) {
        return cell(text, false);
    }

    /** Body cell — bold = true highlights this row (used for the current admin). */
    public PdfPCell cell(String text, boolean bold) {
        PdfPCell c = new PdfPCell(new Phrase(text == null ? "" : text,
                bold ? TABLE_BODY_BOLD : TABLE_BODY));
        c.setPadding(4);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return c;
    }

    // ----------------------------------------------------------------
    // GETTERS
    // ----------------------------------------------------------------

    public Date getGeneratedAt() { return generatedAt; }
    public String getUsername()  { return username; }
}
