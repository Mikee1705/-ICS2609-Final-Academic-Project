<%-- 
    Document   : Error
    Created on : 02 20, 26, 8:43:31 AM
    Author     : juliojosechavez
--%>
<%@ page isErrorPage="true" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>
            ERROR <%= request.getAttribute("javax.servlet.error.status_code")%>
        </title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
        <link href="/Styles/Styles.css" rel="stylesheet">
    </head>
    <body>
        <%@ include file="Header.jsp" %>

        <main class="container text-center my-5">

            <h1>
                ${pageContext.errorData.statusCode == 404 ? 'Oops! That page can’t be found.' : 
                  pageContext.errorData.statusCode == 403 ? 'Session Expired! Please log in again.' : 
                  pageContext.errorData.statusCode == 401 ? 'Access Denied! Admin permissions required.' : 
                  'SOMETHING WENT WRONG!!'}
            </h1>


            <hr>
            <h3 style="color: red;">
                Status Code: <%= request.getAttribute("javax.servlet.error.status_code")%>
            </h3>
            <p><strong>Requested URI: </strong><%= request.getAttribute("javax.servlet.error.request_uri")%></p>

            <ul class="list-unstyled">
                <% if (exception != null) {%>
                <li><b>Type:</b> <%= exception.getClass().getName()%></li>
                <li><b>Message:</b> <%= exception.getMessage()%></li>
                <li><b>Trace:</b> <pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre></li>
                    <% } else { %>
                <li><b>Note:</b> No server-side exception occurred</li>
                    <% }
                    %>
            </ul>
            <br>
            <a onclick="window.history.back()" class="btn btn-secondary">&laquo; Return to Previous Page</a>
        </main>


        <%@ include file="Footer.jsp" %>


        <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js" integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"/>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.min.js" integrity="sha384-G/EV+4j2dNv+tEPo3++6LCgdCROaejBqfUeNjuKAiuXbjrxilcCdDz6ZAVfHWe1Y" crossorigin="anonymous"/>
    </body>
</html>
