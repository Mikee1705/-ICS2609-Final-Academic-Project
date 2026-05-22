package Servlets;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaServlet extends HttpServlet {

    
    private String SECRET_KEY;
    
    @Override
    public void init() throws ServletException {
        // This runs once when the server starts or the first request hits
        this.SECRET_KEY = getServletContext().getInitParameter("SecretKey");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //this.SECRET_KEY = getServletContext().getInitParameter("SecretKey");

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        if (gRecaptchaResponse == null || gRecaptchaResponse.isEmpty()) {
            request.getSession().setAttribute("Title", "Captcha Missing");
            request.getSession().setAttribute("Error", "Please check the 'I am not a robot' box.");
            response.sendRedirect("LoginError.jsp");
            return;
        }

        if (verifyCaptcha(gRecaptchaResponse)) {
            RequestDispatcher rd = request.getRequestDispatcher("Login");
            rd.forward(request, response);
        } else {
            request.getSession().setAttribute("Title", "Verification Failed");
            request.getSession().setAttribute("Error", "Captcha invalid. Try again.");
            response.sendRedirect("LoginError.jsp");
        }
    }

    private boolean verifyCaptcha(String gRecaptchaResponse) throws IOException {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "secret=" + SECRET_KEY + "&response=" + gRecaptchaResponse;

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(params.getBytes());
            os.flush();
        }

        StringBuilder responseStr = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }
        }
        return responseStr.toString().contains("\"success\": true");
    }
}