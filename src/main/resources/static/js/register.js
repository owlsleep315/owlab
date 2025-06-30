// 사용자 ID 중복 확인
async function checkUsername() {
    const username = document.querySelector('#username').value;
    if (!username) return;

    try {
        const res = await fetch('/check-username?username=' + encodeURIComponent(username));
        const available = await res.json();
        const checkEl = document.getElementById('username-check');
        if (checkEl) {
            checkEl.innerText = available ? "사용 가능" : "이미 사용 중";
            checkEl.style.color = available ? "green" : "red";
        }
    } catch (e) {
        console.error("중복 확인 중 오류 발생", e);
    }
}