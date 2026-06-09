const StoryViewer = (() => {
  let stories = [];
  let currentIndex = 0;
  let timer = null;
  let paused = false;
  let progressStart = null;
  let progressEl = null;
  let viewer = null;

  function open(storyList, startIndex = 0) {
    stories = storyList;
    currentIndex = startIndex;
    render();
    show(currentIndex);
    API.get(`/stories/${stories[currentIndex].id}`).catch(() => {});
  }

  function render() {
    viewer = document.createElement('div');
    viewer.className = 'story-viewer';
    viewer.innerHTML = `
      <div class="story-progress-bar" id="story-progress"></div>
      <div class="story-top-bar">
        <div class="story-user-info">
          <img class="avatar avatar-sm" id="story-avatar" src="" alt="">
          <div>
            <div class="story-user-name" id="story-username"></div>
            <div class="story-time" id="story-time"></div>
          </div>
        </div>
        <button class="story-close" id="story-close">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
        </button>
      </div>
      <img class="story-media" id="story-media" src="" alt="">
      <div class="story-tap-left" id="story-prev"></div>
      <div class="story-tap-right" id="story-next"></div>
    `;
    document.body.appendChild(viewer);

    document.getElementById('story-close').addEventListener('click', close);
    document.getElementById('story-prev').addEventListener('click', prev);
    document.getElementById('story-next').addEventListener('click', next);

    // Long press to pause
    viewer.addEventListener('pointerdown', () => { paused = true; });
    viewer.addEventListener('pointerup', () => { paused = false; });
  }

  function show(index) {
    if (index < 0 || index >= stories.length) { close(); return; }
    currentIndex = index;
    const story = stories[index];

    document.getElementById('story-avatar').src = Components.avatarSrc(story.user.avatarUrl, story.user.username);
    document.getElementById('story-username').textContent = story.user.username;
    document.getElementById('story-time').textContent = Components.timeAgo(story.createdAt);
    document.getElementById('story-media').src = story.mediaUrl;

    buildProgressBar();
    startTimer();
    API.get(`/stories/${story.id}`).catch(() => {});
  }

  function buildProgressBar() {
    const bar = document.getElementById('story-progress');
    bar.innerHTML = stories.map((_, i) => `
      <div class="story-progress-segment">
        <div class="story-progress-fill" id="seg-${i}" style="width:${i < currentIndex ? '100%' : '0%'}"></div>
      </div>`).join('');
    progressEl = document.getElementById(`seg-${currentIndex}`);
  }

  function startTimer() {
    clearInterval(timer);
    progressStart = Date.now();
    const duration = 5000;
    timer = setInterval(() => {
      if (paused) return;
      const elapsed = Date.now() - progressStart;
      const pct = Math.min((elapsed / duration) * 100, 100);
      if (progressEl) progressEl.style.width = pct + '%';
      if (elapsed >= duration) next();
    }, 50);
  }

  function next() { clearInterval(timer); show(currentIndex + 1); }
  function prev() { clearInterval(timer); show(currentIndex - 1); }

  function close() {
    clearInterval(timer);
    if (viewer) { viewer.remove(); viewer = null; }
  }

  return { open, close };
})();
