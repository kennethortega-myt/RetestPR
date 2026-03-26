import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-error-404',
  imports: [],
  templateUrl: './error-404.component.html',
  styleUrl: './error-404.component.css'
})
export class Error404Component {
  public title = "Error - Página no encontrada";
  public imgSrc = "assets/img/errors/404.svg";
  constructor(private readonly router: Router) {}
  volverAlInicio() {
    this.router.navigate(["/"]);
  }
}
