<head>
    <link rel="stylesheet" href="https://cloudflare.com">
    <link rel="stylesheet" href="./bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="./Styles/styles.css">


</head>

<nav class="navbar navbar-dark bg-dark w-100 px-0 px-md-5" style="background-color: #000000;"> 
    <!-- Added responsive flex-wrap and inner padding utilities for mobile viewing -->
    <div class="container d-flex flex-wrap flex-md-nowrap align-items-center justify-content-between px-3 px-md-0">
        <!-- Logo -->
        <a href="#"> 
            <img class="navbar-brand m-0" src="./Assets/logo-white.webp" alt="Logo"
                 style="height: 83px; width: auto;"> 
        </a> 

        <!-- Navigation List -->
        <div class="position-relative mt-2 mt-md-0" style="z-index: 2;">
            <ul class="navbar-nav ml-auto flex-row align-items-center">
                <li class="nav-item active px-2 px-md-4">
                    <a class="nav-link" href="AdminAnalytics.jsp">Company Analytics</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="btn btn-warning dropdown-toggle" href="#" role="button" data-toggle="dropdown" aria-expanded="false">
                        Edit Database
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" style="position: absolute;">
                        <a class="dropdown-item" href="Instructors_CRUD.jsp">
                            <i class="fa-solid fa-chalkboard-user mr-2"></i>Instructors
                        </a> 
                        <a class="dropdown-item"  href="Students_CRUD.jsp">
                            <i class="fa-solid fa-user-graduate mr-2"></i>Students
                        </a>
                        <a class="dropdown-item"  href="Courses_CRUD.jsp">
                            <i class="fa-solid fa-book mr-2"></i>Courses
                        </a>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</nav>




<!-- FIX: Scripts reordered. jQuery first, then Popper, then Bootstrap -->
<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
        integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
        integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
        integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
crossorigin="anonymous"></script>
