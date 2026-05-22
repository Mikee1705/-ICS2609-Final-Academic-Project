function filterTableByName() {
    // Get the typed value and convert it to lowercase
    var input = document.getElementById("nameSearchInput");
    var filter = input.value.toLowerCase();
    
    var tbody = document.getElementById("TableBody");
    var rows = tbody.getElementsByTagName("tr");

    // Loop through all table rows
    for (var i = 0; i < rows.length; i++) {
        var cells = rows[i].getElementsByTagName("td");
        
        if (cells.length > 1) {
            // Target the SECOND column (index 1), where Student Name lives
            var nameCell = cells[1];
            var nameText = nameCell.textContent || nameCell.innerText;
            
            // Check if the name contains the typed search characters
            if (nameText.toLowerCase().indexOf(filter) > -1) {
                rows[i].style.display = ""; // Show row
            } else {
                rows[i].style.display = "none"; // Hide row
            }
        }
    }
}