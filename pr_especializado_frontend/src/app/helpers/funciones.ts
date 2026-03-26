import { MatPaginatorIntl } from '@angular/material/paginator';
import * as am5 from '@amcharts/amcharts5';
import { DatePipe } from '@angular/common';

import { ObtenerTotalesResumenGeneralInput } from '../interfaces/input/resumen-general/obtener-totales-resumen-general-input';
import { ObtenerTotalesResumenGeneralObservadasInput } from '../interfaces/input/resumen-general/obtener-totales-resumen-general-observadas-input';
import { Base } from '../interfaces/output/base.model';
import { ResumenTotal } from '../interfaces/output/resumen-total.model';
import { EnumIdAmbito, EnumTipoFiltro } from './enums';
import { FiltroModel } from '../interfaces/filtro.model';
import { ChartPie } from '../interfaces/chart-pie.model';

interface RGBValue {
  red: number;
  green: number;
  blue: number;
}
export function getDegradatedColorFromPercentage(
  percentage: number,
  colors: { init: string; end: string }
): string {
  const initialColor = convertHexToRGB(colors.init);
  const finalColor = convertHexToRGB(colors.end);
  const newColor: RGBValue = {
    blue: getNewValueInRange(
      initialColor.blue,
      finalColor.blue,
      percentage / 100
    ),
    green: getNewValueInRange(
      initialColor.green,
      finalColor.green,
      percentage / 100
    ),
    red: getNewValueInRange(initialColor.red, finalColor.red, percentage / 100),
  };
  const newColorHex = convertRGBToHex(
    newColor.red,
    newColor.green,
    newColor.blue
  );
  return newColorHex;
}

function convertHexToRGB(color: string): RGBValue {
  const red = Number.parseInt(color.substring(1, 3), 16);
  const green = Number.parseInt(color.substring(3, 5), 16);
  const blue = Number.parseInt(color.substring(5, 7), 16);
  return { red, green, blue };
}

function convertRGBToHex(red: number, green: number, blue: number): string {
  const hexRed = red.toString(16);
  const stringRed = hexRed.length === 1 ? '0' + hexRed : hexRed;
  const hexGreen = green.toString(16);
  const stringGreen = hexGreen.length === 1 ? '0' + hexGreen : hexGreen;
  const hexBlue = blue.toString(16);
  const stringBlue = hexBlue.length === 1 ? '0' + hexBlue : hexBlue;
  return '#' + stringRed + stringGreen + stringBlue;
}

function getNewValueInRange(init: number, end: number, value: number): number {
  return Math.round(init + (end - init) * value);
}

export function obtenerDatos(param: Base): Base | null {
  if (!param) {
    const result: Base = new Base();
    result.success = false;
    result.message = '';
    return result;
  }
  const result = param;
  result.data = param.data ?? null;
  return result;
}

export function mapearCamposResumenTotales(
  data: FiltroModel
): ObtenerTotalesResumenGeneralInput {
  const tipoFiltro = obtenerTipoFiltroResumenTotales(data);

  if (EnumTipoFiltro.DISTRITO_ELECTORAL === tipoFiltro) {
    return {
      idEleccion : data.idTipoEleccion,
      tipoFiltro : tipoFiltro,
      idDistritoElectoral : data.idDistritoElectoral,
    }
  }

  if (EnumTipoFiltro.AMBITO_GEOGRAFICO === tipoFiltro) {
    return {
      idEleccion : data.idTipoEleccion,
      tipoFiltro : tipoFiltro,
      idAmbitoGeografico : data.idAmbitoGeografico,
    }
  } else {
    return {
      idEleccion : data.idTipoEleccion,
      tipoFiltro : tipoFiltro,
      idAmbitoGeografico : data.idAmbitoGeografico,
      idUbigeoDepartamento : data.idUbigeoNivel01,
      idUbigeoProvincia : data.idUbigeoNivel02,
      idUbigeoDistrito : data.idUbigeoNivel03,
    }
  }
}

export function mapearCamposResumenTotalesObservadas(
  data: FiltroModel
): ObtenerTotalesResumenGeneralObservadasInput {
  const tipoFiltro = obtenerTipoFiltroResumenTotales(data);

  const param: ObtenerTotalesResumenGeneralObservadasInput =
    new ObtenerTotalesResumenGeneralObservadasInput();

  if (EnumTipoFiltro.DISTRITO_ELECTORAL === tipoFiltro) {
    return {
      idEleccion : data.idTipoEleccion,
      idDistritoElectoral : data.idDistritoElectoral,
    }
  }

  if (EnumTipoFiltro.AMBITO_GEOGRAFICO === tipoFiltro) {
    return {
      idEleccion : data.idTipoEleccion,
      idAmbitoGeografico : data.idAmbitoGeografico,
    }
  } else {
    param.idEleccion = data.idTipoEleccion;
    param.idAmbitoGeografico = data.idAmbitoGeografico;
    param.ubigeoNivel01 = data.idUbigeoNivel01;
    param.ubigeoNivel02 = data.idUbigeoNivel02;
    param.idUbigeo = data.idUbigeoNivel03;
    param.codigoLocalVotacion = data.idLocalVotacion;
  }

  return param;
}

export function obtenerTipoFiltroResumenTotales(param: FiltroModel): string {
  if (param.esEleccionParaDistritoElectoral) {
    return EnumTipoFiltro.DISTRITO_ELECTORAL;
  } else if (param.idUbigeoNivel03 !== '0') {
    return EnumTipoFiltro.UBIGEO_NIVEL_03;
  } else if (param.idUbigeoNivel02 !== '0') {
    return EnumTipoFiltro.UBIGEO_NIVEL_02;
  } else if (param.idUbigeoNivel01 !== '0') {
    return EnumTipoFiltro.UBIGEO_NIVEL_01;
  } else if (param.idAmbitoGeografico === EnumIdAmbito.TODOS) {
    return EnumTipoFiltro.ELECCION;
  } else {
    return EnumTipoFiltro.AMBITO_GEOGRAFICO;
  }
}

export function cargarGraficaPieActas(resumenTotal: ResumenTotal): ChartPie[] {
  return [
    {
      nombre: 'Contabilizadas',
      valor: Number(resumenTotal?.contabilizadas),
      sliceSettings: {
        fill: am5.color('#1d3c81'),
      },
    },
    {
      nombre: 'Enviadas al JEE',
      valor: Number(resumenTotal?.enviadasJee),
      sliceSettings: {
        fill: am5.color('#6DB2E2'),
      },
    },
    {
      nombre: 'Sin procesar',
      valor: Number(resumenTotal?.pendientesJee),
      sliceSettings: {
        fill: am5.color('#FFF'),
      },
    },
  ];
}
export function cargarGraficaPieActasModeloDos(
  resumenTotal: ResumenTotal
): ChartPie[] {
  return [
    {
      nombre: 'Contabilizadas',
      valor: Number(resumenTotal?.contabilizadas),
      sliceSettings: {
        fill: am5.color('#1d3c81'),
      },
    },
    {
      nombre: 'Para envío al JEE',
      valor: Number(resumenTotal?.enviadasJee),
      sliceSettings: {
        fill: am5.color('#6DB2E2'),
      },
    },
  ];
}
export function cargarGraficaPieParticipacionCiudadana(
  resumenTotal: ResumenTotal
): ChartPie[] {
  return [
    {
      nombre: 'Electores Asistentes',
      valor: Number(resumenTotal?.electoresAsistentes),
      sliceSettings: {
        fill: am5.color('#2971B9'),
      },
    },
    {
      nombre: 'Electores Ausentes',
      valor: Number(resumenTotal?.electoresAusentes),
      sliceSettings: {
        fill: am5.color('#B2B4B6'),
      },
    },
  ];
}

export function cargarGraficaPieParticipacionCiudadanaDos(
  resumenTotal: ResumenTotal
): ChartPie[] {
  return [
    {
      nombre: 'Electores Asistentes',
      valor: Number(resumenTotal?.totalAsistentes),
      sliceSettings: {
        fill: am5.color('#2971B9'),
      },
    },
    {
      nombre: 'Electores Ausentes',
      valor: Number(resumenTotal?.totalAusentes),
      sliceSettings: {
        fill: am5.color('#B2B4B6'),
      },
    },
  ];
}

export function PersonalizacionPaginatorMaterial(): MatPaginatorIntl {
  const customPaginatorIntl = new MatPaginatorIntl();
  customPaginatorIntl.itemsPerPageLabel = 'Registros por página';
  customPaginatorIntl.getRangeLabel = (
    page: number,
    pageSize: number,
    length: number
  ) => {
    if (length === 0 || pageSize === 0) {
      return `0 de ${length}`;
    }
    length = Math.max(length, 0);
    const totalPagina = Math.ceil(length / pageSize);

    return `${page + 1} de ${Number(totalPagina).toLocaleString('en-GB')}`;
  };
  return customPaginatorIntl;
}

export function formatearFecha(fecha: string): string | null {
  if (fecha === '') {
    return '';
  }
  return new DatePipe('en-US').transform(fecha, 'dd/MM/yyyy H:mm:ss');
}

export function descargarArchivoPdf(pdfSrc: any) {
  const currentDateTime =
    new DatePipe('en-US').transform(new Date(), 'MMddyyyyhmmss') ??
    'archivo';

  const src = String(pdfSrc);

  const link = document.createElement('a');
  link.href = src;
  link.download = currentDateTime;
  link.target = '_blank';
  link.click();

  link.remove();
}

export function formatNameElection(string: string){
  if (!string) return '';
  return string.toLowerCase().includes('parlamento andino') ? 'Parlamento Andino' : string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();

}

export const isBrowserChrome = (): boolean => {
  const ua = navigator.userAgent;
  const isChromeDesktop = /Chrome\//i.test(ua) && !/Edg\/|OPR\/|SamsungBrowser|YaBrowser/i.test(ua);
  const isChromeIOS = /CriOS\//i.test(ua);
  const isChrome = isChromeDesktop || isChromeIOS;

  return isChrome;
};
