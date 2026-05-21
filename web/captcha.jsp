<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Captcha Page</title>
        <script src="https://www.google.com/recaptcha/api.js" async
        defer></script>
    </head>
    <body>
        <h2>Please Verify You're Not a Robot</h2>
        <form action="CaptchaServlet" method="post">
            <div class="g-recaptcha" data-sitekey="6LfUt68sAAAAANpCwdqWTJ3xSs4jUQRE0lmAFv6m"></div>
            <br/>
            <input type="submit" value="Submit">
        </form>
    </body>
</html>