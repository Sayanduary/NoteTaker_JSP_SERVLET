<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Login - Note Taker</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
  <style>
    .login-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    .login-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-radius: 15px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    }
  </style>
</head>
<body>
<div class="login-container d-flex align-items-center justify-content-center">
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-md-6 col-lg-4">
        <div class="login-card p-4">
          <div class="text-center mb-4">
            <i class="fas fa-sticky-note fa-3x text-primary mb-3"></i>
            <h2 class="h4 text-gray-900 mb-4">Welcome Back!</h2>
          </div>

          <!-- Error Message -->
          <c:if test="${error != null}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
              <i class="fas fa-exclamation-circle me-2"></i>
                ${error}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <!-- Success Message -->
          <c:if test="${success != null}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
              <i class="fas fa-check-circle me-2"></i>
                ${success}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <!-- Login Form -->
          <form action="${pageContext.request.contextPath}/login" method="post">
            <input type="hidden" name="redirect" value="${param.redirect}">

            <div class="mb-3">
              <label for="username" class="form-label">
                <i class="fas fa-user me-1"></i>Username
              </label>
              <input type="text"
                     class="form-control"
                     id="username"
                     name="username"
                     value="${username}"
                     placeholder="Enter your username"
                     required
                     autofocus>
            </div>

            <div class="mb-3">
              <label for="password" class="form-label">
                <i class="fas fa-lock me-1"></i>Password
              </label>
              <input type="password"
                     class="form-control"
                     id="password"
                     name="password"
                     placeholder="Enter your password"
                     required>
            </div>

            <div class="mb-3 form-check">
              <input type="checkbox" class="form-check-input" id="rememberMe" name="rememberMe">
              <label class="form-check-label" for="rememberMe">
                Remember me for 7 days
              </label>
            </div>

            <button type="submit" class="btn btn-primary w-100 mb-3">
              <i class="fas fa-sign-in-alt me-1"></i>Sign In
            </button>
          </form>

          <div class="text-center">
            <p class="mb-0">Don't have an account?</p>
            <a href="${pageContext.request.contextPath}/register" class="btn btn-link">
              <i class="fas fa-user-plus me-1"></i>Create Account
            </a>
          </div>

          <div class="text-center mt-3">
            <small class="text-muted">
              © 2025 Note Taker by Sayanduary
            </small>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>