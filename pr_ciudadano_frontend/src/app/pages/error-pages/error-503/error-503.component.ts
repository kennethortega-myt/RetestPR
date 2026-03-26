import { Component } from "@angular/core";
import { Router } from "@angular/router";

@Component({
  selector: "app-error-503",
  standalone: false,
  templateUrl: "../common/error.component.html",
  styleUrl: "../common/error.component.scss",
})
export class Error503Component {
  public titleKey = 'errors.503';
  public imgSrc = "assets/img/errors/503.svg";
  public goBack = 'goBack';

  constructor(private readonly router: Router) {}
  volverAlInicio() {
    this.router.navigate(["/"]);
  }
}
