<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Note Taker - Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid">
    <div class="row min-vh-100">
        <div class="col-12 d-flex align-items-center justify-content-center bg-light">
            <div class="text-center">
                <h1 class="display-4 mb-4">Welcome to Note Taker</h1>
                <p class="lead mb-4">Organize your thoughts and ideas with our simple note-taking application.</p>
                <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                    <a href="login" class="btn btn-primary btn-lg me-md-2">Login</a>
                    <a href="register" class="btn btn-outline-primary btn-lg">Register</a>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>