import { Component } from "@angular/core";
import { Router } from "@angular/router";

@Component({
  selector: "app-error-404",
  standalone: false,
  templateUrl: "../common/error.component.html",
  styleUrl: "../common/error.component.scss",
})
export class Error404Component {
  public titleKey = 'errors.404';
  public imgSrc = "assets/img/errors/404.svg";
  public goBack = 'goBack';

  constructor(private readonly router: Router) {}

  volverAlInicio() {
    this.router.navigate(['/']).then((success) => {
      if(success){
        window.location.href = '/';
      }
    });
  }
}
