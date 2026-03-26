import { TranslateService } from "@ngx-translate/core";

export type PersonOnpeMessageType =
  | "there_is_no_information"
  | "there_is_no_information2"
  | "press_button_to_cotinue"
  | "criterios_no_valid"
  | "select_orga_and_press_button"
  | "select_one_politic_org";

export const getPersonOnpeMessages = (
  translateService: TranslateService
): { [key in PersonOnpeMessageType]: string } => {
  return {
    there_is_no_information: translateService.instant("personaje.there_is_no_information"),
    there_is_no_information2: translateService.instant("personaje.there_is_no_information2"),
    press_button_to_cotinue: translateService.instant("personaje.press_button_to_cotinue"),
    criterios_no_valid: translateService.instant("personaje.criterios_no_valid"),
    select_orga_and_press_button: translateService.instant("personaje.select_orga_and_press_button"),
    select_one_politic_org: translateService.instant("personaje.select_one_politic_org"),
  };
};
