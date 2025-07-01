document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".reply-toggle").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const form = btn.nextElementSibling;
            if (form.classList.contains("hidden")) {
                form.classList.remove("hidden");
            } else {
                form.classList.add("hidden");
            }
        });
    });
});
