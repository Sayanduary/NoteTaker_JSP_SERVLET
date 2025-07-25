<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Dashboard - Note Taker</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" rel="stylesheet">
  <style>
    .note-card {
      transition: transform 0.2s ease-in-out;
      border-left: 4px solid #007bff;
    }
    .note-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }
    .dashboard-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 2rem 0;
      margin-bottom: 2rem;
    }
  </style>
</head>
<body class="bg-light">
<!-- Dashboard Header -->
<div class="dashboard-header">
  <div class="container">
    <div class="row align-items-center">
      <div class="col-md-8">
        <h1 class="mb-0">
          <i class="fas fa-tachometer-alt me-2"></i>
          Welcome, ${user.username}!
        </h1>
        <p class="mb-0 opacity-75">
          <i class="fas fa-sticky-note me-1"></i>
          You have ${noteCount} note(s)
        </p>
      </div>
      <div class="col-md-4 text-end">
        <button type="button" class="btn btn-light me-2" data-bs-toggle="modal" data-bs-target="#createNoteModal">
          <i class="fas fa-plus me-1"></i> New Note
        </button>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">
          <i class="fas fa-sign-out-alt me-1"></i> Logout
        </a>
      </div>
    </div>
  </div>
</div>

<div class="container">
  <!-- Success/Error Messages -->
  <c:if test="${param.success != null}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <i class="fas fa-check-circle me-2"></i>
        ${param.success}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <c:if test="${param.error != null}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <i class="fas fa-exclamation-circle me-2"></i>
        ${param.error}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <!-- Notes Grid -->
  <div class="row">
    <c:choose>
      <c:when test="${empty notes}">
        <div class="col-12">
          <div class="text-center py-5">
            <i class="fas fa-sticky-note fa-5x text-muted mb-3"></i>
            <h3 class="text-muted">No notes yet</h3>
            <p class="text-muted">Create your first note to get started!</p>
            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createNoteModal">
              <i class="fas fa-plus me-1"></i> Create Your First Note
            </button>
          </div>
        </div>
      </c:when>
      <c:otherwise>
        <c:forEach var="note" items="${notes}">
          <div class="col-md-6 col-lg-4 mb-4">
            <div class="card note-card h-100">
              <div class="card-body">
                <h5 class="card-title">
                  <i class="fas fa-sticky-note me-2 text-primary"></i>
                    ${note.title}
                </h5>
                <p class="card-text">
                    ${note.content.length() > 100 ? note.content.substring(0, 100).concat('...') : note.content}
                </p>
                <div class="d-flex justify-content-between align-items-center">
                  <small class="text-muted">
                    <i class="fas fa-clock me-1"></i>
                    <fmt:formatDate value="${note.updatedAt}" pattern="MMM dd, yyyy HH:mm"/>
                  </small>
                  <div class="btn-group" role="group">
                    <button type="button" class="btn btn-sm btn-outline-primary"
                            onclick="editNote(${note.id}, '${note.title}', '${note.content}')">
                      <i class="fas fa-edit"></i>
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger"
                            onclick="deleteNote(${note.id}, '${note.title}')">
                      <i class="fas fa-trash"></i>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </c:forEach>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<!-- Create Note Modal -->
<div class="modal fade" id="createNoteModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="${pageContext.request.contextPath}/dashboard" method="post">
        <input type="hidden" name="action" value="create">
        <div class="modal-header">
          <h5 class="modal-title">
            <i class="fas fa-plus me-2"></i>Create New Note
          </h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label for="createTitle" class="form-label">Title</label>
            <input type="text" class="form-control" id="createTitle" name="title" required>
          </div>
          <div class="mb-3">
            <label for="createContent" class="form-label">Content</label>
            <textarea class="form-control" id="createContent" name="content" rows="5" required></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">
            <i class="fas fa-save me-1"></i>Create Note
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Edit Note Modal -->
<div class="modal fade" id="editNoteModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="${pageContext.request.contextPath}/dashboard" method="post">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="noteId" id="editNoteId">
        <div class="modal-header">
          <h5 class="modal-title">
            <i class="fas fa-edit me-2"></i>Edit Note
          </h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label for="editTitle" class="form-label">Title</label>
            <input type="text" class="form-control" id="editTitle" name="title" required>
          </div>
          <div class="mb-3">
            <label for="editContent" class="form-label">Content</label>
            <textarea class="form-control" id="editContent" name="content" rows="5" required></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">
            <i class="fas fa-save me-1"></i>Update Note
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteNoteModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="${pageContext.request.contextPath}/dashboard" method="post">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="noteId" id="deleteNoteId">
        <div class="modal-header">
          <h5 class="modal-title">
            <i class="fas fa-trash me-2"></i>Delete Note
          </h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <p>Are you sure you want to delete the note "<span id="deleteNoteTitle"></span>"?</p>
          <p class="text-muted">This action cannot be undone.</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-danger">
            <i class="fas fa-trash me-1"></i>Delete Note
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
  function editNote(id, title, content) {
    document.getElementById('editNoteId').value = id;
    document.getElementById('editTitle').value = title;
    document.getElementById('editContent').value = content;
    new bootstrap.Modal(document.getElementById('editNoteModal')).show();
  }

  function deleteNote(id, title) {
    document.getElementById('deleteNoteId').value = id;
    document.getElementById('deleteNoteTitle').textContent = title;
    new bootstrap.Modal(document.getElementById('deleteNoteModal')).show();
  }

  // Clear form when create modal is hidden
  document.getElementById('createNoteModal').addEventListener('hidden.bs.modal', function () {
    document.getElementById('createTitle').value = '';
    document.getElementById('createContent').value = '';
  });
</script>
</body>
</html>