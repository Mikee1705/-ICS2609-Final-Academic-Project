<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.fap.model.Course" %>
<%
    // -------- Auth guard --------
    HttpSession sess = request.getSession(false);
    String ClientRole = (sess != null) ? (String) sess.getAttribute("Role") : null;
    String ClientName = (sess != null) ? (String) sess.getAttribute("UName") : null;

    if (sess == null || ClientRole == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED); return;
    } else if (!"Admin".equalsIgnoreCase(ClientRole)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN); return;
    }

    // -------- Data --------
    // Populated by CourseServlet. If a user lands here directly (without
    // going through the servlet) the list will be null, so we redirect.
    @SuppressWarnings("unchecked")
    List<Course> courses = (List<Course>) request.getAttribute("courses");
    if (courses == null) {
        response.sendRedirect("CourseServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Editing Database - Courses</title>

        <link rel="stylesheet"
              href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
              integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
              crossorigin="anonymous">
        <link rel="stylesheet" href="./Styles/styles.css">
    </head>

    <body class="d-flex flex-column min-vh-100">

        <jsp:include page="Header.jsp" />

        <main class="container my-5">

            <!-- ============================================================
                 ADD COURSE BUTTON + MODAL
                 ============================================================ -->
            <button type="button" class="btn btn-success btn-sm mr-1"
                    data-toggle="modal" data-target="#addmodal">
                + Add Course
            </button>

            <div class="modal fade" id="addmodal" tabindex="-1" role="dialog" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Add Course</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">
                            <form action="CourseServlet" method="POST">
                                <input type="hidden" name="action" value="add">

                                <input type="text" name="CourseCode" class="form-control mb-3" placeholder="Course Code (e.g. WD101) *" required>
                                <input type="text" name="CourseName" class="form-control mb-3" placeholder="Course Name *" required>
                                <textarea name="CourseDesc" class="form-control mb-3" rows="3" placeholder="Description"></textarea>

                                <input type="text" name="Category" class="form-control mb-3" placeholder="Category (e.g. Programming)">

                                <select name="Level" class="form-control mb-3">
                                    <option value="Beginner">Beginner</option>
                                    <option value="Intermediate">Intermediate</option>
                                    <option value="Advanced">Advanced</option>
                                </select>

                                <input type="number" name="DurationHours" class="form-control mb-3" placeholder="Duration (hours)" min="0">
                                <input type="number" name="MaxStudents" class="form-control mb-3" placeholder="Max Students" min="1" value="30">

                                <button type="submit" class="btn btn-warning">Add</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ============================================================
                 COURSE TABLE
                 ============================================================ -->
            <table class='table table-striped table-hover table-bordered my-3'>
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Category</th>
                        <th>Level</th>
                        <th>Hours</th>
                        <th>Max</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Course c : courses) { %>
                    <tr>
                        <td class="align-middle"><%= c.getCourseId() %></td>
                        <td class="align-middle"><%= c.getCourseCode() %></td>
                        <td class="align-middle"><%= c.getCourseName() %></td>
                        <td class="align-middle"><%= c.getCategory() %></td>
                        <td class="align-middle"><%= c.getLevel() %></td>
                        <td class="align-middle"><%= c.getDurationHours() %></td>
                        <td class="align-middle"><%= c.getMaxStudents() %></td>
                        <td class="align-middle"><%= c.isActive() ? "Active" : "Inactive" %></td>
                        <td class="align-middle text-nowrap">
                            <button type="button" class="btn btn-primary btn-sm mr-1"
                                    data-toggle="modal" data-target="#editModal<%= c.getCourseId() %>">
                                Edit
                            </button>
                            <button type="button" class="btn btn-danger btn-sm"
                                    data-toggle="modal" data-target="#dltModal<%= c.getCourseId() %>">
                                Delete
                            </button>
                        </td>
                    </tr>

                    <!-- EDIT MODAL -->
                    <div class="modal fade" id="editModal<%= c.getCourseId() %>" tabindex="-1" role="dialog" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Edit Course: <%= c.getCourseName() %></h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    <form action="CourseServlet" method="POST">
                                        <input type="hidden" name="action" value="edit">
                                        <input type="hidden" name="CourseId" value="<%= c.getCourseId() %>">

                                        <input type="text" name="CourseCode" class="form-control mb-3" value="<%= c.getCourseCode() %>" required>
                                        <input type="text" name="CourseName" class="form-control mb-3" value="<%= c.getCourseName() %>" required>
                                        <textarea name="CourseDesc" class="form-control mb-3" rows="3"><%= c.getDescription() == null ? "" : c.getDescription() %></textarea>

                                        <input type="text" name="Category" class="form-control mb-3" value="<%= c.getCategory() %>">

                                        <select name="Level" class="form-control mb-3">
                                            <option value="Beginner"     <%= "Beginner".equals(c.getLevel())     ? "selected" : "" %>>Beginner</option>
                                            <option value="Intermediate" <%= "Intermediate".equals(c.getLevel()) ? "selected" : "" %>>Intermediate</option>
                                            <option value="Advanced"     <%= "Advanced".equals(c.getLevel())     ? "selected" : "" %>>Advanced</option>
                                        </select>

                                        <input type="number" name="DurationHours" class="form-control mb-3" value="<%= c.getDurationHours() %>">
                                        <input type="number" name="MaxStudents" class="form-control mb-3" value="<%= c.getMaxStudents() %>">

                                        <button type="submit" class="btn btn-warning">Save Changes</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- DELETE MODAL -->
                    <div class="modal fade" id="dltModal<%= c.getCourseId() %>" tabindex="-1" role="dialog" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Delete Course</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    <div class="alert alert-danger text-center" role="alert">
                                        <strong>Warning!</strong> <%= c.getCourseName() %> will be permanently removed.
                                    </div>
                                    <form action="CourseServlet" method="POST" class="d-flex justify-content-center">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="CourseId" value="<%= c.getCourseId() %>">
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

        <!-- Scripts loaded by Header.jsp -->
    </body>
</html>
