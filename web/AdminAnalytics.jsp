<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Analytics</title>
        <link rel="stylesheet" href="./Styles/Modal.css">
        <link rel="stylesheet" href="./Styles/styles.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
              integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">


    </head>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js"></script>
    <body>
        <canvas id="myChart" style="width: 100%; max-width: 600px"></canvas>
        <script>
            var xValues = ["Italy", "France", "Spain", "USA", "Argentina"];
            var yValues = [355, 439, 444, 524, 315];
            var barColors = ["red", "green", "blue", "orange", "brown"];
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
                        text: "World Wine Production 2018",
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
    </body>
</html>