import { Injector } from '@angular/core';

/**
 * Helper para permitir el acceso a servicios inyectables de Angular
 * desde contextos no inyectables (como funciones helper estáticas).
 *
 * ¡PRECAUCIÓN! Este es un patrón que debe usarse con moderación, ya que
 * puede ocultar dependencias y hacer que el código sea más difícil de probar.
 */
export class AppInjector {
  private static _injector: Injector;

  static setInjector(injector: Injector) {
    AppInjector._injector = injector;
  }

  static getInjector(): Injector {
    return AppInjector._injector;
  }
}