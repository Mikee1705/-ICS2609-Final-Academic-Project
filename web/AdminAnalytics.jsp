<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
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

    // -------- Real data from AnalyticsServlet --------
    @SuppressWarnings("unchecked")
    List<String>  courseNames     = (List<String>)  request.getAttribute("courseNames");
    @SuppressWarnings("unchecked")
    List<Integer> enrolledCounts  = (List<Integer>) request.getAttribute("enrolledCounts");
    @SuppressWarnings("unchecked")
    List<Double>  avgCourseRatings = (List<Double>) request.getAttribute("avgCourseRatings");
    @SuppressWarnings("unchecked")
    List<Double>  completionRates = (List<Double>) request.getAttribute("completionRates");

    // If user reached the JSP directly (not via /Analytics), redirect
    if (courseNames == null) {
        response.sendRedirect("Analytics");
        return;
    }
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
        <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js"></script>
    </head>

    <body>
        <jsp:include page="Header.jsp" />

        <div class="container d-flex flex-column align-items-center my-2">

            <h2 class="mt-4">Welcome, <%= ClientName %></h2>

            <canvas id="Stud_Chart"      style="width: 100%; max-width: 600px;  height: 500px;"></canvas>
            <hr style="height: 2px; border: none; background-color: #333;">
            <canvas id="Ratings_Chart"   style="width: 100%; max-width: 1000px; height: 500px;"></canvas>
            <hr style="height: 2px; border: none; background-color: #333;">
            <canvas id="Completion_Chart" style="width: 100%; max-width: 600px; max-height: 500px;"></canvas>

            <p class="mt-4">Data as of: <%= now %></p>

            <div class="container text-center mt-3">
                <h2>Download Reports</h2>

                <div class="d-flex flex-wrap justify-content-center mb-3">
                    <a href="report/users"               class="btn btn-warning mx-2 my-1">User List</a>
                    <a href="report/courses"             class="btn btn-warning mx-2 my-1">Course List</a>
                    <a href="report/enrollments?scope=all" class="btn btn-warning mx-2 my-1">Enrollments</a>
                    <a href="report/teacher-assignments?scope=all" class="btn btn-warning mx-2 my-1">Teacher Assignments</a>
                    <a href="report/ratings?scope=all"   class="btn btn-warning mx-2 my-1">Course Ratings</a>
                </div>

                <hr style="height: 2px; border: none; background-color: #333;">

                <div class="d-flex justify-content-center w-100">
                    <a href="Logout" class="btn btn-danger">Log Out</a>
                </div>
            </div>
        </div>

        <jsp:include page="Footer.jsp" />

        <!-- ============================================================
             CHART DATA — injected by AnalyticsServlet
             ============================================================ -->
        <script>
            var xValues = [<%
                for (int i = 0; i < courseNames.size(); i++) {
                    out.print("\"" + courseNames.get(i).replace("\"", "\\\"") + "\"");
                    if (i < courseNames.size() - 1) out.print(",");
                }
            %>];

            var enrollData = [<%
                for (int i = 0; i < enrolledCounts.size(); i++) {
                    out.print(enrolledCounts.get(i));
                    if (i < enrolledCounts.size() - 1) out.print(",");
                }
            %>];

            var courseRatingData = [<%
                for (int i = 0; i < avgCourseRatings.size(); i++) {
                    out.print(avgCourseRatings.get(i));
                    if (i < avgCourseRatings.size() - 1) out.print(",");
                }
            %>];

            var rateData = [<%
                for (int i = 0; i < completionRates.size(); i++) {
                    out.print(completionRates.get(i));
                    if (i < completionRates.size() - 1) out.print(",");
                }
            %>];

            // Auto-generated palette
            function randomPalette(n) {
                var palette = ["#FF5733","#33FF57","#3357FF","#F3FF33","#FF33F3","#33FFF0",
                               "#FFA833","#AF33FF","#33FF8F","#FF3333","#3380FF","#8FFF33"];
                return palette.slice(0, n);
            }
            var barColors = randomPalette(xValues.length);
        </script>

        <!-- STUDENT ENROLLMENT BAR CHART -->
        <script>
            new Chart("Stud_Chart", {
                type: "bar",
                data: {
                    labels: xValues,
                    datasets: [{ backgroundColor: barColors, data: enrollData }]
                },
                options: {
                    legend: { display: false },
                    title:  { display: true, text: "Student Enrollment by Course" },
                    scales: { yAxes: [{ ticks: { beginAtZero: true } }] }
                }
            });
        </script>

        <!-- COURSE vs TEACHER RATINGS (Course only for now — teacher ratings deferred) -->
        <script>
            new Chart("Ratings_Chart", {
                type: 'bar',
                data: {
                    labels: xValues,
                    datasets: [{
                        label: 'Avg Course Rating',
                        data: courseRatingData,
                        backgroundColor: 'rgba(54, 162, 235, 0.8)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    title:  { display: true, text: 'Average Course Rating', fontSize: 16 },
                    legend: { display: true, position: 'bottom' },
                    scales: {
                        xAxes: [{ stacked: false, gridLines: { display: false } }],
                        yAxes: [{ stacked: false, ticks: { beginAtZero: true, max: 5, stepSize: 1 } }]
                    }
                }
            });
        </script>

        <!-- ENROLLMENT VOLUME vs COMPLETION RATE -->
        <script>
            new Chart("Completion_Chart", {
                type: 'bar',
                data: {
                    labels: xValues,
                    datasets: [
                        {
                            label: 'Completion Rate (%)',
                            type: 'line',
                            data: rateData,
                            fill: false,
                            borderColor: '#FF5733',
                            backgroundColor: '#FF5733',
                            borderWidth: 3,
                            yAxisID: 'y-axis-rate'
                        },
                        {
                            label: 'Students Enrolled',
                            data: enrollData,
                            backgroundColor: 'rgba(153, 102, 255, 0.7)',
                            borderColor: 'rgba(153, 102, 255, 1)',
                            borderWidth: 1,
                            yAxisID: 'y-axis-enroll'
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    title:  { display: true, text: 'Course Enrollment Volume vs. Completion Rates', fontSize: 16 },
                    legend: { display: true, position: 'bottom' },
                    scales: {
                        xAxes: [{ gridLines: { display: false } }],
                        yAxes: [
                            {
                                id: 'y-axis-enroll', type: 'linear', position: 'left',
                                ticks: { beginAtZero: true, suggestedMax: 500 },
                                scaleLabel: { display: true, labelString: 'Total Students Registered' }
                            },
                            {
                                id: 'y-axis-rate', type: 'linear', position: 'right',
                                gridLines: { display: false },
                                ticks: { beginAtZero: true, max: 100, stepSize: 20 },
                                scaleLabel: { display: true, labelString: 'Completion Success (%)' }
                            }
                        ]
                    }
                }
            });
        </script>

        <!-- jQuery / Popper / Bootstrap are loaded once in Header.jsp — no need to repeat here -->
    </body>
</html>
