import { Component, OnInit } from '@angular/core';
import { AuthComponent } from '../../helpers/auth-component';
import { Usuario } from '../../interfaces/usuario-bean';
import { Router } from '@angular/router';
import { UsuarioService } from '../../services/usuario.service';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  standalone: false,
})
export class HeaderComponent extends AuthComponent implements OnInit {
  public usuario?: Usuario;
  onKeyDown: any;


  constructor(
    public dialog: MatDialog,
    public router: Router,
    private readonly usuarioService: UsuarioService){
    super();
  }
  
  ngOnInit(): void {
      this.usuario = this.authentication();
  }

  public cerrarSession(): void {
    this.usuarioService.cerrarSesion();
  }
}
