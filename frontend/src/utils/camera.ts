/**
 * 格式化时间（数据库timestamp转本地格式）
 */
export const formatTime = (timeStr: string): string => {
  if (!timeStr) return '';
  const date = new Date(timeStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

/**
 * 视频全屏切换
 */
export const toggleFullScreen = (videoElement: HTMLVideoElement | null): void => {
  if (!videoElement) return;
  if (videoElement.requestFullscreen) {
    videoElement.requestFullscreen();
  } else if ((videoElement as any).webkitRequestFullscreen) {
    (videoElement as any).webkitRequestFullscreen();
  } else if ((videoElement as any).mozRequestFullScreen) {
    (videoElement as any).mozRequestFullScreen();
  } else if ((videoElement as any).msRequestFullscreen) {
    (videoElement as any).msRequestFullscreen();
  }
};

/**
 * 重启视频播放
 */
export const restartVideo = (videoElement: HTMLVideoElement | null): void => {
  if (!videoElement) return;
  videoElement.pause();
  videoElement.load();
  videoElement.play();
};