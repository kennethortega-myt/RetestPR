import { Component } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

import { Usuario } from '../interfaces/usuario-bean';
import { Perfil } from '../interfaces/perfil-bean';

const helper = new JwtHelperService();

@Component({
  selector: 'app-auth-utils',
  template: '',
})
export class AuthComponent {
  public fullError: any;
  public prefixAccesos: string = 'acprad';

  constructor() {}

  public authentication(): Usuario {
    let u = {} as Usuario;
    let token = localStorage.getItem('token');
    if (token != null) {
      u = {} as Usuario;
      let decodedToken = helper.decodeToken(token);
      u.userId = decodedToken['dil'];
      u.codigoCentroComputo = decodedToken['ccc'];
      u.nombreCentroComputo = decodedToken['ncc'];
      u.acronimoProceso = decodedToken['apr'];
      u.nombre = decodedToken['name'];
      let p: Perfil = {} as Perfil;
      p.idPerfil = decodedToken['idp'];
      p.descripcion = decodedToken['perfil'];
      u.perfil = p;
    }
    return u;
  }

  setAccesos(datos : any){
    const encrypted = btoa(JSON.stringify(datos));
    localStorage.setItem(this.prefixAccesos, encrypted);
  }

  getAccesos(){
    const raw = localStorage.getItem(this.prefixAccesos);
    return raw ? JSON.parse(atob(raw)) : null;
  }

  public removerToken() {
    localStorage.clear();
  }
}
