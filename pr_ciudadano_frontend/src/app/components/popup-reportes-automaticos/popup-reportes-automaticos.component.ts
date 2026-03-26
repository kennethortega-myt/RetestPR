import { Component, inject, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";
import { DataPopupReportesAutomaticos } from "../../interfaces/data-popup-reportes-automaticos";
import { ReporteService } from "../../services/elecciones-generales/reporte.service";
import { ReporteAutomaticoPaginado, ReporteEstructura } from "../../interfaces/response/reportes-automaticos.interface";
import { DialogService } from "../../services/elecciones-generales/dialog.service";
import { ReporteApiService } from "../../services/elecciones-generales/reporte-api.service";
import { catchError, of, take } from "rxjs";
import { BlobWithFilename } from "../actas-components/modal-visor-pdf/modal-visor-pdf.interface";
import {HttpResponse} from "@angular/common/http";
import { PopupArchivoNoDisponibleComponent } from "../popup-archivo-no-disponible/popup-archivo-no-disponible.component";

interface PaginaItem {
    grupo: number;
    pagina: number;
    seleccionado?: boolean;
}

@Component({
  selector: "app-popup-reportes-automaticos",
  templateUrl: "./popup-reportes-automaticos.component.html",
  standalone: false,
})
export class PopupReportesAutomaticosComponent implements OnInit {
  public paginaActual = 0;
  public sizePagina = 10;
  public totalPaginasReales = 0;
  public totalRegistros = 0;

  public listaPagina: PaginaItem[] = [];
  public listaPaginaTotal: PaginaItem[] = [];

  public habilitarBotonAnterior = false;
  public habilitarBotonSiguiente = false;

  public loadingPaginacion = false;

  private readonly dialogService = inject(DialogService);
  private readonly reporteService = inject(ReporteService);
  private readonly reporteApiService = inject(ReporteApiService);

  public reportesAutomaticos: ReporteEstructura;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: DataPopupReportesAutomaticos
  ){}

  ngOnInit(): void {
    this.listarReportes();
  }

  listarReportes(): void {
    this.loadingPaginacion = true;

    this.reporteService
      .listarReportesAutomaticosPorTipoEleccion(
        {
          usuarioConsulta: 'automatico',
          tipoEleccion: +this.data.codigoTipoEleccion
        },
        {
          pagina: this.paginaActual,
          tamanio: this.sizePagina
        }
      )
      .pipe(take(1))
        .subscribe({
          next: (result: HttpResponse<ReporteAutomaticoPaginado>) => {
            const data = result.body.data;

            if (!data) {
                this.loadingPaginacion = false;
                return;
            }

            this.reportesAutomaticos = data;
            this.totalRegistros = data.totalRegistros;
            this.totalPaginasReales = data.totalPaginas;

            this.crearListaPaginado(this.totalRegistros, this.paginaActual);
            this.loadingPaginacion = false;
          },
          error: () => {
            this.loadingPaginacion = false;
          }
        });
  }

  crearListaPaginado(totalRegistros: number, pagina: number): void {
    pagina = pagina + 1;

    const totalPaginas = Math.ceil(totalRegistros / this.sizePagina);
    let contPaginaAcumulado = 1;
    let grupo = 1;
    const paginasPorGrupo = 4;

    this.listaPaginaTotal = [];

    while (contPaginaAcumulado <= totalPaginas) {
      this.listaPaginaTotal.push({
        grupo,
        pagina: contPaginaAcumulado
      });

      if (contPaginaAcumulado % paginasPorGrupo === 0) {
        grupo++;
      }

      contPaginaAcumulado++;
    }

    const actual = this.listaPaginaTotal.find(p => p.pagina === pagina);
    if (actual) {
      this.obtenerListaPaginaPorGrupo(this.listaPaginaTotal, actual.grupo, pagina);
    }
  }

  obtenerListaPaginaPorGrupo(lista: PaginaItem[], grupo: number, pagina: number): void {
    const maxGrupo = Math.max(...lista.map(p => p.grupo));
    const minGrupo = Math.min(...lista.map(p => p.grupo));

    this.habilitarBotonAnterior = grupo > minGrupo;
    this.habilitarBotonSiguiente = grupo < maxGrupo;

    this.listaPagina = lista
      .filter(p => p.grupo === grupo)
      .map(p => ({
        ...p,
        seleccionado: p.pagina === pagina
      }));
  }

  paginado(numero: number): void {
    this.paginaActual = numero - 1;
    this.listarReportes();
  }

  eventoPaginaAnterior(): void {
    this.listaPaginaTotal = [];
    this.paginaActual = this.listaPagina[0].pagina - 2;
    this.listarReportes();
  }

  eventoPaginaSiguiente(): void {
    this.listaPaginaTotal = [];
    if (this.listaPagina.length === 0) {
      return;
    }    
    this.paginaActual = this.listaPagina[this.listaPagina.length - 1].pagina;
    this.listarReportes();
  }

  descargarArchivo(id: string): void {
    this.reporteApiService.descargarZipReportesAutomaticosPorUUID(id)
      .pipe(take(1),
      catchError(() => {
            return of(null);
          })
      )
      .subscribe((result: BlobWithFilename | null) => {
        if (!result || result.size === 0) {
          this.dialogService.openComponentData
          (PopupArchivoNoDisponibleComponent)
           return;
        }    
        const link = document.createElement("a");
        link.href = result.url;        
        const filename = result.filename || `${id}.zip`;
        link.download = filename;        
        link.target = "_blank";
        link.click();
        URL.revokeObjectURL(result.url);
        link.remove();
      });
  }

  cerrarPopup(): void {
    this.dialogService.cerrarUltimoDialog();
    // Loading functionality removed
  }

}
