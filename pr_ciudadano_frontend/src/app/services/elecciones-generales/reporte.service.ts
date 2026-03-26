import { Injectable } from "@angular/core";
import { HttpErrorResponse, HttpResponse, HttpStatusCode } from "@angular/common/http";
import { Observable, catchError, map, of } from "rxjs";
import saveAs from "file-saver";

import { catchErrorHandler$ } from "../common/catchErrorHandler";
import { RequestsService } from "../common/requests.service";
import {
  IPDFReportParams,
  IPDFReportParamsForPartCiud,
  ModulesForPDFReportType,
  IFileResponse,
  IDescargarPdfCommonParams,
  IListarReportesAutomaticosParams,
  IListarReportesAutomaticosBody,
} from "../../interfaces/reporte.interfaces";
import { URL_FOR_MODULE_PDF_REPORT, NAME_FOR_MODULE_PDF_REPORT } from "../../helpers/report.constants";
import { getCurrentDateTime } from "../../helpers/datetime-helper.common";
import { ReporteApiService } from "./reporte-api.service";
import { DetalleReporteAutomaticoPorTipoEleccion } from "../../interfaces/response/detalle-por-tipo-eleccion.interface";
import { ReporteAutomaticoPaginado } from "../../interfaces/response/reportes-automaticos.interface";

@Injectable({
  providedIn: "root",
})
export class ReporteService {
  constructor(private readonly request: RequestsService, private reporteApiService: ReporteApiService) {}

  /**
   * Generic Method to download PDF Report directly
   * @param params
   * @param pdfReportType
   * @returns
   */
  public downloadReporteBlob_old$(
    params: IPDFReportParams | IPDFReportParamsForPartCiud,
    pdfReportType: ModulesForPDFReportType
  ): Observable<IFileResponse> {
    const currentURL = URL_FOR_MODULE_PDF_REPORT[pdfReportType];
    const currentPDFName = NAME_FOR_MODULE_PDF_REPORT[pdfReportType];
    let mediaType = "application/pdf";
    return this.request
      .postPDFReport(currentURL, params)
      .pipe(
        map((response) => {
          let blob = new Blob([response], { type: mediaType });
          saveAs(blob, `${currentPDFName}${getCurrentDateTime()}.pdf`);
          return { success: true };
        })
      )
      .pipe(catchError(catchErrorHandler$));
  }

  public downloadReporteBlob$(
    params: IPDFReportParams | IPDFReportParamsForPartCiud,
    pdfReportType: ModulesForPDFReportType
  ): Observable<IFileResponse> {
    const currentURL = URL_FOR_MODULE_PDF_REPORT[pdfReportType];
    const currentPDFName = NAME_FOR_MODULE_PDF_REPORT[pdfReportType];
    let mediaType = params.tipoReporte == 1 ? "application/pdf" : "text/csv";
    const extesion = params.tipoReporte == 1 ? ".pdf" : ".csv";
    return this.request
      .postPDFHttpRequest(currentURL, params)
      .pipe(
        map((newResponse) => {
          const { body } = newResponse;
          let blob = new Blob([body], { type: mediaType });
          saveAs(blob, `${currentPDFName}${getCurrentDateTime()}${extesion}`);
          return { success: true };
        })
      )
      .pipe(
        catchError((errorResponse: HttpErrorResponse) => {
          const { status } = errorResponse;
          if (status == HttpStatusCode.InternalServerError) {
            return of({ success: false, reportErrorType: "no_firma_digital" } as IFileResponse);
          }
          return of({ success: false });
        })
      );
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
    return this.reporteApiService.descargarPdfEleccionDiputados(
      tipoReporte,
      idAmbitoGeografico,
      idDistritoElectoral,
      idEleccion,
      tipoFiltro,
      nombreProceso,
      nombreEleccion
    );
  }

  descargarPdfEleccionDiputadosCandidato(params: IDescargarPdfCommonParams): Observable<Blob> {
    return this.reporteApiService.descargarPdfEleccionDiputadosCandidato(params);
  }

  descargarPdfEleccionDiputadosCandidatoOrganizacion(params: IDescargarPdfCommonParams): Observable<Blob> {
    return this.reporteApiService.descargarPdfEleccionDiputadosCandidatoOrganizacion(params);
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
    return this.reporteApiService.descargarPdfEleccionSenadoresMultiple(
      tipoReporte,
      idAmbitoGeografico,
      idDistritoElectoral,
      idEleccion,
      tipoFiltro,
      nombreProceso,
      nombreEleccion
    );
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
    return this.reporteApiService.descargarPdfEleccionSenadoresMultipleCandidato(
      tipoReporte,
      idAmbitoGeografico,
      idDistritoElectoral,
      idEleccion,
      nombreProceso,
      nombreEleccion,
      tipoFiltro
    );
  }

  descargarPdfEleccionSenadoresMultipleCandidatoOrganizacion(params: IDescargarPdfCommonParams): Observable<Blob> {
    return this.reporteApiService.descargarPdfEleccionSenadoresMultipleCandidatoOrganizacion(params);
  }

  obtenerReportesAutomaticosPorTipoEleccion(id: number): Observable<HttpResponse<DetalleReporteAutomaticoPorTipoEleccion[]>> {
    return this.reporteApiService.obtenerReportesAutomaticosPorTipoEleccion(id);
  }

  listarReportesAutomaticosPorTipoEleccion(
    body: IListarReportesAutomaticosBody, 
    params?: IListarReportesAutomaticosParams
  ): Observable<HttpResponse<ReporteAutomaticoPaginado>> {
    return this.reporteApiService.listarReportesAutomaticosPorTipoEleccion(body, params);
  }
}
