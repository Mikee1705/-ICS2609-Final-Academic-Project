<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.fap.model.Student" %>
<%@ page import="com.fap.model.Salutation" %>
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
    List<Student> students = (List<Student>) request.getAttribute("students");
    @SuppressWarnings("unchecked")
    List<Salutation> salutations = (List<Salutation>) request.getAttribute("salutations");
    if (students == null || salutations == null) {
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

                                <input type="text"  name="StudentId" class="form-control mb-3" placeholder="Student ID *" required>
                                <input type="text"  name="Username"  class="form-control mb-3" placeholder="Username (login) *" required>

                                <select name="SalutationId" class="form-control mb-3">
                                    <option value="">Select Salutation</option>
                                    <% for (Salutation sal : salutations) { %>
                                        <option value="<%= sal.getSalutationId() %>"><%= sal.getTitle() %></option>
                                    <% } %>
                                </select>

                                <input type="text"  name="FirstName" class="form-control mb-3" placeholder="First Name *" required>
                                <input type="text"  name="LastName"  class="form-control mb-3" placeholder="Last Name *" required>
                                <input type="email" name="Email"     class="form-control mb-3" placeholder="Email *" required>

                                <select name="Funding" class="form-control mb-3">
                                    <option value="">Select Funding</option>
                                    <option value="Self-Funded">Self-Funded</option>
                                    <option value="Scholarship">Scholarship</option>
                                    <option value="Company-Paid">Company-Paid</option>
                                </select>

                                <input type="password" name="Password" class="form-control mb-3" placeholder="Default password (blank = Password123)">

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
                        <th>Funding</th>
                        <th>Registered</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="studentTableBody">
                    <% for (Student s : students) { %>
                    <tr>
                        <td class="align-middle"><%= s.getStudentId() %></td>
                        <td class="align-middle"><%= s.getUsername() == null ? "" : s.getUsername() %></td>
                        <td class="align-middle"><%= s.getFullName() %></td>
                        <td class="align-middle"><%= s.getEmail() == null ? "" : s.getEmail() %></td>
                        <td class="align-middle"><%= s.getFunding() == null ? "" : s.getFunding() %></td>
                        <td class="align-middle"><%= s.getRegistrationDate() == null ? "" : s.getRegistrationDate() %></td>
                        <td class="align-middle text-nowrap" style="width: 12%">
                            <button type="button" class="btn btn-danger btn-sm"
                                    data-toggle="modal" data-target="#dltModal<%= s.getStudentId() %>">
                                Delete
                            </button>
                        </td>
                    </tr>

                    <!-- DELETE MODAL -->
                    <div class="modal fade" id="dltModal<%= s.getStudentId() %>" tabindex="-1" role="dialog" aria-hidden="true">
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
                                        <strong>Warning!</strong> <%= s.getFullName() %> (<%= s.getUsername() %>)
                                        will be permanently removed from both Derby (auth) and PostgreSQL (profile).
                                    </div>
                                    <form action="StudentServlet" method="POST" class="d-flex justify-content-center">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="StudentId" value="<%= s.getStudentId() %>">
                                        <input type="hidden" name="Username" value="<%= s.getUsername() == null ? "" : s.getUsername() %>">
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
