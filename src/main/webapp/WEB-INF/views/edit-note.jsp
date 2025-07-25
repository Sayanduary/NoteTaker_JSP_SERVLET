<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Edit Note - Note Taker</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
  <div class="container">
    <a class="navbar-brand" href="dashboard">
      <i class="bi bi-journal-text"></i> Note Taker
    </a>
    <div class="navbar-nav ms-auto">
      <span class="navbar-text me-3">Welcome, ${user.username}!</span>
      <a class="nav-link" href="logout">
        <i class="bi bi-box-arrow-right"></i> Logout
      </a>
    </div>
  </div>
</nav>

<div class="container mt-4">
  <div class="row justify-content-center">
    <div class="col-md-8">
      <div class="card">
        <div class="card-header">
          <h3><i class="bi bi-pencil"></i> Edit Note</h3>
        </div>
        <div class="card-body">
          <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                ${error}
            </div>
          </c:if>
          <form action="note" method="post">
            <input type="hidden" name="noteId" value="${note.id}">
            <div class="mb-3">
              <label for="title" class="form-label">Title *</label>
              <input type="text" class="form-control" id="title" name="title"
                     value="${note.title}" placeholder="Enter note title" required>
            </div>
            <div class="mb-3">
              <label for="content" class="form-label">Content</label>
              <textarea class="form-control" id="content" name="content" rows="10"
                        placeholder="Write your note content here...">${note.content}</textarea>
            </div>
            <div class="d-flex gap-2">
              <button type="submit" class="btn btn-primary">
                <i class="bi bi-save"></i> Update Note
              </button>
              <a href="dashboard" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to Dashboard
              </a>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>