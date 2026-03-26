export interface ViewerTransformState {
  rotation: number;
  scale: number;
  translateX: number;
  translateY: number;
  isDragging: boolean;
  lastMouseX: number;
  lastMouseY: number;
}

export function getViewerTransform(state: ViewerTransformState): string {
  return `translate(${state.translateX}px, ${state.translateY}px) rotate(${state.rotation}deg) scale(${state.scale})`;
}

export function resetViewerTransform(state: ViewerTransformState): void {
  state.rotation = 0;
  state.scale = 1;
  state.translateX = 0;
  state.translateY = 0;
}

export function startViewerDragging(state: ViewerTransformState, event: MouseEvent): void {
  state.isDragging = true;
  state.lastMouseX = event.clientX;
  state.lastMouseY = event.clientY;
}

export function onViewerDragging(state: ViewerTransformState, event: MouseEvent): void {
  if (!state.isDragging) return;

  const deltaX = event.clientX - state.lastMouseX;
  const deltaY = event.clientY - state.lastMouseY;

  state.translateX += deltaX;
  state.translateY += deltaY;

  state.lastMouseX = event.clientX;
  state.lastMouseY = event.clientY;
}

export function stopViewerDragging(state: ViewerTransformState): void {
  state.isDragging = false;
}
