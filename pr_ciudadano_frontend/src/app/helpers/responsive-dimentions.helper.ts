import { Breakpoints } from "@angular/cdk/layout";
import { NUMBER_OF_BAR_IN_GRAFIC, NUMBER_OF_BAR_IN_GRAFIC_GROUP, SIZE_PAGINATION } from "../settings/responsive.settings";

export const GENERAL_BREAKPOINTS = [Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium, Breakpoints.Large];
export const BAR_GRAFIC_BREAKPOINTS = [Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium, Breakpoints.Large];

export function getNumberOfBarsForGrafic(breakpoints: { [key: string]: boolean }): number {
  if (breakpoints[Breakpoints.XSmall]) {
    return NUMBER_OF_BAR_IN_GRAFIC.CELL;
  }
  if (breakpoints[Breakpoints.Small]) {
    return NUMBER_OF_BAR_IN_GRAFIC.TABLET;
  }
  if (breakpoints[Breakpoints.Medium]) {
    return NUMBER_OF_BAR_IN_GRAFIC.LAPTOP;
  }
  if (breakpoints[Breakpoints.Large]) {
    return NUMBER_OF_BAR_IN_GRAFIC.DESKTOP;
  }
  return NUMBER_OF_BAR_IN_GRAFIC.DESKTOP;
}

export function getNumberOfBarsForGraficGroup(breakpoints: { [key: string]: boolean }): number {
  if (breakpoints[Breakpoints.XSmall]) {
    return NUMBER_OF_BAR_IN_GRAFIC_GROUP.CELL;
  }
  if (breakpoints[Breakpoints.Small]) {
    return NUMBER_OF_BAR_IN_GRAFIC_GROUP.TABLET;
  }
  if (breakpoints[Breakpoints.Medium]) {
    return NUMBER_OF_BAR_IN_GRAFIC.LAPTOP;
  }
  if (breakpoints[Breakpoints.Large]) {
    return NUMBER_OF_BAR_IN_GRAFIC_GROUP.LAPTOP;
  }
  return NUMBER_OF_BAR_IN_GRAFIC_GROUP.DESKTOP;
}

export function getSizePagination(breakpoints: { [key: string]: boolean }): number {
  if (breakpoints[Breakpoints.XSmall]) {
    return SIZE_PAGINATION.CELL;
  }
  if (breakpoints[Breakpoints.Small]) {
    return SIZE_PAGINATION.TABLET;
  }
  if (breakpoints[Breakpoints.Medium]) {
    return NUMBER_OF_BAR_IN_GRAFIC.LAPTOP;
  }
  if (breakpoints[Breakpoints.Large]) {
    return SIZE_PAGINATION.DESKTOP;
  }
  
  return SIZE_PAGINATION.DESKTOP;
}
