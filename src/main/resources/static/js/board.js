document.addEventListener('DOMContentLoaded', function () {
    const replyToggleButtons = document.querySelectorAll('.reply-toggle');
    replyToggleButtons.forEach(button => {
        button.addEventListener('click', function () {
            const formId = this.getAttribute('data-reply-form-id');
            const form = document.getElementById(formId);
            if (form) {
                form.classList.toggle('d-none');
            }
        });
    });
});
