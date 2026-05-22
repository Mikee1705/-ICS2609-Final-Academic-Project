<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Servlets.User" %> 
<!--
    
    HttpSession sess = request.getSession(false);
    
    String ClientRole = (sess != null) ? (String) sess.getAttribute("Role") : null;
    String ClientName = (sess != null) ? (String) sess.getAttribute("UName") : null;
    Object status = request.getAttribute("status");

    
    if (sess == null || ClientRole == null) {
        // 401: Unauthorized / Session Expired
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return; // CRITICAL: Stop compilation immediately
    } else if (!"Admin".equalsIgnoreCase(ClientRole)) {
        // 403: Forbidden / Wrong Role
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return; // CRITICAL: Stop compilation immediately
    }
-->

<%
    // replace with actual data
    int stuCount = 30;
%>

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Editing Database - Student</title>

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
              integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

        <link rel="stylesheet" href="./Styles/styles.css">
    </head>

    <body class="d-flex flex-column min-vh-100">

        <jsp:include page="Header.jsp" />
        <main class="container my-5">
            
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

                            <form action="AddServlet" method="POST">

                                <!-- Salutation -->
                                <div class="mb-3">
                                    <select class="custom-select custom-select-m" aria-label="Select Salutation">
                                        <option selected disabled>Select Salutation</option>
                                        <option value="1">Mr.</option>
                                        <option value="2">Ms.</option>
                                        <option value="3">Mrs.</option>
                                        <option value="4">Dr.</option>
                                        <option value="5">Engr.</option>
                                    </select>
                                </div>

                                <!-- First Name -->
                                <input type="text" class="form-control form-control-m mb-3" id="FirstName" placeholder="First Name *">

                                <!-- Last Name -->
                                <input type="text" class="form-control form-control-m mb-3" id="LastName" placeholder="Last Name *">

                                <!-- Email -->
                                <input type="email" class="form-control form-control-m mb-3" id="Email" placeholder="Email *">

                                <!-- Cell Number -->
                                <input type="text" class="form-control form-control-m mb-3" id="CellNumber" value="+63 ">

                                <!-- Landline (Fixed duplicate ID to Landline) -->
                                <input type="text" class="form-control form-control-m mb-3" id="Landline" placeholder="Landline Number">

                                <!-- Funding -->
                                <div class="mb-3">
                                    <select class="custom-select custom-select-m mb-3" aria-label="Select Funding">
                                        <option selected disabled>Select Funding *</option>
                                        <option value="1">Personal</option>
                                        <option value="2">Corporate</option>
                                    </select>
                                </div>

                                <!-- Submit Button -->
                                <button type="submit" class="btn btn-warning">Add</button>

                            </form> <!-- Close form here, AFTER all elements have been declared -->
                        </div>

                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-3">

                <!-- Left side: Add Student Button -->
                <div>
                    <button type="button" 
                            class="btn btn-success btn-sm" 
                            data-toggle="modal" 
                            data-target="#addmodal">
                        + Add Student
                    </button>
                </div>

                <!-- Right side: Search Input (Fixed column constraints for a clean layout) -->
                <div style="width: 300px;">
                    <input type="text" 
                           id="nameSearchInput" 
                           class="form-control form-control-sm" 
                           placeholder="Search Student Name..." 
                           onkeyup="filterTableByName()">
                </div>

            </div>



            <table class='table table-striped table-hover table-bordered my-3'>
                <thead class="table-light">
                    <tr>
                        <th>Student ID</th>
                        <th>Student Name</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody id="studentTableBody">
                    <%
                        // Create an index to uniquely identify each row's course
                        for (int index = 1; index <= stuCount; index++) {

                    %>
                    <tr>
                        <td class="align-middle"><%= index%></td>

                        <td class="align-middle">Student <%= index%></td>

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
                                <!-- Edit with actual name-->
                                <h5 class="modal-title">Edit Student <%= index%></h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">

                                <form action="EditServlet" method="POST">

                                    <!-- Salutation -->
                                    <div class="mb-3">
                                        <select class="custom-select custom-select-m" aria-label="Select Salutation">
                                            <option selected disabled>Select Salutation</option>
                                            <option value="1">Mr.</option>
                                            <option value="2">Ms.</option>
                                            <option value="3">Mrs.</option>
                                            <option value="4">Dr.</option>
                                            <option value="5">Engr.</option>
                                        </select>
                                    </div>

                                    <!-- First Name -->
                                    <input type="text" class="form-control form-control-m mb-3" id="FirstName" placeholder="First Name *">

                                    <!-- Last Name -->
                                    <input type="text" class="form-control form-control-m mb-3" id="LastName" placeholder="Last Name *">

                                    <!-- Email -->
                                    <input type="email" class="form-control form-control-m mb-3" id="Email" placeholder="Email *">

                                    <!-- Cell Number -->
                                    <input type="text" class="form-control form-control-m mb-3" id="CellNumber" value="+63 ">

                                    <!-- Landline (Fixed duplicate ID to Landline) -->
                                    <input type="text" class="form-control form-control-m mb-3" id="Landline" placeholder="Landline Number">

                                    <!-- Funding -->
                                    <div class="mb-3">
                                        <select class="custom-select custom-select-m mb-3" aria-label="Select Funding">
                                            <option selected disabled>Select Funding *</option>
                                            <option value="1">Personal</option>
                                            <option value="2">Corporate</option>
                                        </select>
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

                                <h5 class="modal-title">Delete Student</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">

                                <div class="modal-body">
                                    <div class="alert alert-danger text-center" role="alert">
                                        <strong>Warning!</strong> This Student will be permanently removed from the system.
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

        <script src="./Scripts/Search.js"></script>


    </body>

</html>
