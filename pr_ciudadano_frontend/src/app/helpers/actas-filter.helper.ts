import { FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ID_AMBITO_GEOGRAFICO, TIPO_FILTRO } from './constantes';
import { GenericFilterParams } from '../interfaces/filtro-settings';

/**
 * Centraliza la lógica del métodos compartida entre
 * ActasObservadasComponent y ActasPorAmbitoComponent.
 */

/**
 * Centraliza la lógica del método applyFiltersEvent
 *
 * Actualiza el FormGroup de ubigeo según el nivel de filtro recibido,
 * sincronizando siempre el control `region` para que el ámbito geográfico
 * (nacional=1 / extranjero=2) sea correcto tanto en desktop (paso a paso)
 * como en mobile (el popup emite un único evento con el nivel final).
 *
 * @param myFormUbigeo       FormGroup con controles: region, departamento, provincia, distrito
 * @param params             Parámetros emitidos por GenericFilterUbigeoComponent
 * @param onAmbitoGeografico Callback ejecutado solo cuando tipoFiltro === AMBITO_GEOGRAFICO
 *                           (normalmente llama a this.limpiar() en el componente)
 * @param translateService   Instancia de TranslateService para resolver los mensajes i18n
 * @returns                  El mensaje de guía al usuario correspondiente al nivel seleccionado
 */
export function applyFiltersEvent(
  params: GenericFilterParams,
  myFormUbigeo: FormGroup,
  translateService: TranslateService,
  onAmbitoGeografico: () => void
): string {
  switch (params.tipoFiltro) {
    case TIPO_FILTRO.AMBITO_GEOGRAFICO:
      myFormUbigeo.controls['region'].setValue(params.idAmbitoGeografico);
      myFormUbigeo.controls['departamento'].setValue(0, { emitEvent: false });
      myFormUbigeo.controls['provincia'].setValue(0, { emitEvent: false });
      myFormUbigeo.controls['distrito'].setValue(0, { emitEvent: false });
      onAmbitoGeografico();
      break;
    case TIPO_FILTRO.UBIGEO_NIVEL_01:
      if (params.idAmbitoGeografico) {
        myFormUbigeo.controls['region'].setValue(params.idAmbitoGeografico, { emitEvent: false });
      }
      myFormUbigeo.controls['departamento'].setValue(params.ubigeoNivel1);
      myFormUbigeo.controls['provincia'].setValue(0, { emitEvent: false });
      myFormUbigeo.controls['distrito'].setValue(0, { emitEvent: false });
      break;
    case TIPO_FILTRO.UBIGEO_NIVEL_02:
      if (params.idAmbitoGeografico) {
        myFormUbigeo.controls['region'].setValue(params.idAmbitoGeografico, { emitEvent: false });
      }
      if (params.ubigeoNivel1) {
        myFormUbigeo.controls['departamento'].setValue(params.ubigeoNivel1, { emitEvent: false });
      }
      myFormUbigeo.controls['provincia'].setValue(params.ubigeoNivel2);
      myFormUbigeo.controls['distrito'].setValue(0, { emitEvent: false });
      break;
    case TIPO_FILTRO.UBIGEO_NIVEL_03:
      if (params.idAmbitoGeografico) {
        myFormUbigeo.controls['region'].setValue(params.idAmbitoGeografico, { emitEvent: false });
      }
      if (params.ubigeoNivel1) {
        myFormUbigeo.controls['departamento'].setValue(params.ubigeoNivel1, { emitEvent: false });
      }
      if (params.ubigeoNivel2) {
        myFormUbigeo.controls['provincia'].setValue(params.ubigeoNivel2, { emitEvent: false });
      }
      myFormUbigeo.controls['distrito'].setValue(params.ubigeoNivel3);
      break;
    default:
      break;
  }

  return resolveActasFilterMessage(params, myFormUbigeo, translateService);
}

function resolveActasFilterMessage(
  params: GenericFilterParams,
  myFormUbigeo: FormGroup,
  translateService: TranslateService
): string {
  const t = (key: string) => translateService.instant(`personaje.${key}`);

  const messagesByScope = {
    [ID_AMBITO_GEOGRAFICO.ID_NACIONAL]: {
      [TIPO_FILTRO.AMBITO_GEOGRAFICO]: t('seleccione_region'),
      [TIPO_FILTRO.UBIGEO_NIVEL_01]:   t('seleccione_provincia'),
      [TIPO_FILTRO.UBIGEO_NIVEL_02]:   t('seleccione_distrito'),
    },
    [ID_AMBITO_GEOGRAFICO.ID_EXTRANJERO]: {
      [TIPO_FILTRO.AMBITO_GEOGRAFICO]: t('seleccione_continente'),
      [TIPO_FILTRO.UBIGEO_NIVEL_01]:   t('seleccione_pais'),
      [TIPO_FILTRO.UBIGEO_NIVEL_02]:   t('seleccione_ciudad'),
    }
  };

  const scope = params.idAmbitoGeografico ?? Number(myFormUbigeo.controls['region'].value);
  return messagesByScope[scope]?.[params.tipoFiltro] ?? '';
}
