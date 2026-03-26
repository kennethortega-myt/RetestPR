import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { URL_PATHS_TO_REDIRECT } from '../../../../settings/app.routes.settings';
import { ELECCION } from '../../../../constants/constantes';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { AccesibilidadComponent } from '../../../../components/accesibilidad/accesibilidad.component';

@Component({
  selector: 'app-sitemap',
  standalone: false,
  templateUrl: './sitemap.component.html',
  styleUrl: './sitemap.component.scss'
})
export class SitemapComponent {

  private readonly router = inject(Router);  
  private readonly _bottomSheet = inject(MatBottomSheet)

  SELECCION = ELECCION;
  RESUMEN = URL_PATHS_TO_REDIRECT.resumen;
  PRESIDENCIAL = URL_PATHS_TO_REDIRECT.presidenciales
  SENADOR_MULTIPLE = URL_PATHS_TO_REDIRECT.distrito_electoral_multiple
  SENADOR_UNICO = URL_PATHS_TO_REDIRECT.distrito_electoral_unico
  DIPUTADOS = URL_PATHS_TO_REDIRECT.diputados
  PARLAMENTO = URL_PATHS_TO_REDIRECT.parlamento_andino
  PARTICIPACION = URL_PATHS_TO_REDIRECT.participacion_ciudadana
  ACTAS = URL_PATHS_TO_REDIRECT.actas
  REPORTE = URL_PATHS_TO_REDIRECT.reportes_automaticos
  FRECUENTE = URL_PATHS_TO_REDIRECT.faq
  SITEMAP = URL_PATHS_TO_REDIRECT.sitemap

  goToEleccion(tipoEleccion: string, index?: number): void {
    if (index !== undefined) {
      this.router.navigateByUrl(tipoEleccion, {
        state: { sectionIndex: index }
      });
    } else {
      this.router.navigateByUrl(tipoEleccion);
    }
  }
  
  openBottomSheet(): void {
    this._bottomSheet.open(AccesibilidadComponent, {
      panelClass: "barra-accesibilidad",
    });
  }
}
