<%-- 
    Document   : logout
    Created on : Mar 3, 2026, 6:52:46 PM
    Author     : joshuagaas
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    HttpSession sess = request.getSession(false);
    if (sess != null) {
        sess.invalidate();

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

    }
    response.sendRedirect(request.getContextPath() + "/index.jsp");
    return;
%>