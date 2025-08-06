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

document.querySelectorAll(".btn-like, .btn-dislike").forEach(btn => {
    btn.addEventListener("click", function() {
        const postId = this.getAttribute("data-post-id");
        const type = this.classList.contains("btn-like") ? "LIKE" : "DISLIKE";

        // 버튼들 가져오기
        const likeBtn = document.querySelector(`.btn-like[data-post-id="${postId}"]`);
        const dislikeBtn = document.querySelector(`.btn-dislike[data-post-id="${postId}"]`);

        // 버튼 잠시 비활성화 (중복 클릭 방지)
        likeBtn.disabled = true;
        dislikeBtn.disabled = true;

        fetch(`/board/${postId}/reaction?type=${type}`, {
            method: "POST",
            headers: {
                "X-Requested-With": "XMLHttpRequest"
            }
        })
            .then(res => {
                if (res.status === 401) {
                    window.location.href = "/login";
                    return;
                }
                return res.json();
            })
            .then(data => {
                if (!data) return; // 위에서 리다이렉트 했으면 중단
                document.getElementById("like-count").textContent = data.likes;
                document.getElementById("dislike-count").textContent = data.dislikes;

                const likeBtn = document.querySelector(`.btn-like[data-post-id="${postId}"]`);
                const dislikeBtn = document.querySelector(`.btn-dislike[data-post-id="${postId}"]`);

                // 현재 버튼 눌렀다면 active 토글
                if (type === "LIKE") {
                    if (likeBtn.classList.contains("active")) {
                        likeBtn.classList.remove("active"); // 취소
                    } else {
                        likeBtn.classList.add("active");
                        dislikeBtn.classList.remove("active");
                    }
                } else {
                    if (dislikeBtn.classList.contains("active")) {
                        dislikeBtn.classList.remove("active"); // 취소
                    } else {
                        dislikeBtn.classList.add("active");
                        likeBtn.classList.remove("active");
                    }
                }
            })
            .catch(err => console.error(err));

    });
});