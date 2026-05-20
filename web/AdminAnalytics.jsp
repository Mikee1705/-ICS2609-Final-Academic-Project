<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String[] Courses = {"Course1", "Course2", "Course3", "Course4", "Course5"};
    int[] StuCount = {5, 6, 7, 8, 9};
    String[] Colors = {"#FF5733", "#33FF57", "#3357FF", "#F3FF33", "#FF33F3"};
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

        <div class="container d-flex flex-column align-items-center">
            <canvas id="myChart" style="width: 100%; max-width: 600px;"></canvas>
            <p class="mt-2">Data as of: </p>

            <div class="container">
                <h2>Download Data and Analytics as PDF</h2>
                <div class="px-3 d-flex flex-row justify-content-center">
                    <button type="submit" class="btn btn-warning mx-5">Company Data</button>
                    <button type="submit" class="btn btn-warning mx-5">User Data</button>
                </div>

            </div>
        </div>

        <jsp:include page="Footer.jsp" />

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

            new Chart("myChart", {
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
