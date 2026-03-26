import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-participacion-ciudadana',
  templateUrl: './participacion-ciudadana.component.html',
  standalone: false
})
export class ParticipacionCiudadanaComponent implements OnDestroy {
  public isResponsive = false;
  private breakpointSubscription: Subscription;

  constructor(private readonly breakpointObserver: BreakpointObserver) {
    this.breakpointSubscription = this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
      .subscribe((result) => {
        this.isResponsive = result.matches;
      });
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
  }
}
