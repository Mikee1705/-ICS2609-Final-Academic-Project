<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Servlets.User" %> 
<%
    HttpSession sess = request.getSession(false);
    Object ClientRole = session.getAttribute("Role");
    Object status = request.getAttribute("status");
    String ClientName = (String) session.getAttribute("UName");

    //Role Security
    if (sess == null || ClientRole == null) {
        // 401: You don't know who they are (Session Expired)
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    } else if (!"Admin".equalsIgnoreCase((String) ClientRole)) {
        // 403: You know them, but they aren't allowed here (Wrong Role)
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Editing Database - Courses</title>

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
              integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
        
        <link rel="stylesheet" href="./Styles/styles.css">
    </head>

    <body class="d-flex flex-column min-vh-100">

        <jsp:include page="Header.jsp" />



        <jsp:include page="Footer.jsp" />
        

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
                integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>
        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
                integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
        crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
                integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
    </body>

</html>
