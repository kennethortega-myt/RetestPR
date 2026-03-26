import { Component } from "@angular/core";
import { Router } from "@angular/router";

@Component({
  selector: "app-error-500",
  standalone: false,
  templateUrl: "../common/error.component.html",
  styleUrl: "../common/error.component.scss",
})
export class Error500Component {
  public titleKey = 'errors.500';
  public imgSrc = "assets/img/errors/500.svg";
  public goBack = 'goBack';

  constructor(private readonly router: Router) {}
  volverAlInicio() {
    this.router.navigate(["/"]);
  }
}
