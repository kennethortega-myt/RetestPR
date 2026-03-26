import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Usuario } from '../../../interfaces/usuario-bean';
import { AuthComponent } from '../../../helpers/auth-component';
import { NgFor, NgIf } from '@angular/common';

@Component({
  selector: 'app-principal',
  templateUrl: './principal.component.html',
  imports: [NgIf, NgFor, RouterLink],
})
export class PrincipalComponent extends AuthComponent implements OnInit {
  public usuario?: Usuario;
  modulos: any[] = [];

  constructor(public router: Router) {
    super();
  }
  
  ngOnInit(): void {
    this.usuario = this.authentication();
    const accesos = this.getAccesos();
    this.modulos = accesos.modulos.filter((el:any) => el.nombre.toLowerCase() != 'principal');
  }

  public nacionAutorizado() {
    return this.usuario!.perfil.descripcion == 'ADM_NAC';
  }

  public ccAutorizado() {
    return this.usuario!.perfil.descripcion == 'ADM_CC';
  }
  
}
