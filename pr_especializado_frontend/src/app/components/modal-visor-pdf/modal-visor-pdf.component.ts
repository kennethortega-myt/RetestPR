import { DatePipe, CommonModule } from "@angular/common";
import { Component, Inject, Input, ElementRef, ViewChild, OnInit,ChangeDetectorRef, inject, OnDestroy } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { DragDropModule } from '@angular/cdk/drag-drop';
import { finalize, take } from "rxjs";
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { LoadingService } from "../loading/loading.service";
import { ActaApiService } from "../../services/acta-api.service";
import { DialogService } from "../../services/dialog.service";
import { TYPE_FOR_PDF } from '../../helpers/enums';
import {
  getViewerTransform,
  onViewerDragging,
  resetViewerTransform,
  startViewerDragging,
  stopViewerDragging,
  ViewerTransformState
} from '../../helpers/viewerinteraction.helper';

export interface Archivo {
  daudFechaCreacion?: string;
  descripcion: string;
  id: string;
  nombre: string;
  tipo: number;
  activo?: boolean;
}
export interface IModalVisorConfig {
  file?: any;
  numeroDeActa?: string;
  nombreDeActa?: string;
  multiple: boolean;
  archivos: Archivo[];
  esPantallaChica?: boolean;
}
export interface BlobConUrl extends Blob {
  url?: string;
}

@Component({
  selector: "app-modal-visor-pdf",
  templateUrl: "./modal-visor-pdf.component.html",
  styleUrls: ["./modal-visor-pdf.component.scss"],
  providers: [DatePipe],
  standalone: true,
  imports: [CommonModule, PdfViewerModule, DragDropModule]
})
export class ModalVisorPdfComponent implements OnInit, OnDestroy {
  @ViewChild("pseudoModal") pseudoModal!: ElementRef;

  isFullscreen: boolean = false; // Estado del modo pantalla completa

  pdfSrc: any;
  tempRetFileData: any;
  tempBlob: any;
  selectedArchivo?: Archivo;

  DescargarPDF = "modal-visor-img.DescargarPDF"
  Cerrar = "modal-visor-img.Cerrar";
  mensajeError: string = '';
  fileAction = false;

  private readonly dialogService = inject(DialogService);
  private readonly loadingService = inject(LoadingService);
  private readonly actaService = inject(ActaApiService);
  private readonly cdr = inject(ChangeDetectorRef);
  public readonly datepipe = inject(DatePipe, { optional: true });
  public readonly dialogRef = inject(MatDialogRef<ModalVisorPdfComponent>, { optional: true });
  public readonly data = inject<IModalVisorConfig>(MAT_DIALOG_DATA);

  // Nuevo Modal (Ini)
  /* Funcionalidades */
  @Input() imageSrc: string = ""; // Ruta de la imagen

  // Estados para transformar la imagen
  rotation: number = 0;
  scale: number = 1;
  translateX: number = 0;
  translateY: number = 0;

  // Dragging
  isDragging: boolean = false;
  lastMouseX: number = 0;
  lastMouseY: number = 0;
  @ViewChild('pdfContainer', { static: false }) pdfContainer!: ElementRef;
  zoomLevel = 1;
  rotation2 = 0;

  private readonly fullscreenChangeListener = this.onFullscreenChange.bind(this);

  get mostrarDisclaimerActas(): boolean {
    const tipoArchivo = this.data.archivos?.[0]?.tipo;

    if (tipoArchivo == null) {
      return false;
    }

    return [
      TYPE_FOR_PDF['ID_ACTA_ESCRUTINIO'],
      TYPE_FOR_PDF['ID_ACTA_INSTALACION_Y_SUFRAGIO'],
      TYPE_FOR_PDF['ID_ACTA_INSTALACION'],
      TYPE_FOR_PDF['ID_ACTA_SUFRAGIO'],
    ].includes(tipoArchivo);
  }

  onFullscreenChange(): void {
    this.isFullscreen = !!document.fullscreenElement;
    this.cdr.detectChanges();
  }

  ngOnInit(): void {
    this.inicializarPantalla();
    this.inicializarContenido();
    document.addEventListener('fullscreenchange', this.fullscreenChangeListener);
  }

  private inicializarPantalla(): void {
    this.data.esPantallaChica = window.innerWidth < 960;
  }

  private inicializarContenido(): void {
    if (this.data.file) {
      this.pdfSrc = this.data.file;
      return;
    }

    if (!this.data.multiple) {
      this.loadActa();
      return;
    }

    this.prepararArchivosMultiples();
    this.loadResolution();
  }

  private prepararArchivosMultiples(): void {
    this.data.archivos.forEach(a => (a.activo = false));

    this.ordenarArchivosSiCorresponde();

    if (!this.data.esPantallaChica && this.data.archivos.length > 0) {
      this.data.archivos[0].activo = true;
    }
  }

  private ordenarArchivosSiCorresponde(): void {
    const tipoArchivo = this.data.archivos[0]?.tipo;

    if (tipoArchivo !== 5) return;

    this.data.archivos = [...this.data.archivos].sort((a, b) => {
      const fechaA = a.daudFechaCreacion
        ? new Date(a.daudFechaCreacion).getTime()
        : 0;
      const fechaB = b.daudFechaCreacion
        ? new Date(b.daudFechaCreacion).getTime()
        : 0;
      return fechaB - fechaA;
    });
  }

  ngOnDestroy(): void {
    document.removeEventListener('fullscreenchange', this.fullscreenChangeListener);
  }

  showLoading() {
    this.fileAction = false;
    this.mensajeError = '';
    this.loadingService.show();
  }

  hideLoading() {
    setTimeout(() => {
      this.loadingService.hide();
    }, 525);
  }

  public loadActa(callback?: () => void): void {
    const { archivos } = this.data;
    if (archivos && archivos.length > 0 && !this.data.esPantallaChica){
      const actaToDownload = archivos[0]; // should download only one file
      this.selectedArchivo = actaToDownload;
      this.showLoading();
      this.actaService
        .descargarPdf(actaToDownload.id.toString())
        .pipe(
          take(1),
          finalize(() => {
            this.hideLoading();
          })
        )
        .subscribe({
          next: (blob) => {
            if (blob && blob.size > 0 && blob.type === 'application/pdf') {
              this.urlBlob(blob)
              callback?.();
            } else {
              console.error('Blob no es un PDF válido:',  blob.type);
              this.urlBlob(new Blob([], { type: 'application/pdf' }));
              this.mensajeError = 'No hay un archivo PDF';
              this.fileAction = true;
            }
          },
          error: (err) => {
            console.error('Error al descargar la imagen:', err);
            this.urlBlob(new Blob([], { type: 'application/pdf' }));
            this.mensajeError = 'No hay un archivo a descargar';
            this.fileAction = true;
          }
        });
    }
  }

  public loadResolution() {
    const { archivos } = this.data;
    if (archivos && archivos.length > 0 && !this.data.esPantallaChica){
      const resolutionToDownload = archivos[0]; // download only one resolution and after download others
      this.selectedArchivo = resolutionToDownload;
      this.showLoading();
      this.actaService
        .descargarImagen(resolutionToDownload.id.toString())
        .pipe(
          take(1),
          finalize(() => {
            this.hideLoading();
          })
        )
        .subscribe({
          next: (blob) => {
            if (blob && blob.size > 0 && blob.type === 'application/pdf') {
              this.urlBlob(blob)
            } else {
              console.error('Blob no es un PDF válido:',  blob.type);
              this.urlBlob(new Blob([], { type: 'application/pdf' }));
              this.mensajeError = 'No hay un archivo PDF';
              this.fileAction = true;
            }
          },
          error: (err) => {
            console.error('Error al descargar la imagen:', err);
            this.urlBlob(new Blob([], { type: 'application/pdf' }));
            this.mensajeError = 'No hay un archivo a descargar';
            this.fileAction = true;
          }
        });
    }
  }

  urlBlob(blob: any){
    this.pdfSrc = (blob as BlobConUrl).url;
    if (this.pdfSrc) {
      URL.revokeObjectURL(this.pdfSrc);
    }
    this.pdfSrc = URL.createObjectURL(blob);
    this.cdr.detectChanges();
  }

  public loadOtherResolution(archivo: Archivo) {
    archivo.activo = true;
    const { archivos } = this.data;
    if (this.selectedArchivo?.id !== archivo.id || this.data.esPantallaChica) {
      archivos.filter(x => x.id !== archivo.id).forEach(y => y.activo = false);
      this.selectedArchivo = archivo;
      this.showLoading();
      this.actaService.descargarImagen(archivo.id.toString())
        .pipe(
          take(1),
          finalize(() => {
            this.hideLoading();
          })
        )
        .subscribe({
          next: (blob) => {
            if (blob && blob.size > 0 && blob.type === 'application/pdf') {
              this.urlBlob(blob)
              if(this.data.esPantallaChica){
                this.descargarPDF(archivo);
              }
            } else {
              console.error('Blob no es un PDF válido:',  blob.type);
              this.urlBlob(new Blob([], { type: 'application/pdf' }));
              this.mensajeError = 'No hay un archivo PDF';
              this.fileAction = true;
            }
          },
          error: (err) => {
            console.error('Error al descargar la imagen:', err);
            this.urlBlob(new Blob([], { type: 'application/pdf' }));
            this.mensajeError = 'No hay un archivo a descargar';
            this.fileAction = true;
          }
        });
    }
  }

  onPdfError(error: any) {
    console.error('Error al cargar el PDF:', error);
  }

  afterLoadComplete(pdf: any) {
    // PDF cargado correctamente
  }

  cerrar(): void {
    this.dialogRef?.close(true);
  }

  descargarPDF(archivo?: any): void {
    if (!archivo) archivo = this.selectedArchivo
    const src = `${this.pdfSrc}`;
    const link = document.createElement("a");
    link.href = src;
    link.download = `${archivo.descripcion} - ${this.data.numeroDeActa} - ${this.data.nombreDeActa}`;

    link.target = "_blank";
    link.click();
    link.remove();
  }

  // Métodos para controlar transformaciones
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
    const pdfViewer = document.querySelector('.pdf-viewer') as HTMLElement;
    if (pdfViewer) {
      pdfViewer.style.transform = 'none';
    }
  }

  decreaseZoom() {
    if (this.zoomLevel > 0.5) {
      this.zoomLevel -= 0.5;
    }
    const pdfViewer = document.querySelector('.pdf-viewer') as HTMLElement;
    if (pdfViewer) {
      pdfViewer.style.transform = 'none';
    }
  }

  rotatePDF() {
    this.rotation2 = (this.rotation2 + 90) % 360; this.cdr.detectChanges();
  }

  resetTransform() {
    resetViewerTransform(this.viewerState);
  }

  public getTransform(): string {
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

  private get viewerState(): ViewerTransformState {
    return this;
  }

  /* Función para alternar pantalla completa */
  toggleFullscreen(): void {
    const modal = this.pseudoModal.nativeElement as HTMLElement;

    const isFullscreen = !!document.fullscreenElement;

    if (isFullscreen) {
      document.exitFullscreen?.();
    } else {
      modal.requestFullscreen?.();
    }
  }
}
