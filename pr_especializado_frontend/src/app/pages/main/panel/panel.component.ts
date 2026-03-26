import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { Module, Usuario } from '../../../interfaces/login';
import { DataLoginStore } from '../../../states/data-login.store';
import { PotiticGroupsService } from '../../../services/potitic-groups.service';
import { loadPoliticGroupDescriptionHelper } from '../../../helpers/app.helper';

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule],
  templateUrl: './panel.component.html',
})
export class PanelComponent implements OnInit {

  readonly potiticGroupsService = inject(PotiticGroupsService);
  readonly dataLoginStore = inject(DataLoginStore);

  usuarioLogin: Usuario = this.dataLoginStore.usuario() ?? {} as Usuario;
  labelUserLogin = '';

  modulos: Module[] = [];

  ngOnInit(): void {
    this.modulos = this.dataLoginStore.modulos().filter(el => el.nombre.toLowerCase() != 'inicio');
    loadPoliticGroupDescriptionHelper(
      this.usuarioLogin,
      this.potiticGroupsService,
      (label) => {
        this.labelUserLogin = label;
      },
      'PanelComponent'
    );
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      (event.target as HTMLElement).click();
    }
  }

  getTranslationKey(moduleName: string): string {
    const translationMap: { [key: string]: string } = {
      'Inicio': 'menu.inicio',
      'Consultas avanzadas': 'menu.consultasAvanzadas',
      'Actas enviadas y devueltas del JEE': 'menu.actasEnviadasJEE',
      'Actas enviadas y recibidas del JEE': 'menu.actasEnviadasJEE',
      'Mis reportes': 'menu.misReportes',
      'Reportes automáticos': 'menu.reportesAutomaticos',
      'Configuración de reportes automáticos': 'menu.configuracionReportes',
      'Lista de configuración de reportes': 'menu.listaConfiguracion',
      'Descarga de actas': 'menu.descargaActas',
      'Configuración de descarga de actas': 'menu.configuracionDescargaActas',
      'Lista de configuración de descarga de actas': 'menu.listaConfiguracionActas'
    };

    return translationMap[moduleName] || moduleName;
  }
}
