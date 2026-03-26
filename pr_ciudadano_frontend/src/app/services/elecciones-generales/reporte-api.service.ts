import { Injectable } from "@angular/core";
import { Observable, Subject } from "rxjs";
import { environment } from "../../../environments/environment";
import { IDescargarPdfCommonParams, IListarReportesAutomaticosBody, IListarReportesAutomaticosParams } from "../../interfaces/reporte.interfaces";
import { RequestsService } from "../common/requests.service";
import { DetalleReporteAutomaticoPorTipoEleccion } from "../../interfaces/response/detalle-por-tipo-eleccion.interface";
import { HttpResponse } from "@angular/common/http";
import { ReporteAutomaticoPaginado } from "../../interfaces/response/reportes-automaticos.interface";
import { BlobWithFilename } from "../../components/actas-components/modal-visor-pdf/modal-visor-pdf.interface";

@Injectable({
  providedIn: "root",
})
export class ReporteApiService {
  private urlServidor: string;
  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(private readonly httpClient: RequestsService) {
    this.urlServidor = environment.apiUrlLocal;
  }

  descargarPdfEleccionDiputados(
    tipoReporte: number,
    idAmbitoGeografico: number,
    idDistritoElectoral: number,
    idEleccion: number,
    tipoFiltro: string,
    nombreProceso: string,
    nombreEleccion: string
  ): Observable<Blob> {
    let request: any = {
      tipoReporte: tipoReporte,
      idAmbitoGeografico: idAmbitoGeografico,
      idDistritoElectoral: idDistritoElectoral,
      idEleccion: idEleccion,
      tipoFiltro: tipoFiltro,
      nombreProceso: nombreProceso,
      nombreEleccion: nombreEleccion,
    };
    return this.httpClient.postPdf(this.urlServidor + `reportes/eleccion-diputados`, request);
  }

  descargarPdfEleccionDiputadosCandidato(params: IDescargarPdfCommonParams): Observable<Blob> {
    return this.httpClient.postPdf(this.urlServidor + `reportes/eleccion-diputados-candidato`, params);
  }

  descargarPdfEleccionDiputadosCandidatoOrganizacion(params: IDescargarPdfCommonParams): Observable<Blob> {
    return this.httpClient.postPdf(this.urlServidor + `reportes/eleccion-diputados-candidato-organizacion`, params);
  }

  descargarPdfEleccionSenadoresMultiple(
    tipoReporte: number,
    idAmbitoGeografico: number,
    idDistritoElectoral: number,
    idEleccion: number,
    tipoFiltro: string,
    nombreProceso: string,
    nombreEleccion: string
  ): Observable<Blob> {
    let request: any = {
      tipoReporte: tipoReporte,
      idAmbitoGeografico: idAmbitoGeografico,
      idDistritoElectoral: idDistritoElectoral,
      idEleccion: idEleccion,
      tipoFiltro: tipoFiltro,
      nombreProceso: nombreProceso,
      nombreEleccion: nombreEleccion,
    };
    return this.httpClient.postPdf(this.urlServidor + `reportes/eleccion-senadores-multiple`, request);
  }

  descargarPdfEleccionSenadoresMultipleCandidato(
    tipoReporte: number,
    idAmbitoGeografico: number,
    idDistritoElectoral: number,
    idEleccion: number,
    nombreProceso: string,
    nombreEleccion: string,
    tipoFiltro: string
  ): Observable<Blob> {
    let request: any = {
      tipoReporte: tipoReporte,
      idAmbitoGeografico: idAmbitoGeografico,
      idDistritoElectoral: idDistritoElectoral,
      idEleccion: idEleccion,
      tipoFiltro: tipoFiltro,
      nombreProceso: nombreProceso,
      nombreEleccion: nombreEleccion,
    };
    return this.httpClient.postPdf(this.urlServidor + `reportes/eleccion-senadores-multiple-candidato`, request);
  }

  descargarPdfEleccionSenadoresMultipleCandidatoOrganizacion(params: IDescargarPdfCommonParams): Observable<Blob> {
    return this.httpClient.postPdf(
      this.urlServidor + `reportes/eleccion-senadores-multiple-candidato-organizacion`,
      params
    );
  }

  obtenerReportesAutomaticosPorTipoEleccion(id: number): Observable<HttpResponse<DetalleReporteAutomaticoPorTipoEleccion[]>> {
    return this.httpClient.get(
      this.urlServidor + `reportes/detalle-tipo-eleccion/${id}`,
    );
  }

  listarReportesAutomaticosPorTipoEleccion(
    body: IListarReportesAutomaticosBody,
    params: IListarReportesAutomaticosParams = {
      tamanio: 10,
      pagina: 0
    }
  ): Observable<HttpResponse<ReporteAutomaticoPaginado>>
  {
    return this.httpClient.postBodyWithParams(
      this.urlServidor + `reportes/automatico-paginado`,
      body,
      undefined,
      params
    );
  }

  descargarZipReportesAutomaticosPorUUID(id: string): Observable<BlobWithFilename> {
    return this.httpClient.getArchivo(
      this.urlServidor + `reportes/file?id=${id}`,"application/zip"
    );
  }

  descargarReporteCandidato(id: number): Observable<BlobWithFilename> {
    return this.httpClient.getArchivo(
      this.urlServidor + `reportes/reporteCandidato?id=${id}`, "text/csv"
    );
  }
}
