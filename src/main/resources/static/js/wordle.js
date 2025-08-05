let currentRow = 0;
let currentCol = 0;
let board = document.getElementById("board");
let guess = "";
let gameOver = false;

// 새 게임 버튼
document.getElementById("new-game-btn").addEventListener("click", () => {
    fetch("/wordle/start", { method: "POST" })
        .then(res => res.json())
        .then(() => {
            location.reload(); // 페이지 리로드로 보드 초기화
        });
});

// 가상 키보드 버튼 클릭 처리
document.querySelectorAll(".key").forEach(key => {
    key.addEventListener("click", () => handleKeyInput(key.textContent));
});

// 실제 키보드 입력 처리
document.addEventListener("keydown", (event) => {
    let key = event.key.toUpperCase();

    if (key === "ENTER") {
        handleKeyInput("Enter");
    } else if (key === "BACKSPACE") {
        handleKeyInput("⌫");
    } else if (/^[A-Z]$/.test(key)) {
        handleKeyInput(key);
    }
});

// 공통 키 입력 처리
function handleKeyInput(keyValue) {
    if (gameOver) return; // 게임 끝났으면 입력 차단

    // 가상 키보드 버튼 "눌린 효과"
    const btn = [...document.querySelectorAll(".key")]
        .find(b => b.textContent === keyValue);
    if (btn) {
        btn.classList.add("active");
        setTimeout(() => btn.classList.remove("active"), 100);
    }

    if (keyValue === "Enter") {
        submitGuess();
    } else if (keyValue === "⌫") {
        deleteLetter();
    } else if (/^[A-Z]$/.test(keyValue) && keyValue.length === 1) {
        addLetter(keyValue);
    }
}

// 글자 추가
function addLetter(letter) {
    if (currentCol < 5) {
        const row = board.children[currentRow];
        const tile = row.children[currentCol];
        tile.textContent = letter;
        guess += letter;
        currentCol++;
    }
}

// 글자 삭제
function deleteLetter() {
    if (currentCol > 0) {
        currentCol--;
        const row = board.children[currentRow];
        const tile = row.children[currentCol];
        tile.textContent = "";
        guess = guess.slice(0, -1);
    }
}

// 단어 제출
function submitGuess() {
    if (guess.length !== 5) {
        const row = board.children[currentRow];
        row.classList.add("shake");
        setTimeout(() => row.classList.remove("shake"), 600);
        clearRow(currentRow);
        return;
    }

    fetch("/wordle/guess", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ guess })
    })
        .then(res => res.json())
        .then(data => {
            if (data.error) {
                // 존재하지 않는 단어 → 흔들기 애니메이션
                const row = board.children[currentRow];
                row.classList.add("shake");
                setTimeout(() => row.classList.remove("shake"), 600);

                clearRow(currentRow);
                return;
            }

            const result = data.result;
            const row = board.children[currentRow];

            // 한 칸씩 flip 애니메이션 적용 + 키보드 업데이트
            result.forEach((status, i) => {
                const tile = row.children[i];
                const letter = guess[i];

                setTimeout(() => {
                    tile.classList.add("flip");
                    setTimeout(() => {
                        tile.classList.remove("flip");
                        if (status === "G") tile.classList.add("correct");
                        else if (status === "Y") tile.classList.add("present");
                        else if (status === "B") tile.classList.add("absent");

                        updateKeyboard(letter, status);
                    }, 250);
                }, i * 400);
            });

            if (data.success === true) {
                gameOver = true; // 정답 → 게임 종료

                // 정답 → 타일 점프 애니메이션
                setTimeout(() => {
                    for (let i = 0; i < 5; i++) {
                        const tile = row.children[i];
                        setTimeout(() => tile.classList.add("jump"), i * 150);
                    }
                }, result.length * 400 + 500);
            } else if (data.success === false) {
                gameOver = true; // 기회 소진 → 게임 종료

                setTimeout(() => {
                    alert("정답은 " + data.answer + "입니다.");
                }, result.length * 400 + 500);
            }

            currentRow++;
            currentCol = 0;
            guess = "";
        });
}

// 현재 줄 초기화
function clearRow(rowIndex) {
    const row = board.children[rowIndex];
    for (let i = 0; i < 5; i++) {
        row.children[i].textContent = "";
    }
    currentCol = 0;
    guess = "";
}

// 키보드 색상 업데이트 (letter 단위)
function updateKeyboard(letter, status) {
    const keyBtn = [...document.querySelectorAll(".key")]
        .find(b => b.textContent === letter);

    if (!keyBtn) return;

    if (status === "G") {
        keyBtn.classList.remove("present", "absent");
        keyBtn.classList.add("correct");
    } else if (status === "Y") {
        if (!keyBtn.classList.contains("correct")) {
            keyBtn.classList.remove("absent");
            keyBtn.classList.add("present");
        }
    } else if (status === "B") {
        if (!keyBtn.classList.contains("correct") &&
            !keyBtn.classList.contains("present")) {
            keyBtn.classList.add("absent");
        }
    }
}
