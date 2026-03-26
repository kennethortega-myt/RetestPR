import { Component, Input } from '@angular/core';
import {
  getViewerTransform,
  onViewerDragging,
  resetViewerTransform,
  startViewerDragging,
  stopViewerDragging,
  ViewerTransformState
} from '../../../../helpers/viewerinteraction.helper';

@Component({
  selector: 'app-pop-imagen-acta',
  templateUrl: './pop-imagen-acta.component.html',
  styleUrl: './pop-imagen-acta.component.css',
})
export class PopImagenActaComponent {
  @Input() imageSrc: string = ''; // Ruta de la imagen

  // Estados para transformar la imagen
  rotation: number = 0;
  scale: number = 1;
  translateX: number = 0;
  translateY: number = 0;

  // Dragging
  isDragging: boolean = false;
  lastMouseX: number = 0;
  lastMouseY: number = 0;

  // Métodos para controlar transformaciones
  rotateImage() {
    this.rotation += 90;
  }

  zoomIn() {
    this.scale += 0.1;
  }

  zoomOut() {
    this.scale = Math.max(0.1, this.scale - 0.1); // Evita que el zoom sea menor a 0.1
  }

  private get viewerState(): ViewerTransformState {
    return this;
  }

  resetTransform() {
    resetViewerTransform(this.viewerState);
  }

  getTransform(): string {
    return getViewerTransform(this.viewerState);
  }

  // Métodos para arrastrar la imagen
  startDragging(event: MouseEvent) {
    startViewerDragging(this.viewerState, event);
  }

  onDragging(event: MouseEvent) {
    onViewerDragging(this.viewerState, event);
  }

  stopDragging() {
    stopViewerDragging(this.viewerState);
  }
}
