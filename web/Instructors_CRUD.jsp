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
    List<User> teachers = (List<User>) request.getAttribute("teachers");
    if (teachers == null) {
        response.sendRedirect("InstructorServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Editing Database - Instructors</title>
        <link rel="stylesheet"
              href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
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
                            <h5 class="modal-title">Add Instructor</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <form action="InstructorServlet" method="POST">
                                <input type="hidden" name="action" value="add">

                                <input type="text"     name="Username" class="form-control mb-3" placeholder="Username *" required>
                                <input type="password" name="Password" class="form-control mb-3" placeholder="Password (blank = Password123)">

                                <button type="submit" class="btn btn-warning">Add</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">
                <button type="button" class="btn btn-success btn-sm"
                        data-toggle="modal" data-target="#addmodal">
                    + Add Instructor
                </button>

                <div style="width: 300px;">
                    <input type="text" id="nameSearchInput" class="form-control form-control-sm"
                           placeholder="Search Instructor Name..." onkeyup="filterTableByName()">
                </div>
            </div>

            <table class='table table-striped table-hover table-bordered my-3'>
                <thead class="table-light">
                    <tr>
                        <th>#</th>
                        <th>Username</th>
                        <th>Role</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="TableBody">
                    <% int row = 0; for (User t : teachers) { row++; %>
                    <tr>
                        <td class="align-middle"><%= row %></td>
                        <td class="align-middle"><%= t.getUsername() %></td>
                        <td class="align-middle"><%= t.getRole() %></td>
                        <td class="align-middle text-nowrap" style="width: 20%">
                            <button type="button" class="btn btn-danger btn-sm"
                                    data-toggle="modal" data-target="#dltModal<%= row %>">
                                Delete
                            </button>
                        </td>
                    </tr>

                    <!-- DELETE MODAL -->
                    <div class="modal fade" id="dltModal<%= row %>" tabindex="-1" role="dialog" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Delete Instructor</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    <div class="alert alert-danger text-center" role="alert">
                                        <strong>Warning!</strong> <%= t.getUsername() %> will be permanently removed.
                                    </div>
                                    <form action="InstructorServlet" method="POST" class="d-flex justify-content-center">
                                        <input type="hidden" name="action"   value="delete">
                                        <input type="hidden" name="Username" value="<%= t.getUsername() %>">
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
