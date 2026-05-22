<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.fap.model.User" %>
<%
    // -------- Auth guard --------
    HttpSession sess = request.getSession(false);
    String ClientRole = (sess != null) ? (String) sess.getAttribute("Role") : null;

    if (sess == null || ClientRole == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED); return;
    } else if (!"Admin".equalsIgnoreCase(ClientRole)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN); return;
    }

    @SuppressWarnings("unchecked")
    List<User> students = (List<User>) request.getAttribute("students");
    if (students == null) {
        response.sendRedirect("StudentServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Editing Database - Students</title>

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
              integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
        <link rel="stylesheet" href="./Styles/styles.css">
    </head>

    <body class="d-flex flex-column min-vh-100">

        <jsp:include page="Header.jsp" />

        <main class="container my-5">

            <!-- ADD MODAL -->
            <div class="modal fade" id="addmodal" tabindex="-1" role="dialog" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Add Student</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <form action="StudentServlet" method="POST">
                                <input type="hidden" name="action" value="add">

                                <input type="text"  name="Username"  class="form-control mb-3" placeholder="Username *" required>
                                <input type="text"  name="FirstName" class="form-control mb-3" placeholder="First Name *" required>
                                <input type="text"  name="LastName"  class="form-control mb-3" placeholder="Last Name *" required>
                                <input type="email" name="Email"     class="form-control mb-3" placeholder="Email *" required>
                                <input type="password" name="Password" class="form-control mb-3" placeholder="Default password (leave blank for Password123)">

                                <button type="submit" class="btn btn-warning">Add</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <button type="button" class="btn btn-success btn-sm"
                        data-toggle="modal" data-target="#addmodal">
                    + Add Student
                </button>

                <div style="width: 300px;">
                    <input type="text" id="nameSearchInput" class="form-control form-control-sm"
                           placeholder="Search Student Name..." onkeyup="filterTableByName()">
                </div>
            </div>

            <!-- STUDENT TABLE -->
            <table class='table table-striped table-hover table-bordered my-3'>
                <thead class="table-light">
                    <tr>
                        <th>Student ID</th>
                        <th>Username</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="studentTableBody">
                    <% for (User s : students) { %>
                    <tr>
                        <td class="align-middle"><%= s.getUserId() %></td>
                        <td class="align-middle"><%= s.getUsername() %></td>
                        <td class="align-middle"><%= s.getFullName() %></td>
                        <td class="align-middle"><%= s.getEmail() == null ? "" : s.getEmail() %></td>
                        <td class="align-middle"><%= s.isActive() ? "Active" : "Inactive" %></td>
                        <td class="align-middle text-nowrap" style="width: 25%">
                            <button type="button" class="btn btn-danger btn-sm"
                                    data-toggle="modal" data-target="#dltModal<%= s.getUserId() %>">
                                Delete
                            </button>
                        </td>
                    </tr>

                    <!-- DELETE MODAL -->
                    <div class="modal fade" id="dltModal<%= s.getUserId() %>" tabindex="-1" role="dialog" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Delete Student</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    <div class="alert alert-danger text-center" role="alert">
                                        <strong>Warning!</strong> <%= s.getFullName() %> will be permanently removed.
                                    </div>
                                    <form action="StudentServlet" method="POST" class="d-flex justify-content-center">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="StudentId" value="<%= s.getUserId() %>">
                                        <button type="submit" class="btn btn-danger">Confirm Delete</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                    <% } %>
                </tbody>
            </table>

        </main>

        <jsp:include page="Footer.jsp" />

        <!-- Bootstrap scripts loaded by Header.jsp -->
        <script src="./Scripts/Search.js"></script>
    </body>
</html>
