import { ChangeDetectorRef, Component, HostListener, inject, Input } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { take } from 'rxjs';
import { obtenerImagenForActa } from '../../../helpers/actas.obtener-imagen';
import { ACTA_LINEAT_CODIGO_ESTADO, TYPE_FOR_PDF } from '../../../helpers/constantes';
import { Archivo, Detalle, LineaTiempo, Mesa } from '../../../interfaces/acta-bean';
import { ModalVisorPdfComponent } from '../modal-visor-pdf/modal-visor-pdf.component';
import { IModalVisorConfig } from '../modal-visor-pdf/modal-visor-pdf.interface';

@Component({
  selector: 'app-linea-tiempo',
  templateUrl: './linea-tiempo.component.html',
  standalone: false
})
export class LineaTiempoComponent {
  @Input({ required: true }) detalleMesa: Mesa;
  @Input({ required: true }) detalle: Detalle[];
  @Input({ required: true }) archivos: [Archivo];
  @Input({ required: true }) lineaTiempo: [LineaTiempo];
  @Input() numeroDeActa: string;
  @Input() nombreDeActa: string;
  ACTA_LINEAT_CODIGO_ESTADO = ACTA_LINEAT_CODIGO_ESTADO;
  esPantallaChica = window.innerWidth < 960;
  mostrarDatos = false;
  buttonIsDisabled = false;
  showMapMobile: boolean = false;
  private readonly cd = inject(ChangeDetectorRef);

  constructor(private readonly dialog: MatDialog) {}

  // 🔹 Detecta cambios de tamaño de pantalla
  @HostListener('window:resize', ['$event'])
  onResize(event: UIEvent) {
    this.esPantallaChica = (event.target as Window).innerWidth < 960;
  }

  get existActa(): boolean {
    return this.archivos
      .map((file) => file.tipo)
      .some(
        (tipo) =>
          tipo == TYPE_FOR_PDF.ID_ACTA_ESCRUTINIO ||
          tipo == TYPE_FOR_PDF.ID_ACTA_INSTALACION_Y_SUFRAGIO ||
          tipo == TYPE_FOR_PDF.ID_ACTA_INSTALACION ||
          tipo == TYPE_FOR_PDF.ID_ACTA_SUFRAGIO
      );
  }

  get existResolution(): boolean {
    return this.archivos.map((file) => file.tipo).some((tipo) => tipo == TYPE_FOR_PDF.ID_RESOLUTION);
  }

  toggleMapMobile(): void {
    this.showMapMobile = !this.showMapMobile;
  }

  verActa(): void {
    const archivoForActa = this.archivos.filter(
      (file) =>
        file.tipo == TYPE_FOR_PDF.ID_ACTA_ESCRUTINIO ||
        file.tipo == TYPE_FOR_PDF.ID_ACTA_INSTALACION_Y_SUFRAGIO ||
        file.tipo == TYPE_FOR_PDF.ID_ACTA_INSTALACION ||
        file.tipo == TYPE_FOR_PDF.ID_ACTA_SUFRAGIO
    );

    const modalConfig = {
      numeroDeActa: this.numeroDeActa,
      nombreDeActa: this.nombreDeActa,
      multiple: true,
      archivos: archivoForActa,
      esPantallaChica: this.esPantallaChica
    } as IModalVisorConfig;
    this.openModal(modalConfig);
  }

  verResolucion(): void {
    const archivoForResolutions = this.archivos.filter((file) => file.tipo == TYPE_FOR_PDF.ID_RESOLUTION);
    const modalConfig = {
      numeroDeActa: this.numeroDeActa,
      nombreDeActa: this.nombreDeActa,
      multiple: true,
      archivos: archivoForResolutions,
      esPantallaChica: this.esPantallaChica,
      hideInstructions: true
    } as IModalVisorConfig;
    this.openModal(modalConfig);
  }

  obtenerImagen(codigoEstadoActa: string): string {
    return obtenerImagenForActa(codigoEstadoActa);
  }

  getDescripcionReducida(desc: string | null | undefined): string {
    if (!desc) return '';
    if(this.esPantallaChica){
      return desc;
    }
    else{
    let maxLength = 17;
    return desc.length >= maxLength ? desc.substring(0, maxLength) + '...' : desc;
    }
  }

  limpiarDescripcionRepetida(descripcion: string): string {
    const normalizar = (texto: string) =>
      texto
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .trim()
        .toLowerCase();

    const elementosUnicos = new Map<string, string>();

    descripcion.split(',').forEach((original) => {
      const clave = normalizar(original);
      if (!elementosUnicos.has(clave)) {
        elementosUnicos.set(clave, original.trim());
      }
    });

    return Array.from(elementosUnicos.values()).join(', ');
  }

  private openModal(modalConfig: IModalVisorConfig): void {
    this.buttonIsDisabled = true;

    const dialogConfig: MatDialogConfig = {
      width: '1024px',
      height: this.esPantallaChica ? 'auto' : '715px', // 👈 altura dinámica
      maxHeight: '95vh',
      panelClass: 'popup-acta',
      data: modalConfig
    };

    this.dialog
      .open<ModalVisorPdfComponent, IModalVisorConfig>(ModalVisorPdfComponent, dialogConfig)
      .afterClosed()
      .pipe(take(1))
      .subscribe(() => {
        this.buttonIsDisabled = false;
      });
  }
}
