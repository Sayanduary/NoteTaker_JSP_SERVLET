<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Note Taker - Home</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
  <style>
    .hero-section {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      min-height: 100vh;
    }
    .feature-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-radius: 15px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
    .btn-custom {
      background: linear-gradient(45deg, #667eea, #764ba2);
      border: none;
      transition: transform 0.2s ease;
    }
    .btn-custom:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    }
    .brand-logo {
      background: linear-gradient(45deg, #667eea, #764ba2);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  </style>
</head>
<body>
<div class="hero-section d-flex align-items-center">
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-lg-8 text-center">
        <div class="feature-card p-5">
          <!-- Brand Header -->
          <div class="mb-4">
            <i class="fas fa-sticky-note fa-4x text-primary mb-3"></i>
            <h1 class="display-4 brand-logo fw-bold mb-3">Note Taker</h1>
            <p class="lead text-muted mb-4">
              Organize your thoughts and ideas with our simple, elegant note-taking application.
              Built with ❤️ by Sayanduary.
            </p>
          </div>

          <!-- Features -->
          <div class="row mb-4">
            <div class="col-md-4 mb-3">
              <i class="fas fa-edit fa-2x text-primary mb-2"></i>
              <h5>Easy Writing</h5>
              <p class="text-muted small">Create and edit notes with a clean, distraction-free interface.</p>
            </div>
            <div class="col-md-4 mb-3">
              <i class="fas fa-search fa-2x text-primary mb-2"></i>
              <h5>Quick Search</h5>
              <p class="text-muted small">Find your notes instantly with powerful search capabilities.</p>
            </div>
            <div class="col-md-4 mb-3">
              <i class="fas fa-cloud fa-2x text-primary mb-2"></i>
              <h5>Secure Storage</h5>
              <p class="text-muted small">Your notes are safely stored and always accessible.</p>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="d-grid gap-2 d-md-flex justify-content-md-center">
            <a href="${pageContext.request.contextPath}/login"
               class="btn btn-custom btn-lg text-white me-md-2 px-4">
              <i class="fas fa-sign-in-alt me-2"></i>Login
            </a>
            <a href="${pageContext.request.contextPath}/register"
               class="btn btn-outline-primary btn-lg px-4">
              <i class="fas fa-user-plus me-2"></i>Register
            </a>
          </div>

          <!-- Footer -->
          <div class="mt-4 pt-3 border-top">
            <p class="text-muted small mb-0">
              <i class="fas fa-code me-1"></i>
              Developed by <strong>Sayanduary</strong> •
              <i class="fas fa-calendar-alt me-1"></i>
              2025-07-25 16:57:47 UTC
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>