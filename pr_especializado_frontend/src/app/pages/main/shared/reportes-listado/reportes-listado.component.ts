import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { TranslateModule } from '@ngx-translate/core';
import { ComponentsModule } from '../../../../components/components.module';
import { TipoEleccion } from '../../../../interfaces/output/tipo-eleccion.model';
import {
  getEstadoIconByRow,
  getRepositorioIcon,
  hasRepositorio,
  ReportesListadoRow,
  ReportesListadoTableInfo
} from '../../../../helpers/reportes-list.helper';
import { catchError, of, take } from 'rxjs';
import { ReporteApiService } from '../../../../services/reporte-api.service';
import { DialogService } from '../../../../services/dialog.service';
import { ARCHIVO_NO_DISPONIBLE, REPORTE_NO_DISPONIBLE } from '../../../../helpers/constantes';
import { FechaApiService } from '../../../../services/fecha.service';
import { Fecha, FechaResponse } from '../../../../interfaces/fecha';
import { HttpErrorResponse } from '@angular/common/http';
import { PorcentajeFormatPipe } from '../../../../pipes/porcentaje-format.pipe';
import { ELECCION_DEFAULT, REPORTE_DEFAULT } from '../../../../helpers/enums';

@Component({
  selector: 'app-reportes-listado',
  standalone: true,
  templateUrl: './reportes-listado.component.html',
  styleUrl: './reportes-listado.component.scss',
  imports: [
    CommonModule,
    PorcentajeFormatPipe,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSelectModule,
    MatExpansionModule,
    TranslateModule,
    ComponentsModule
  ]
})
export class ReportesListadoComponent implements OnInit {
  private readonly reporteApiService = inject(ReporteApiService);
  private readonly dialogService = inject(DialogService);
  private readonly fechaService = inject(FechaApiService);

  @Input({ required: true }) isContentReady = false;
  @Input({ required: true }) isFirstRequest = true;
  @Input({ required: true }) isLoadingReportList = false;
  @Input({ required: true }) tableInformation!: ReportesListadoTableInfo;
  @Input({ required: true }) form!: FormGroup;
  @Input({ required: true }) titleKey!: string;
  @Input({ required: true }) totalKey!: string;
  @Input({ required: true }) emptyMessage!: string;
  @Input() showTipoReporteFilter = false;
  @Input() listaEleccion: TipoEleccion[] = [];
  @Input() listaReporte: TipoEleccion[] = [];
  @Input() displayedColumns: string[] = [
    'fecha',
    'tipoReporte',
    'tipoEleccion',
    'porcentaje',
    'ambito',
    'dc',
    'pp',
    'de',
    'estado',
    'repositorio',
  ];

  @Output() limpiar = new EventEmitter<void>();
  @Output() actualizar = new EventEmitter<void>();
  @Output() openMobileFilter = new EventEmitter<void>();
  @Output() descargar = new EventEmitter<ReportesListadoRow>();
  @Output() pageChange = new EventEmitter<PageEvent>();
  @Input() criterioSeleccionado: string = 'SELECCIONA EL CRITERIO DE BÚSQUEDA';
  fechas: Partial<Fecha> = {
       id: 0,
       fechaProceso: new Date().toISOString(),
       servicioFirma: '',
       cDescripcion: new Date()
  };
  number = Number;

  ngOnInit(): void {
    this.listarFechas();
  }
  
  onLimpiar(): void {
    this.limpiar.emit();
  }

  onActualizar(): void {
    this.actualizar.emit();
  }

  onOpenMobileFilter(): void {
    this.openMobileFilter.emit(); 
  }

  onDescargar(element: ReportesListadoRow): void {
    if (!hasRepositorio(element.repositorio)) return;
    this.descargar.emit(element);
  }

  onPageChange(event: PageEvent): void {
    this.pageChange.emit(event);
  }

  getEstadoIcon(element: ReportesListadoRow): { icon: string; alt: string } {
    return getEstadoIconByRow(element);
  }

  hasRepo(repositorio: string | number | null): boolean {
    return hasRepositorio(repositorio);
  }

  getRepoIcon(repositorio: string | number | null): string {
    return getRepositorioIcon(repositorio);
  }

  descargarReporte(tipoEleccion: TipoEleccion) {
    if (!tipoEleccion || !tipoEleccion.value) {
      return;
    }

    this.reporteApiService
      .descargarReporteCandidato(tipoEleccion.value)
      .pipe(
        take(1),
        catchError(() => of(null))
      )
      .subscribe((result: { url: string; fileName: string } | null) => {
        if (!result || !result.url) {
          this.dialogService.mostrarMensajeAdvertencia(REPORTE_NO_DISPONIBLE,ARCHIVO_NO_DISPONIBLE);
          return;
        }

        const link = document.createElement("a");
        link.href = result.url;
        link.download = result.fileName;
        link.click();

        URL.revokeObjectURL(result.url);
        link.remove();
      });
  }

  listarFechas(){
    this.fechaService.listarFecha().subscribe({
      next: (response: FechaResponse) => {
        if (!response.success) {
            this.fechas= {
              id: 0,
              fechaProceso: new Date().toISOString(),
              servicioFirma: '',
              cDescripcion: new Date()
            };
            return;
          }
          this.fechas = response.data as Fecha;
        },
        error: (error: HttpErrorResponse) => {
          this.dialogService.mostrarMensajeError(`${error.error.message}`);
        }
      });    
  }

  get ocultarLimpiar(): boolean {
    return (
      this.form.get('tipoEleccion')?.value !== ELECCION_DEFAULT ||
      this.form.get('tipoReporte')?.value !== REPORTE_DEFAULT
    );
  }
}