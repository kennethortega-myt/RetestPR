import { inject, Injectable, NgZone } from '@angular/core';
import { AuthService } from './auth-service.service';
import { Router } from '@angular/router';
import { DataLoginStore } from '../states/data-login.store';
import { DialogService } from './dialog.service';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class InactivityTimeoutService {

  private readonly dialogService = inject(DialogService);

  private inactivityTimer: any;
  private readonly INACTIVITY_TIME = 15 * 60 * 1000; // 15 minutos en milisegundos
  private readonly EVENTS_TO_MONITOR = [
    'mousedown', 'mousemove', 'keypress', 'scroll',
    'touchstart', 'click', 'keydown'
  ];

  private readonly boundOnUserActivity: () => void;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly dataLoginStore: DataLoginStore,
    private readonly ngZone: NgZone,
    private readonly translate: TranslateService) {
    this.boundOnUserActivity = this.onUserActivity.bind(this);
  }

  public startMonitoring(): void {
    if (this.authService.isAuthenticated()) {
      this.resetInactivityTimer();
      this.addEventListeners();
    }
  }

  public stopMonitoring(): void {
    this.clearInactivityTimer();
    this.removeEventListeners();
  }

  private resetInactivityTimer(): void {
    this.clearInactivityTimer();
    this.ngZone.runOutsideAngular(() => {
      this.inactivityTimer = setTimeout(() => {
        this.ngZone.run(() => {
          this.handleInactivityTimeout();
        });
      }, this.INACTIVITY_TIME);
    });
  }

  private clearInactivityTimer(): void {
    if (this.inactivityTimer) {
      clearTimeout(this.inactivityTimer);
      this.inactivityTimer = null;
    }
  }

  private addEventListeners(): void {
    for (const event of this.EVENTS_TO_MONITOR) {
      document.addEventListener(event, this.boundOnUserActivity, true);
    }
  }

  private removeEventListeners(): void {
    for (const event of this.EVENTS_TO_MONITOR) {
      document.removeEventListener(event, this.boundOnUserActivity, true);
    }
  }

  private onUserActivity(): void {
    if (this.authService.isAuthenticated()) {
      this.resetInactivityTimer();
    }
  }

  private handleInactivityTimeout(): void {
    this.dataLoginStore.clearDataLogin();
    this.router.navigate(['/']);
    this.stopMonitoring();
    this.dialogService.mostrarMensajeAdvertencia(this.translate.instant('SesionExpirada'),
      this.translate.instant('TituloExpirado'));
  }
}