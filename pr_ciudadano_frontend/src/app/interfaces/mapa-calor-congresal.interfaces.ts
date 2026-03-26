import { EventEmitter } from "@angular/core";
import { MapaCalor } from "./resumen-general-bean";

export interface DatosMapaDeCalor {
  id: string;
  mapaCalor?: MapaCalor[];
  esEstranjero?: boolean;
  oneFilter?: boolean;
  height?: number;
  idDistritoElectoral?: number;
  mostrarBotonMundo?: boolean;
};

export interface IMapaCongresal {
  eventClickMapa?: EventEmitter<number>;
  datos: DatosMapaDeCalor;
}
