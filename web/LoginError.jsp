<%-- 
    Document   : error
    Created on : 05 20, 26, 10:01:40 PM
    Author     : juliojosechavez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login</title>

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
              integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
        
        <link rel="stylesheet" href="./Styles/styles.css">
    </head>

    <body class="d-flex flex-column min-vh-100">

        <jsp:include page="Header.jsp" />

        <main class="container text-center my-5">
            <h1>SOMETHING WENT WRONG!!</h1><hr>
            <h3 style="color: red;">Authentication Failed</h3>
            <p><strong>Why are you seeing this?</strong> You ended up here because you attempted to log in without entering a valid username and password.</p>
            <p><em>System Message: <%= session.getAttribute("Error") %></em></p>
            
            <br>
            <a href="index.jsp" class="btn btn-secondary">&laquo; Return to Login Page</a>
        </main>

        <jsp:include page="Footer.jsp" />
         <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js" integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r" crossorigin="anonymous"/>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.min.js" integrity="sha384-G/EV+4j2dNv+tEPo3++6LCgdCROaejBqfUeNjuKAiuXbjrxilcCdDz6ZAVfHWe1Y" crossorigin="anonymous"/>
    </body>
</html>
