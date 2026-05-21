const form = document.querySelector("#analysis-form");
const input = document.querySelector("#channel-url");
const button = document.querySelector("#submit-button");
const statusBox = document.querySelector("#status");
const errorBox = document.querySelector("#error");
const emptyState = document.querySelector("#empty-state");
const results = document.querySelector("#results");

const loadingSteps = [
    "채널 확인 중",
    "공개 지표 수집 중",
    "인사이트 생성 중"
];

let loadingTimer = null;

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const channelUrl = input.value.trim();
    if (!isSupportedUrl(channelUrl)) {
        showError("지원하는 YouTube 채널 URL을 입력하세요. 예: https://www.youtube.com/@channelname");
        input.focus();
        return;
    }

    setLoading(true);
    hideError();

    try {
        const response = await fetch("/api/channel-analyses", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ channelUrl })
        });

        const body = await response.json();
        if (!response.ok) {
            throw new Error(body.message || "분석에 실패했습니다. 잠시 후 다시 시도하세요.");
        }

        renderResult(body);
        statusBox.textContent = "분석 완료";
    } catch (error) {
        showError(error.message);
    } finally {
        setLoading(false);
    }
});

function isSupportedUrl(value) {
    if (!value) {
        return false;
    }

    try {
        const url = new URL(value);
        const host = url.hostname.toLowerCase();
        const path = url.pathname;
        return (host === "youtube.com" || host === "www.youtube.com")
            && (path.startsWith("/channel/") || path.startsWith("/@") || path.startsWith("/user/"));
    } catch {
        return false;
    }
}

function setLoading(isLoading) {
    button.disabled = isLoading;
    input.disabled = isLoading;
    button.textContent = isLoading ? "분석 중" : "분석";

    if (!isLoading) {
        clearInterval(loadingTimer);
        loadingTimer = null;
        return;
    }

    let index = 0;
    statusBox.textContent = loadingSteps[index];
    loadingTimer = setInterval(() => {
        index = Math.min(index + 1, loadingSteps.length - 1);
        statusBox.textContent = loadingSteps[index];
    }, 1400);
}

function showError(message) {
    errorBox.hidden = false;
    errorBox.textContent = message;
    statusBox.textContent = "";
}

function hideError() {
    errorBox.hidden = true;
    errorBox.textContent = "";
}

function renderResult(data) {
    emptyState.hidden = true;
    results.hidden = false;

    document.querySelector("#channel-title").textContent = data.channel.title;
    document.querySelector("#channel-description").textContent = data.channel.description || "채널 설명이 없습니다.";
    document.querySelector("#channel-thumbnail").src = data.channel.thumbnailUrl || "";
    document.querySelector("#subscriber-count").textContent = formatNumber(data.channel.subscriberCount);
    document.querySelector("#view-count").textContent = formatNumber(data.channel.viewCount);
    document.querySelector("#video-count").textContent = formatNumber(data.channel.videoCount);
    document.querySelector("#summary").textContent = data.insight.summary;

    renderList("#strengths", data.insight.strengths);
    renderList("#opportunities", data.insight.opportunities);
    renderList("#next-actions", data.insight.nextActions);
}

function renderList(selector, items) {
    const list = document.querySelector(selector);
    list.replaceChildren();
    for (const item of items || []) {
        const li = document.createElement("li");
        li.textContent = item;
        list.append(li);
    }
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString("ko-KR");
}
