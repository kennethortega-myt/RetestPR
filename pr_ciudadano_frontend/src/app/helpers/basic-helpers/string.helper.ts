import { TranslateService } from "@ngx-translate/core";
import { AppInjector } from "../app-injector";


export function capitalizeFirstLetter(str: string) {
  if (!str) return str;
  str = str.toLowerCase();
  return str.charAt(0).toUpperCase() + str.slice(1);
}

export function formatPascalCaseInText(str: string) {
  const strArr = str?.split(" ");
  const formateddArr = strArr?.map((elem) => {
    return capitalizeFirstLetter(elem);
  });
  return formateddArr?.join(" ");
}

/**
 * Traduce una clave de i18n utilizando una instancia de TranslateService.
 * Esta función utiliza un inyector estático para obtener el TranslateService,
 * permitiendo su uso en contextos no inyectables.
 * @param key La clave de traducción (ej. 'header.info-process').
 * @returns El texto traducido.
 */
export function getTranslatedString(key: string): string {
  if (!key) return '';
  const injector = AppInjector.getInjector();
  const translateService = injector.get(TranslateService);
  return translateService.instant(key);
}
