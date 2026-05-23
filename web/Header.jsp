<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Read the session safely — null on the login page
    HttpSession sessHdr = request.getSession(false);
    String hdrRole = (sessHdr != null) ? (String) sessHdr.getAttribute("Role") : null;
    boolean loggedIn = (hdrRole != null);
%>

<!-- Bootstrap 4 CSS -->
<link rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
      integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
      crossorigin="anonymous">

<!-- Font Awesome -->
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<!-- Project styles -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/Styles/styles.css">

<!-- =============================================================
     Navbar dropdown fix — force the menu to overlay content below
     instead of pushing the navbar taller.
     ============================================================= -->
<style>
    /* Keep the navbar a fixed height regardless of dropdown state */
    .navbar { overflow: visible; }

    /* The <li.dropdown> must be the positioning anchor */
    .navbar .nav-item.dropdown { position: relative; }

    /* Float the dropdown menu over page content */
    .navbar .dropdown-menu {
        position: absolute !important;
        top: 100%;
        right: 0;
        left: 0;
        margin-top: .25rem;
        z-index: 1050;          /* above modals, below tooltips */
        min-width: 100%;        /* match the Edit Database button width */
        width: 100%;
        padding: .25rem 0;
    }

    /* Keep dropdown items on one line + tighten padding so they still fit */
    .navbar .dropdown-menu .dropdown-item {
        white-space: nowrap;
        padding: .4rem .75rem;
        font-size: .9rem;
    }
</style>


<nav class="navbar navbar-dark bg-dark w-100 px-0 px-md-5" style="background-color: #000000;">
    <div class="container d-flex flex-wrap flex-md-nowrap align-items-center justify-content-between px-3 px-md-0">

        <!-- Logo -->
        <a href="#">
            <img class="navbar-brand m-0"
                 src="${pageContext.request.contextPath}/Assets/logo-white.webp"
                 alt="Logo" style="height: 83px; width: auto;">
        </a>

        <!-- Navigation -->
        <div class="position-relative mt-2 mt-md-0" style="z-index: 2;">
            <ul class="navbar-nav ml-auto flex-row align-items-center">

                <% if (!loggedIn) { %>
                    <li class="nav-item active px-2 px-md-4">
                        <a class="nav-link text-muted" href="index.jsp">Sign In</a>
                    </li>
                <% } else { %>
                    <li class="nav-item active px-2 px-md-4">
                        <a class="nav-link text-white" href="Analytics">Company Analytics</a>
                    </li>

                    <li class="nav-item dropdown">
                        <a class="btn btn-warning dropdown-toggle" href="#" role="button"
                           id="navEditDb" data-toggle="dropdown"
                           aria-haspopup="true" aria-expanded="false">
                            Edit Database
                        </a>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navEditDb">
                            <a class="dropdown-item" href="InstructorServlet">
                                <i class="fa-solid fa-chalkboard-user mr-2"></i>Instructors
                            </a>
                            <a class="dropdown-item" href="StudentServlet">
                                <i class="fa-solid fa-user-graduate mr-2"></i>Students
                            </a>
                            <a class="dropdown-item" href="CourseServlet">
                                <i class="fa-solid fa-book mr-2"></i>Courses
                            </a>
                        </div>
                    </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>

<!-- Scripts: jQuery → Popper → Bootstrap. Loaded ONCE here for every page. -->
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
        integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>
