<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>404 - Page Not Found</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-8">
      <div class="card">
        <div class="card-header bg-warning text-dark">
          <h3>❌ 404 - Page Not Found</h3>
        </div>
        <div class="card-body">
          <h5>🔍 What happened?</h5>
          <p>The page you're looking for doesn't exist or has been moved.</p>

          <div class="alert alert-info">
            <h6>📊 Request Information:</h6>
            <ul class="mb-0">
              <li><strong>Time:</strong> 2025-07-25 15:09:17 UTC</li>
              <li><strong>User:</strong> Sayanduary</li>
              <li><strong>Requested URL:</strong> <%= request.getRequestURL() %></li>
              <li><strong>Context Path:</strong> <%= request.getContextPath() %></li>
            </ul>
          </div>

          <h6>🎯 Try these links:</h6>
          <div class="list-group">
            <a href="<%= request.getContextPath() %>/" class="list-group-item list-group-item-action">
              🏠 Home Page
            </a>
            <a href="<%= request.getContextPath() %>/test" class="list-group-item list-group-item-action">
              🧪 Test Servlet
            </a>
            <a href="<%= request.getContextPath() %>/WEB-INF/views/test.jspviews/test.jsp" class="list-group-item list-group-item-action">
              📄 Test JSP
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>