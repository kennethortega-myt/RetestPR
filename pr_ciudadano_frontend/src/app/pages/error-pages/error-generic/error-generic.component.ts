import { Component } from "@angular/core";
import { Router } from "@angular/router";

@Component({
  selector: "app-error-generic",
  standalone: false,
  templateUrl: "../common/error.component.html",
  styleUrl: "../common/error.component.scss",
})
export class ErrorGenericComponent {
  public titleKey = "Error - Ocurrió un error inesperado";
  public imgSrc = "assets/img/errors/500.svg";
  public goBack = 'goBack';

  constructor(private readonly router: Router) {}
  volverAlInicio() {
    this.router.navigate(["/"]);
  }
}
