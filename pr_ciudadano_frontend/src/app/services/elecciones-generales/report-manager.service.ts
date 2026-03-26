import { Injectable } from "@angular/core";
import { Observable, take } from "rxjs";
import { ReporteService } from "./reporte.service";
import { FilterByLocationParams, RegionValue } from "../../interfaces/filtro-settings";
import {
  IFileResponse,
  IPDFReportParams,
  IPDFReportParamsForPartCiud,
  ModulesForPDFReportType,
} from "../../interfaces/reporte.interfaces";
import { SnackbarService } from "./snackbar.service";
import { SnackbarMessageType } from "../../interfaces/snackbar.interfaces";
import {
  CONFIG_ELECTION_IDS,
  GEOGRAPHIC_SCOPE,
  MAIN_ELECTION_IDS,
  MENSAJE_REPORTE,
  UBIGEO_LEVELS,
} from "../../helpers/constantes";
import { ELECTION_NAMES_FOR_REPORT } from "../../helpers/report.constants";
import { getGeograpScopeByRegion } from "../../helpers/election-type.config";
import { getFilterTypeForBackend } from "../../helpers/ubigeo-level.common";
import { encryptStorageEleccion } from "../../settings/encrypt-storage.settings";

export interface IReportConfigParams {
  regionValue: RegionValue;
  electionId?: number;
  filters: FilterByLocationParams;
  pdfReportType?: ModulesForPDFReportType;
  idOrgPolitica?: number;
  descripcionOrgPolitica?: string;
  tipoReporte?: number;
}

@Injectable({
  providedIn: "root",
})
export class ReportManagerService {
  constructor(
    private readonly reporteService: ReporteService,
    private readonly snackbarService: SnackbarService
  ) {}

  public presentSnackbarForNoDownloadReport() {
    this.snackbarService.showSnackbarWithWarningMessage();
  }

  public showSnackbarWithCustomMessage(messageType: SnackbarMessageType) {
    this.snackbarService.showSnackbarWithWarningMessage(messageType);
  }

  /**
   * GENERACIÓN DE REPORTE PARA EL MÓDULO DE RESUMEN GENERAL
   * @param reportConfigParams
   */
  public downloadReportePDFResumenGeneral(reportConfigParams: IReportConfigParams) {
    const { electionId } = reportConfigParams;
    reportConfigParams.pdfReportType = this.getElectoralPDFReportType(electionId);
    const selectedConfigElectionId = CONFIG_ELECTION_IDS.find((elem) => elem.electionId == electionId);

    if (selectedConfigElectionId) {
      const params = this.isSpecialElectionType(electionId)
        ? this.getCustomParamsToReporterRequestForElectoralDistric(reportConfigParams)
        : this.getCustomParamsToReporterRequest(reportConfigParams);

      // AGREGAR AQUÍ LOS NUEVOS REQUEST PARA DESCARGAR REPORTE
      let observableRequest: Observable<IFileResponse>;
      if (selectedConfigElectionId.electionId == MAIN_ELECTION_IDS.presidenciales) {
        observableRequest = this.reporteService.downloadReporteBlob$(params, "Presidencial_Resumen_General");
      } else if (selectedConfigElectionId.electionId == MAIN_ELECTION_IDS.parlamento_andino) {
        observableRequest = this.reporteService.downloadReporteBlob$(params, "Parlamento_Resumen_General");
      } else if (selectedConfigElectionId.electionId == MAIN_ELECTION_IDS.senadores_33) {
        observableRequest = this.reporteService.downloadReporteBlob$(params, "Senadores_33_Resumen_General");
      } else if (selectedConfigElectionId.electionId == MAIN_ELECTION_IDS.senadores_27) {
        observableRequest = this.reporteService.downloadReporteBlob$(params, "Senadores27_Resumen_General");
      } else if (selectedConfigElectionId.electionId == MAIN_ELECTION_IDS.diputados) {
        observableRequest = this.reporteService.downloadReporteBlob$(params, "Diputados_Resumen_General");
      }

      if (observableRequest) {
        // Loading functionality removed
        observableRequest.pipe(take(1)).subscribe((response) => {
          // Loading functionality removed
          if (response.success) {
            this.snackbarService.showSnackbarWithSuccessMessage();
          } else {
            console.log("error getReporteDeEleccionPresidencialBlob");
            if (response.reportErrorType == "no_firma_digital") {
              this.snackbarService.showSnackbarWithSuccessMessage(MENSAJE_REPORTE.SIN_FIRMA_DIGITAL, "ambar");
            }
          }
        });
      } else {
        // Loading functionality removed
        console.log("este ID no tiene un request asignado para descargar reporte");
      }
    } else {
      // Loading functionality removed
      console.log("no existe el ID seleccionado en resumen general");
    }
  }

  /**
   * MÉTODO PRINCIPAL PARA MOSTRAR PDF EN EL MODAL O DESCARGAR
   * Nuevo método para la generación y muestra en modal de un reporte en PDF
   * @param reportConfigParams
   */
  public presentReportePDFModal(reportConfigParams: IReportConfigParams) {
    const { pdfReportType } = reportConfigParams;
    const params = this.getCustomParamsToReporterRequest(reportConfigParams);
    // Loading functionality removed
    this.reporteService
      .downloadReporteBlob$(params, pdfReportType)
      .pipe(take(1))
      .subscribe((response) => {
        // Loading functionality removed
        if (response.success) {
          this.snackbarService.showSnackbarWithSuccessMessage();
        } else {
          console.log("error getReporteDeEleccionPresidencialBlob");
          if (response.reportErrorType == "no_firma_digital") {
            this.snackbarService.showSnackbarWithSuccessMessage(MENSAJE_REPORTE.SIN_FIRMA_DIGITAL, "ambar");
          }
        }
      });
  }

  public presentReportePDFModalForPartCiud(reportConfigParams: IReportConfigParams) {
    const { pdfReportType } = reportConfigParams;
    const params = this.getCustomParamsToReporterRequestForPartCiud(reportConfigParams);
    // Loading functionality removed
    this.reporteService
      .downloadReporteBlob$(params, pdfReportType)
      .pipe(take(1))
      .subscribe((response) => {
        // Loading functionality removed
        if (response.success) {
          this.snackbarService.showSnackbarWithSuccessMessage();
        } else {
          console.log("error getReporteDeEleccionPresidencialBlob");
          if (response.reportErrorType == "no_firma_digital") {
            this.snackbarService.showSnackbarWithSuccessMessage(MENSAJE_REPORTE.SIN_FIRMA_DIGITAL, "ambar");
          }
        }
      });
  }

  /**
   * MÉTODO SECUNDARIO PARA MOSTRAR PDF EN EL MODAL O DESCARGAR
   * Nuevo método para la generación y muestra en modal de un reporte en PDF
   * @param regionValue
   * @param electionId
   * @param filters
   * @param pdfReportType
   */
  public showReportePDFModal(
    regionValue: RegionValue,
    electionId: number,
    filters: FilterByLocationParams,
    pdfReportType: ModulesForPDFReportType,
    tipoReporte: number = 1
  ) {
    const params = this.getParamsToReporterRequest(tipoReporte, regionValue, electionId, filters, pdfReportType);
    // Loading functionality removed
    this.reporteService
      .downloadReporteBlob$(params, pdfReportType)
      .pipe(take(1))
      .subscribe((response) => {
        // Loading functionality removed
        if (response.success) {
          this.snackbarService.showSnackbarWithSuccessMessage();
        } else {
          console.log("error getReporteDeEleccionPresidencialBlob");
          if (response.reportErrorType == "no_firma_digital") {
            this.snackbarService.showSnackbarWithSuccessMessage(MENSAJE_REPORTE.SIN_FIRMA_DIGITAL, "ambar");
          }
        }
      });
  }

  // PRIVATE METHODS

  private getParamsToReporterRequest(
    tipoReporte: number,
    regionValue: RegionValue,
    electionId: number,
    filters: FilterByLocationParams,
    pdfReportType: ModulesForPDFReportType
  ): IPDFReportParams {
    let params: IPDFReportParams;
    if (regionValue == "TODOS") {
      params = {
        tipoReporte: tipoReporte,
        idEleccion: electionId,
        tipoFiltro: UBIGEO_LEVELS.ELECTION,
        nombreProceso: this.getActiveElectoralProccessName(),
        nombreEleccion: ELECTION_NAMES_FOR_REPORT[pdfReportType],
      } as IPDFReportParams;
    } else {
      params = {
        tipoReporte: tipoReporte,
        idAmbitoGeografico: getGeograpScopeByRegion(regionValue),
        idEleccion: electionId,
        tipoFiltro: getFilterTypeForBackend(filters),
        ubigeoNivel1: filters.departmentUbigeoId,
        ubigeoNivel2: filters.provinceUbigeoId,
        ubigeoNivel3: filters.districtUbigeoId,
        nombreProceso: this.getActiveElectoralProccessName(),
        nombreEleccion: ELECTION_NAMES_FOR_REPORT[pdfReportType],
      };
    }
    return params;
  }

  private getElectoralPDFReportType(electionId: number): ModulesForPDFReportType {
    if (electionId == MAIN_ELECTION_IDS.presidenciales) {
      return "Presidencial_Resumen_General";
    } else if (electionId == MAIN_ELECTION_IDS.parlamento_andino) {
      return "Parlamento_Resumen_General";
    } else if (electionId == MAIN_ELECTION_IDS.senadores_33) {
      return "Senadores_33_Resumen_General";
    } else if (electionId == MAIN_ELECTION_IDS.senadores_27) {
      return "Senadores27_Resumen_General";
    } else if (electionId == MAIN_ELECTION_IDS.diputados) {
      return "Diputados_Resumen_General";
    }

    return "Presidencial_Resumen_General"; // este caso no debería darse nunca
  }

  private getCustomParamsToReporterRequest(reportConfigParams: IReportConfigParams): IPDFReportParams {
    const { electionId, filters, regionValue, descripcionOrgPolitica, idOrgPolitica, pdfReportType } =
      reportConfigParams;
    let params: IPDFReportParams;
    if (regionValue == "TODOS") {
      params = {
        tipoReporte: reportConfigParams.tipoReporte,
        idEleccion: electionId,
        tipoFiltro: UBIGEO_LEVELS.ELECTION,
        descripcionOrgPolitica,
        idOrgPolitica,
        nombreProceso: this.getActiveElectoralProccessName(),
        nombreEleccion: ELECTION_NAMES_FOR_REPORT[pdfReportType],
      } as IPDFReportParams;
    } else {
      params = {
        tipoReporte: reportConfigParams.tipoReporte,
        idAmbitoGeografico: getGeograpScopeByRegion(regionValue),
        idEleccion: electionId,
        tipoFiltro: getFilterTypeForBackend(filters),
        ubigeoNivel1: filters.departmentUbigeoId,
        ubigeoNivel2: filters.provinceUbigeoId,
        ubigeoNivel3: filters.districtUbigeoId,
        descripcionOrgPolitica,
        idOrgPolitica,
        nombreProceso: this.getActiveElectoralProccessName(),
        nombreEleccion: ELECTION_NAMES_FOR_REPORT[pdfReportType],
      };
    }
    return params;
  }

  private getCustomParamsToReporterRequestForPartCiud(
    reportConfigParams: IReportConfigParams
  ): IPDFReportParamsForPartCiud {
    const { filters, regionValue, descripcionOrgPolitica, idOrgPolitica, pdfReportType } = reportConfigParams;
    let params: IPDFReportParamsForPartCiud;
    if (regionValue == "TODOS") {
      params = {
        tipoReporte: reportConfigParams.tipoReporte,
        tipoFiltro: UBIGEO_LEVELS.TOTAL,
        descripcionOrgPolitica,
        idOrgPolitica,
        nombreProceso: this.getActiveElectoralProccessName(),
        nombreEleccion: ELECTION_NAMES_FOR_REPORT[pdfReportType],
      } as IPDFReportParamsForPartCiud;
    } else {
      params = {
        tipoReporte: reportConfigParams.tipoReporte,
        idAmbitoGeografico: getGeograpScopeByRegion(regionValue),
        tipoFiltro: getFilterTypeForBackend(filters),
        ubigeoNivel01: filters.departmentUbigeoId,
        ubigeoNivel02: filters.provinceUbigeoId,
        ubigeoNivel03: filters.districtUbigeoId,
        descripcionOrgPolitica,
        idOrgPolitica,
        nombreProceso: this.getActiveElectoralProccessName(),
        nombreEleccion: ELECTION_NAMES_FOR_REPORT[pdfReportType],
      } as IPDFReportParamsForPartCiud;
    }
    return params;
  }

  private getCustomParamsToReporterRequestForElectoralDistric(
    reportConfigParams: IReportConfigParams
  ): IPDFReportParams {
    const { electionId, filters, descripcionOrgPolitica, idOrgPolitica, pdfReportType } = reportConfigParams;
    let params: IPDFReportParams = {
      idAmbitoGeografico: GEOGRAPHIC_SCOPE,
      idEleccion: electionId,
      tipoFiltro: UBIGEO_LEVELS.DISTRITO_ELECTORAL,
      idDistritoElectoral: filters.electoralDistrictId,
      descripcionOrgPolitica,
      idOrgPolitica,
      nombreProceso: this.getActiveElectoralProccessName(),
      nombreEleccion: ELECTION_NAMES_FOR_REPORT[pdfReportType],
      tipoReporte: reportConfigParams.tipoReporte,
    } as IPDFReportParams;
    return params;
  }

  private isSpecialElectionType(electionId: number): boolean {
    return this.isSpecialElectionType(electionId);
  }

  private getActiveElectoralProccessName(): string {
    let objProceso = JSON.parse(encryptStorageEleccion.getItem("PROCESO_ELECTORAL_ACTIVO"));
    return objProceso.nombre ?? "ELECCIONES GENERALES 2021";
  }
}
