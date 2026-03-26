import { FormControl } from "@angular/forms";

export class Eleccion {
  id?: number;
  nombre?: string;
}

export interface FiltroUbigeoData {
  tipoEleccion: number;
  tipoReporte?: number;
  mostrarUbigeo?: boolean;
  mostrarReporte?: boolean;

  distritoElectoral?: number;
  ambitoGeografico?: number;
  nivelUbigeoUno?: string;
  nivelUbigeoDos?: string;
  nivelUbigeoTres?: string;
  esEleccionParaDistritoElectoral?: boolean;

  distritoElectoralText?: string;
  nivelUbigeoUnoText?: string;
  nivelUbigeoDosText?: string;
  nivelUbigeoTresText?: string;
}

export interface FiltroEleccionData {
  tipoEleccion: number;
  tipoReporte?: number;
  mostrarReporte?: boolean;
}

export interface PopupFiltroEleccionForm {
  tipoEleccion: FormControl<number>;
  tipoReporte: FormControl<number>;
}

export interface PopupFiltroUbigeoForm {
  distritoElectoral: FormControl<number>;
  ambitoGeografico: FormControl<number>;
  nivelUbigeoUno: FormControl<string>;
  nivelUbigeoDos: FormControl<string>;
  nivelUbigeoTres: FormControl<string>;
}
