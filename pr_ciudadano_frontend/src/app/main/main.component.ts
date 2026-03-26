import { Component, inject, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { getProcesoElectoralUrl } from "../helpers/redirections-helpers/proceso-electoral-redirection.helper";

@Component({
  selector: "app-main",
  template: ` <router-outlet></router-outlet> `,
  standalone: false
})
export class MainComponent implements OnInit {
  private router = inject(Router);

  ngOnInit(): void {
    this.router.navigateByUrl(getProcesoElectoralUrl());
  }
}
