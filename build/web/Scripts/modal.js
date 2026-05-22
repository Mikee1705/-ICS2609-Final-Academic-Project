var modal = document.getElementById("myModal");
var btn = document.getElementById("myBtn");
var span = document.getElementsByClassName("close")[0];


function closeModal() {
    if (modal.classList.contains('closing')) return;

    modal.classList.add('closing');
    setTimeout(() => {
        modal.classList.remove('open', 'closing');
    }, 300); // Must match CSS animation time (0.3s)
}

btn.onclick = function () {
    modal.classList.add("open");
};

// Close modal via X button
span.onclick = function () {
    closeModal();
};

window.onclick = function (event) {
    if (event.target == modal) {
        closeModal();
    };
};
