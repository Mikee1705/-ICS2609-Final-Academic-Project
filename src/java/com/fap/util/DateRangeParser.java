package com.fap.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;

/**
 * DateRangeParser
 *
 * Helper for safely reading "from" / "to" date params from the request.
 * Accepts ISO format yyyy-MM-dd (HTML date input default).
 *
 * If the params are missing or invalid, falls back to:
 *   - from = first day of the current year
 *   - to   = today
 */
public class DateRangeParser {

    private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd");

    private final Date from;
    private final Date to;

    public DateRangeParser(HttpServletRequest req) {
        this.from = parse(req.getParameter("from"), defaultFrom());
        this.to   = parse(req.getParameter("to"),   defaultTo());
    }

    public Date getFrom() { return from; }
    public Date getTo()   { return to;   }

    /** Returns a human-friendly subtitle e.g. "From Feb 4, 2026 to Feb 22, 2026". */
    public String describe() {
        SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy");
        return "From " + fmt.format(from) + " to " + fmt.format(to);
    }

    // ----------------------------------------------------------------
    // INTERNAL
    // ----------------------------------------------------------------

    private static Date parse(String s, Date fallback) {
        if (s == null || s.trim().isEmpty()) return fallback;
        try {
            return new Date(ISO.parse(s.trim()).getTime());
        } catch (ParseException e) {
            return fallback;
        }
    }

    private static Date defaultFrom() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return new Date(c.getTimeInMillis());
    }

    private static Date defaultTo() {
        return new Date(System.currentTimeMillis());
    }
}
