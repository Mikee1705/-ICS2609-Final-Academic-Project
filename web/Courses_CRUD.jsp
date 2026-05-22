<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Servlets.User" %> 

<%
    // Up to 12 Courses, replace with actual data
    String[] Courses = {
        "AI Automation", "ITIL", "Cybersecurity", "CompTIA",
        "Python", "Project Management", "Data Analysis", "Microsoft Excel",
        "Microsoft Azure", "UX", "Java", "Web Development"
    };


%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Editing Database - Courses</title>

        <!-- Replace your current bootstrap.min.css line with this clean CDN link -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/bootstrap/css/bootstrap.min.css">

        <link rel="stylesheet" href="./Styles/styles.css">
    </head>

    <body class="d-flex flex-column min-vh-100">

        <jsp:include page="Header.jsp" />

        <main class="container my-5">
            <button type="button" 
                    class="btn btn-success btn-sm mr-1" 
                    data-toggle="modal" 
                    data-target="#addmodal">
                + Add Course
            </button>
            <div class="modal fade" id="addmodal" tabindex="-1" role="dialog" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content">
                        <div class="modal-header">

                            <h5 class="modal-title">Add Course:</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body">

                            <form action="AddServlet" method="POST">

                                <div class="form-group mb-3">
                                    <input type="text" class="form-control" id="CourseName" name="CourseName" placeholder="Course Name *">
                                </div>

                                <div class="form-group mb-3">
                                    <textarea class="form-control" 
                                              id="CourseDesc" 
                                              name="CourseDesc" 
                                              rows="4" 
                                              placeholder="Course Description *" 
                                              required></textarea>
                                </div>

                                <!-- Submit Button inside the form structure -->
                                <button type="submit" class="btn btn-warning">Add</button>

                            </form> <!-- Close form here, AFTER all elements have been declared -->
                        </div>

                    </div>
                </div>
            </div>
            <table class='table table-striped table-hover table-bordered my-3'>
                <thead class="table-light">
                    <tr>
                        <th>Courses</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <%        int index = 0; // Create an index to uniquely identify each row's course
                        for (String c : Courses) {
                            index++;
                    %>
                    <tr>
                        <td class="align-middle"><%= c%></td>
                        <td class="align-middle text-nowrap" style="width: 25%">
                            <button type="button" class="btn btn-warning btn-sm mr-1">
                                View
                            </button>
                            <!-- Bootstrap 4 native triggers: data-toggle and dynamic data-target -->
                            <button type="button" 
                                    class="btn btn-primary btn-sm mr-1" 
                                    data-toggle="modal" 
                                    data-target="#editModal<%= index%>">
                                Edit
                            </button>
                            <button type="button" 
                                    class="btn btn-danger btn-sm" 
                                    data-toggle="modal" 
                                    data-target="#dltModal<%= index%>">
                                Delete
                            </button>
                        </td>
                    </tr>

                    <!-- EDIT MODAL -->
                <div class="modal fade" id="editModal<%= index%>" tabindex="-1" role="dialog" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <!-- Dynamic title based on the row's course item -->
                                <h5 class="modal-title">Edit Course: <%= c%></h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">

                                <form action="EditServlet" method="POST">

                                    <div class="form-group mb-3">
                                        <input type="text" class="form-control" id="CourseName" name="CourseName" placeholder="Course Name *">
                                    </div>

                                    <div class="form-group mb-3">
                                        <textarea class="form-control" 
                                                  id="CourseDesc" 
                                                  name="CourseDesc" 
                                                  rows="4" 
                                                  placeholder="Course Description *" 
                                                  required></textarea>
                                    </div>

                                    <!-- Submit Button inside the form structure -->
                                    <button type="submit" class="btn btn-warning">Modify</button>

                                </form>
                            </div>

                        </div>
                    </div>
                </div>


                <!-- DELETE MODAL -->
                <div class="modal fade" id="dltModal<%= index%>" tabindex="-1" role="dialog" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered" role="document">
                        <div class="modal-content">
                            <div class="modal-header">

                                <h5 class="modal-title">Delete Course:</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">

                                <div class="modal-body">
                                    <div class="alert alert-danger text-center" role="alert">
                                        <strong>Warning!</strong> This course will be permanently removed from the system.
                                    </div>

                                    <form action="DeleteServlet" method="POST" class="d-flex justify-content-center">
                                        <button type="submit" class="btn btn-danger">Confirm Delete</button>
                                    </form> 
                                </div>
                            </div>

                        </div>
                    </div>
                    <%}%>
                    </tbody>

            </table>

        </main>

        <jsp:include page="Footer.jsp" />


        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
                integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>
        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
                integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
        crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
                integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>

    </body>

</html>
