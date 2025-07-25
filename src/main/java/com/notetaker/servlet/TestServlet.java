package com.notetaker.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Test Servlet for deployment verification
 * Created by: Sayanduary
 * Date: 2025-07-25 15:09:17 UTC
 */
@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String currentTime = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Test Servlet - Note Taker</title>");
        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css' rel='stylesheet'>");
        out.println("</head>");
        out.println("<body class='bg-light'>");
        out.println("<div class='container mt-5'>");
        out.println("<div class='card'>");
        out.println("<div class='card-header bg-success text-white'>");
        out.println("<h3>✅ Servlet Test Successful!</h3>");
        out.println("</div>");
        out.println("<div class='card-body'>");
        out.println("<h5>📊 Deployment Information</h5>");
        out.println("<table class='table table-borderless'>");
        out.println("<tr><td><strong>Current Time (UTC):</strong></td><td>" + currentTime + "</td></tr>");
        out.println("<tr><td><strong>Current User:</strong></td><td>Sayanduary</td></tr>");
        out.println("<tr><td><strong>Servlet Working:</strong></td><td><span class='text-success'>✅ YES</span></td></tr>");
        out.println("<tr><td><strong>Context Path:</strong></td><td>" + request.getContextPath() + "</td></tr>");
        out.println("<tr><td><strong>Server Info:</strong></td><td>" + request.getServletContext().getServerInfo() + "</td></tr>");
        out.println("<tr><td><strong>Request URL:</strong></td><td>" + request.getRequestURL() + "</td></tr>");
        out.println("</table>");

        out.println("<h5>🔗 Available URLs</h5>");
        out.println("<ul class='list-group'>");
        out.println("<li class='list-group-item'><a href='" + request.getContextPath() + "/' class='text-decoration-none'>🏠 Home Page</a></li>");
        out.println("<li class='list-group-item'><a href='" + request.getContextPath() + "/login' class='text-decoration-none'>🔐 Login</a></li>");
        out.println("<li class='list-group-item'><a href='" + request.getContextPath() + "/register' class='text-decoration-none'>📝 Register</a></li>");
        out.println("<li class='list-group-item'><a href='" + request.getContextPath() + "/test.jsp' class='text-decoration-none'>🧪 Test JSP</a></li>");
        out.println("</ul>");

        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}