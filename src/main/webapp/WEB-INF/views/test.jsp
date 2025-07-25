<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JSP Test - Note Taker</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
    <div class="card">
        <div class="card-header bg-primary text-white">
            <h3>✅ JSP Test Successful!</h3>
        </div>
        <div class="card-body">
            <h5>📊 JSP Information</h5>
            <table class="table table-borderless">
                <tr><td><strong>Current Time (UTC):</strong></td><td>2025-07-25 15:09:17</td></tr>
                <tr><td><strong>Current User:</strong></td><td>Sayanduary</td></tr>
                <tr><td><strong>JSP Working:</strong></td><td><span class="text-success">✅ YES</span></td></tr>
                <tr><td><strong>Server Time:</strong></td><td><%= new java.util.Date() %></td></tr>
                <tr><td><strong>Session ID:</strong></td><td><%= session.getId() %></td></tr>
                <tr><td><strong>Context Path:</strong></td><td><%= request.getContextPath() %></td></tr>
                <tr><td><strong>Server Info:</strong></td><td><%= application.getServerInfo() %></td></tr>
            </table>

            <div class="alert alert-info">
                <h6>🎯 Next Steps:</h6>
                <p class="mb-0">If you see this page, JSP is working correctly. Try accessing:</p>
                <ul class="mt-2 mb-0">
                    <li><code><%= request.getContextPath() %>/</code> - Home page</li>
                    <li><code><%= request.getContextPath() %>/test</code> - Test servlet</li>
                </ul>
            </div>

            <a href="<%= request.getContextPath() %>/" class="btn btn-primary">🏠 Go to Home</a>
        </div>
    </div>
</div>
</body>
</html>