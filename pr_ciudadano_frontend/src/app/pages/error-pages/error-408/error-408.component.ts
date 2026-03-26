import { Component } from "@angular/core";
import { Router } from "@angular/router";

@Component({
  selector: "app-error-408",
  standalone: false,
  templateUrl: "../common/error.component.html",
  styleUrl: "../common/error.component.scss",
})
export class Error408Component {
  public titleKey = 'errors.408';
  public imgSrc = "assets/img/errors/408.svg";
  public goBack = 'goBack';

  constructor(private readonly router: Router) {}
  volverAlInicio() {
    this.router.navigate(["/"]);
  }
}
