<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="Servlets.User" %> 
<%
    HttpSession sess = request.getSession(false);
    Object ClientRole = session.getAttribute("Role");
    Object status = request.getAttribute("status");
    String ClientName = (String) session.getAttribute("UName");

    //Role Security
    if (sess == null || ClientRole == null) {
        // 401: You don't know who they are (Session Expired)
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    } else if (!"Admin".equalsIgnoreCase((String) ClientRole)) {
        // 403: You know them, but they aren't allowed here (Wrong Role)
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
%>

<%
    // Replace with database data array
    // Chart 1 Data: Student Counts expanded to 12 items
    int[] StuCount = {15, 22, 18, 30, 45, 12, 28, 55, 20, 16, 25, 38};

    // Dynamic palette expanded to 12 matching colors
    String[] Colors = {
        "#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3", "#33FFF0",
        "#FFA833", "#AF33FF", "#33FF8F", "#FF3333", "#3380FF", "#8FFF33"
    };
    
    // Up to 12 Courses
    String[] Courses = {
        "AI Automation", "ITIL", "Cybersecurity", "CompTIA",
        "Python", "Project Management", "Data Analysis", "Excel",
        "Azure", "UX", "Java", "WebDev"
    };

    // Average Course Ratings (out of 5.0)
    double[] AvgCourseRatings = {4.5, 3.8, 4.2, 4.7, 4.0, 3.9, 4.6, 4.1, 4.3, 4.8, 3.7, 4.2};

    // Average Teacher Ratings (out of 5.0)
    double[] AvgTeacherRatings = {4.7, 4.1, 4.0, 4.5, 4.2, 3.8, 4.8, 4.3, 4.1, 4.9, 4.0, 4.4};

    int[] EnrolledCount = {210, 85, 145, 190, 320, 110, 240, 410, 165, 130, 180, 290};
    double[] CompletionRates = {91.2, 82.5, 76.0, 88.7, 64.2, 89.0, 71.4, 94.8, 78.3, 85.0, 68.9, 73.5};
%>


<jsp:useBean id="now" class="java.util.Date" />
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Analytics</title>
        <link rel="stylesheet" href="./Styles/Modal.css">
        <link rel="stylesheet" href="./Styles/styles.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
              integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
        <!-- Using Chart.js v2.5.0 as specified in your header -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js"></script>
    </head>

    <body>
        <jsp:include page="Header.jsp" />

        <div class="container d-flex flex-column align-items-center my-2">

            <canvas id="Stud_Chart" style="width: 100%; max-width: 600px; height: 500px; margin: 0;"></canvas>
            <hr style="height: 2px; border: none; background-color: #333;">
            <canvas id="Ratings_Chart" style="width: 100%; max-width: 1000px; height: 500px; margin: 0;"></canvas>
            <hr style="height: 2px; border: none; background-color: #333;">
            <canvas id="Completion_Chart" style="width: 100%; max-width: 600px; max-height: 500px; margin: 0;"></canvas>


            <p class="mt-4">Data as of: <%= now%></p>

            <div class="container text-center mt-3">
                <h2>Download Data and Analytics as PDF</h2>

                <div class="px-3 d-flex flex-row justify-content-center mb-3">
                    <button type="submit" class="btn btn-warning mx-3">Company Data</button>
                    <button type="submit" class="btn btn-warning mx-3">User Data</button>
                </div>
                <hr style="height: 2px; border: none; background-color: #333;">

                <div class="d-flex justify-content-center w-100">
                    <a href="logout.jsp" class="btn btn-danger">Log Out</a>
                </div>
            </div>
        </div>

        <jsp:include page="Footer.jsp" />

        <!-- FOR STUDENT CHART -->
        <script>
            var xValues = [
            <% for (int i = 0; i < Courses.length; i++) {%>
            "<%= Courses[i]%>"<%= (i < Courses.length - 1) ? "," : ""%>
            <% } %>
            ];
            var yValues = [
            <% for (int i = 0; i < StuCount.length; i++) {%>
            <%= StuCount[i]%><%= (i < StuCount.length - 1) ? "," : ""%>
            <% } %>
            ];
            var barColors = [
            <% for (int i = 0; i < Colors.length; i++) {%>
            "<%= Colors[i]%>"<%= (i < Colors.length - 1) ? "," : ""%>
            <% }%>
            ];

            new Chart("Stud_Chart", {
                type: "bar",
                data: {
                    labels: xValues,
                    datasets: [
                        {
                            backgroundColor: barColors,
                            data: yValues
                        }
                    ]
                },
                options: {
                    legend: {display: false},
                    title: {
                        display: true,
                        text: "Student Enrollment by Course",
                    },
                    scales: {
                        yAxes: [
                            {
                                ticks: {
                                    beginAtZero: true,
                                }
                            }
                        ]
                    }
                }
            });
        </script>

        <!-- FOR RATINGS CHART -->
        <script>
            // 1. Convert JSP Java arrays into JavaScript arrays
            var ratingLabels = [
            <% for (int i = 0; i < Courses.length; i++) {%>
            "<%= Courses[i]%>"<%= (i < Courses.length - 1) ? "," : ""%>
            <% } %>
            ];

            var courseRatingData = [
            <% for (int i = 0; i < AvgCourseRatings.length; i++) {%>
            <%= AvgCourseRatings[i]%><%= (i < AvgCourseRatings.length - 1) ? "," : ""%>
            <% } %>
            ];

            var teacherRatingData = [
            <% for (int i = 0; i < AvgTeacherRatings.length; i++) {%>
            <%= AvgTeacherRatings[i]%><%= (i < AvgTeacherRatings.length - 1) ? "," : ""%>
            <% }%>
            ];

            // 2. Initialize side-by-side clustered bar chart in Chart.js v2
            new Chart("Ratings_Chart", {
                type: 'bar',
                data: {
                    labels: ratingLabels,
                    datasets: [
                        {
                            label: 'Avg Course Rating',
                            data: courseRatingData,
                            backgroundColor: 'rgba(54, 162, 235, 0.8)', // Solid Blue
                            borderColor: 'rgba(54, 162, 235, 1)',
                            borderWidth: 1
                        },
                        {
                            label: 'Avg Teacher Rating',
                            data: teacherRatingData,
                            backgroundColor: 'rgba(75, 192, 192, 0.8)', // Solid Teal Green
                            borderColor: 'rgba(75, 192, 192, 1)',
                            borderWidth: 1
                        }
                    ]
                },
                options: {
                    responsive: true,
                    title: {
                        display: true,
                        text: 'Course vs. Teacher Performance Evaluation',
                        fontSize: 16
                    },
                    legend: {
                        display: true,
                        position: 'bottom'
                    },
                    scales: {
                        xAxes: [{
                                stacked: false, // Ensures side-by-side rendering
                                gridLines: {
                                    display: false // Cleans up layout for 12 items
                                }
                            }],
                        yAxes: [{
                                stacked: false,
                                ticks: {
                                    beginAtZero: true,
                                    max: 5, // Forces standard academic 5-star rating cap
                                    stepSize: 1
                                }
                            }]
                    }
                }
            });
        </script>

        <!-- FOR COMPLETION CHART -->
        <script>
            // 1. Convert the synchronized 12-item arrays into JavaScript
            var compLabels = [
            <% for (int i = 0; i < Courses.length; i++) {%>
            "<%= Courses[i]%>"<%= (i < Courses.length - 1) ? "," : ""%>
            <% } %>
            ];

            var enrollData = [
            <% for (int i = 0; i < EnrolledCount.length; i++) {%>
            <%= EnrolledCount[i]%><%= (i < EnrolledCount.length - 1) ? "," : ""%>
            <% } %>
            ];

            var rateData = [
            <% for (int i = 0; i < CompletionRates.length; i++) {%>
            <%= CompletionRates[i]%><%= (i < CompletionRates.length - 1) ? "," : ""%>
            <% }%>
            ];

            // 2. Initialize Mixed Chart with Dual Axes
            new Chart("Completion_Chart", {
                type: 'bar',
                data: {
                    labels: compLabels,
                    datasets: [
                        {
                            label: 'Completion Rate (%)',
                            type: 'line',
                            data: rateData,
                            fill: false,
                            borderColor: '#FF5733', // Crimson Red Line
                            backgroundColor: '#FF5733',
                            borderWidth: 3,
                            yAxisID: 'y-axis-rate'
                        },
                        {
                            label: 'Students Enrolled',
                            data: enrollData,
                            backgroundColor: 'rgba(153, 102, 255, 0.7)', // Purple Bar
                            borderColor: 'rgba(153, 102, 255, 1)',
                            borderWidth: 1,
                            yAxisID: 'y-axis-enroll'
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    layout: {
                        padding: {top: 10, bottom: 0}
                    },
                    title: {
                        display: true,
                        text: 'Course Enrollment Volume vs. Completion Rates',
                        fontSize: 16,
                        padding: 6
                    },
                    legend: {
                        display: true,
                        position: 'bottom'
                    },
                    scales: {
                        xAxes: [{
                                gridLines: {display: false} // Hides clutter behind the 12 columns
                            }],
                        yAxes: [
                            {
                                id: 'y-axis-enroll',
                                type: 'linear',
                                position: 'left',
                                ticks: {
                                    beginAtZero: true,
                                    suggestedMax: 500 // Adjusted slightly above our highest enrollment (Excel: 410)
                                },
                                scaleLabel: {
                                    display: true,
                                    labelString: 'Total Students Registered'
                                }
                            },
                            {
                                id: 'y-axis-rate',
                                type: 'linear',
                                position: 'right',
                                gridLines: {display: false},
                                ticks: {
                                    beginAtZero: true,
                                    max: 100,
                                    stepSize: 20
                                },
                                scaleLabel: {
                                    display: true,
                                    labelString: 'Completion Success (%)'
                                }
                            }
                        ]
                    }
                }
            });
        </script>


        <!-- Fixed Script Loading Order: jQuery must load before Popper and Bootstrap -->
        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
                integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
        crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
                integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
        crossorigin="anonymous"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
                integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
        crossorigin="anonymous"></script>
    </body>
</html>
