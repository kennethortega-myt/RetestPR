import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  Inject,
  Input,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { take } from 'rxjs';
import { Archivo } from '../../../interfaces/acta-bean';
import { ActasService } from '../../../services/elecciones-generales/actas.service';
import { BlobConUrl, IModalVisorConfig } from './modal-visor-pdf.interface';

@Component({
  selector: 'app-modal-visor-pdf',
  templateUrl: './modal-visor-pdf.component.html',
  styleUrls: ['./modal-visor-pdf.component.scss'],
  standalone: false
})
export class ModalVisorPdfComponent implements OnInit, OnDestroy {
  @Input() imageSrc: string = ''; // Ruta de la imagen
  @ViewChild('pseudoModal') pseudoModal!: ElementRef;
  @ViewChild('pdfContainer', { static: false }) pdfContainer!: ElementRef;
  isFullscreen: boolean = false; // Estado del modo pantalla completa
  pdfSrc: any;
  tempRetFileData: any;
  tempBlob: any;
  esPantallaChica: boolean = false;
  selectedArchivo: Archivo;
  zoomLevel = 1;
  rotation: number = 0;
  scale: number = 1;
  translateX: number = 0;
  translateY: number = 0;
  isDragging: boolean = false;
  lastMouseX: number = 0;
  lastMouseY: number = 0;
  rotation2 = 0;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: IModalVisorConfig,
    private readonly actaService?: ActasService,
    private cdr?: ChangeDetectorRef,
    private readonly dialogRef?: MatDialogRef<ModalVisorPdfComponent>
  ) {
    this.listenToFullscreenChanges();
  }

  @HostListener('window:resize', ['$event']) onResize(event: any): void {
    this.updateEsPantallaChica();
  }

  ngOnInit(): void {
    this.updateEsPantallaChica();
    if (this.data.file) {
      this.pdfSrc = this.data.file;
    } else {
      if (!this.data.multiple) {
        this.loadActa();
      } else {
        this.data.archivos.forEach((y) => (y.activo = false));
        if (!this.esPantallaChica) {
          if (this.data.archivos.length > 0) {
            this.data.archivos[0].activo = true;
          }
        }
        this.loadResolution();
      }
    }
  }

  private updateEsPantallaChica(): void {
    this.esPantallaChica = window.innerWidth < 960;
  }

  showLoading() {
    // Loading functionality removed
  }

  hideLoading() {
    // Loading functionality removed
  }

  loadActa(callback?: () => void): void {
    const { archivos } = this.data;
    if (archivos && archivos.length && !this.data.esPantallaChica) {
      const actaToDownload = archivos[0]; // should download only one file
      this.selectedArchivo = actaToDownload;
      this.showLoading();
      this.actaService
        .descargarPdf(actaToDownload.id.toString())
        .pipe(take(1))
        .subscribe({
          next: (blob) => {
            if (blob && blob.size > 0 && blob.type === 'application/pdf') {
              this.pdfSrc = (blob as BlobConUrl).url;
              this.hideLoading();
              callback?.();
            } else {
              this.hideLoading();
              console.error('Blob no es un PDF válido:', blob.type);
            }
          },
          error: (err) => {
            this.hideLoading();
            console.error('Error al descargar la imagen:', err);
          }
        });
    }
  }

  loadResolution() {
    const { archivos } = this.data;
    if (archivos && archivos.length && !this.data.esPantallaChica) {
      this.showLoading();
      const resolutionToDownload = archivos[0]; // download only one resolution and after download others
      this.selectedArchivo = resolutionToDownload;
      this.actaService
        .descargarImagen(resolutionToDownload.id.toString())
        .pipe(take(1))
        .subscribe({
          next: (blob) => {
            if (blob && blob.size > 0 && blob.type === 'application/pdf') {
              this.pdfSrc = (blob as BlobConUrl).url;
              this.hideLoading();
            } else {
              this.hideLoading();
              console.error('Blob no es un PDF válido:', blob.type);
            }
          },
          error: (err) => {
            this.hideLoading();
            console.error('Error al descargar la imagen:', err);
          }
        });
    }
  }

  loadOtherResolution(archivo: Archivo) {
    archivo.activo = true;
    // debugger
    const { archivos } = this.data;
    if (this.selectedArchivo?.id != archivo.id || this.data.esPantallaChica) {
      this.showLoading();
      archivos.filter((x) => x.id != archivo.id).forEach((y) => (y.activo = false));
      this.selectedArchivo = archivo;
      this.actaService
        .descargarImagen(archivo.id.toString())
        .pipe(take(1))
        .subscribe({
          next: (blob) => {
            if (blob && blob.size > 0 && blob.type === 'application/pdf') {
              this.pdfSrc = (blob as BlobConUrl).url;
              this.hideLoading();
              if (this.data.esPantallaChica) {
                this.descargarPDF(archivo);
                this.hideLoading();
              }
            } else {
              this.hideLoading();
              console.error('Blob no es un PDF válido:', blob.type);
            }
          },
          error: (err) => {
            this.hideLoading();
            console.error('Error al descargar la imagen:', err);
          }
        });
    }
  }

  cerrar(): void {
    this.dialogRef.close(true);
  }

  descargarPDF(archivo?): void {
    if (!archivo) archivo = this.selectedArchivo;
    const src = `${this.pdfSrc}`;
    const link = document.createElement('a');
    link.href = src;
    link.download = `${archivo.descripcion} - ${this.data.numeroDeActa} - ${this.data.nombreDeActa}`;

    link.target = '_blank';
    link.click();
    link.remove();
  }

  rotateImage() {
    this.rotation += 90;
    this.centerContent();
  }
  centerContent() {
    this.translateX = 0;
    this.translateY = 0;
  }
  resetPosition() {
    if (this.scale <= 1) {
      this.translateX = 0;
      this.translateY = 0;
    }
  }

  increaseZoom() {
    this.zoomLevel += 0.5;
    if (this.zoomLevel == this.zoomLevel) {
      const pdfViewer = document.querySelector('.pdf-viewer') as HTMLElement;
      if (pdfViewer) {
        pdfViewer.style.transform = 'none';
      }
    }
  }

  decreaseZoom() {
    if (this.zoomLevel > 0.5) {
      this.zoomLevel -= 0.5;
    }

    if (this.zoomLevel == this.zoomLevel) {
      const pdfViewer = document.querySelector('.pdf-viewer') as HTMLElement;
      if (pdfViewer) {
        pdfViewer.style.transform = 'none';
      }
    }
  }

  rotatePDF() {
    this.rotation2 = (this.rotation2 + 90) % 360;
    this.cdr.detectChanges();
  }

  resetTransform() {
    this.rotation = 0;
    this.scale = 1;
    this.translateX = 0;
    this.translateY = 0;
  }

  getTransform(): string {
    return `translate(${this.translateX}px, ${this.translateY}px) rotate(${this.rotation}deg) scale(${this.scale})`;
  }

  // Métodos para arrastrar la imagen
  startDragging(event: MouseEvent) {
    this.isDragging = true;
    this.lastMouseX = event.clientX;
    this.lastMouseY = event.clientY;
  }

  onDragging(event: MouseEvent) {
    if (!this.isDragging) return;

    const deltaX = event.clientX - this.lastMouseX;
    const deltaY = event.clientY - this.lastMouseY;

    this.translateX += deltaX;
    this.translateY += deltaY;

    this.lastMouseX = event.clientX;
    this.lastMouseY = event.clientY;
  }

  stopDragging() {
    this.isDragging = false;
  }

  toggleFullscreen() {
    const modal = this.pseudoModal.nativeElement;

    if (!this.isFullscreen) {
      if (modal.requestFullscreen) {
        modal.requestFullscreen();
      } else if ((modal as any).webkitRequestFullscreen) {
        (modal as any).webkitRequestFullscreen(); // Safari
      } else if ((modal as any).msRequestFullscreen) {
        (modal as any).msRequestFullscreen(); // IE11
      }
    } else {
      if (document.exitFullscreen) {
        document.exitFullscreen();
      } else if ((document as any).webkitExitFullscreen) {
        (document as any).webkitExitFullscreen(); // Safari
      } else if ((document as any).msExitFullscreen) {
        (document as any).msExitFullscreen(); // IE11
      }
    }
  }

  private listenToFullscreenChanges(): void {
    document.addEventListener('fullscreenchange', this.onFullscreenChange);
    document.addEventListener('webkitfullscreenchange', this.onFullscreenChange);
    document.addEventListener('mozfullscreenchange', this.onFullscreenChange);
    document.addEventListener('MSFullscreenChange', this.onFullscreenChange);
  }

  private onFullscreenChange = (): void => {
    const isCurrentlyFullscreen = !!(
      document.fullscreenElement ||
      (document as Document & { webkitFullscreenElement?: Element }).webkitFullscreenElement ||
      (document as Document & { mozFullScreenElement?: Element }).mozFullScreenElement ||
      (document as Document & { msFullscreenElement?: Element }).msFullscreenElement
    );

    if (this.isFullscreen !== isCurrentlyFullscreen) {
      this.isFullscreen = isCurrentlyFullscreen;
      this.cdr?.detectChanges();
    }
  };

  ngOnDestroy(): void {
    document.removeEventListener('fullscreenchange', this.onFullscreenChange);
    document.removeEventListener('webkitfullscreenchange', this.onFullscreenChange);
    document.removeEventListener('mozfullscreenchange', this.onFullscreenChange);
    document.removeEventListener('MSFullscreenChange', this.onFullscreenChange);
  }
}
