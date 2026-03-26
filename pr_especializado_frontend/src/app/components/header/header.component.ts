import { Component, Input, OnInit, inject } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { Router, RouterModule } from '@angular/router';
import { Usuario } from '../../interfaces/login';
import { DrawerService } from '../../services/drawer.service';
import { PotiticGroupsService } from '../../services/potitic-groups.service';
import { ROUTE_PATHS } from '../../settings/app-routing.settings';
import { DataLoginStore } from '../../states/data-login.store';
import { AccessibilityService } from '../accessibility/services';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { loadPoliticGroupDescriptionHelper } from '../../helpers/app.helper';
import { UsuarioApiService } from '../../services/usuario-api.service';

interface Ipsum {
  value: string;
  viewValue: string;
  timeValue: string;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  imports: [TranslateModule, CommonModule, MatIcon, MatMenuModule, MatSelectModule, FormsModule, RouterModule]
})
export class HeaderComponent implements OnInit {
  @Input() isBoletines = false;

  readonly routePaths = ROUTE_PATHS;
  readonly drawerService = inject(DrawerService);
  readonly router = inject(Router);
  readonly potiticGroupsService = inject(PotiticGroupsService);
  readonly bottomSheet = inject(MatBottomSheet);
  readonly dataLoginStore = inject(DataLoginStore);
  readonly accessibilityService = inject(AccessibilityService);
  readonly usuarioService = inject(UsuarioApiService);

  usuarioLogin: Usuario = this.dataLoginStore.usuario() ?? ({} as Usuario);
  labelUserLogin = '';

  ipsums: Ipsum[] = [
    { value: 'valor-0', viewValue: 'Loren Ipsum 1', timeValue: '12:50:30' },
    { value: 'valor-1', viewValue: 'Loren Ipsum 2', timeValue: '12:50:30' },
    { value: 'valor-2', viewValue: 'Loren Ipsum 3', timeValue: '12:50:30' }
  ];
  selectedIpsum: Ipsum = this.ipsums[1];

  ngOnInit(): void {
    loadPoliticGroupDescriptionHelper(
      this.usuarioLogin,
      this.potiticGroupsService,
      (label) => {
        this.labelUserLogin = label;
      },
      'HeaderComponent'
    );
  }

  openDrawer(): void {
    this.drawerService.toggleDrawer();
  }

  cerrarSession(): void {
    this.usuarioService.cerrarSesion();
  }

  openBottomSheet(): void {
    this.accessibilityService.show();
  }
}
