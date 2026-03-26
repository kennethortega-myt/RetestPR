import { Injectable } from '@angular/core';
import { AuthService } from './auth-service.service';
import { Router } from '@angular/router';
import { DataLoginStore } from '../states/data-login.store';

@Injectable({
  providedIn: 'root'
})
export class TabSessionService {

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly dataLoginStore: DataLoginStore
  ) {}

  public initializeTabSession(): void {
    if (this.authService.isAuthenticated()) {
      const windowTabId = window.name;
      const storedTabId = sessionStorage.getItem('tab_session_id');
      if (!windowTabId || !storedTabId) {
        // Nueva pestaña - no hay window.name establecido
        this.closeSession();
      } else if (windowTabId === storedTabId) {
        // Misma pestaña - refresh
        this.refreshTabSession();
      } else {
        // IDs no coinciden - posible nueva pestaña
        this.closeSession();
      }
    }
  }

  public createTabSession(): void {
    // Crear ID único usando timestamp + crypto random
    const randomArray = new Uint32Array(2);
    crypto.getRandomValues(randomArray);
    const randomId = Array.from(randomArray, value => value.toString(36)).join('');
    const tabId = `tab_${Date.now()}_${randomId}`;
    const timestamp = Date.now().toString();

    // Guardar en window.name (persiste en refresh, se pierde en nueva pestaña)
    window.name = tabId;

    // Guardar también en sessionStorage como respaldo
    sessionStorage.setItem('tab_session_id', tabId);
    localStorage.setItem('login_timestamp', timestamp);

  }

  private refreshTabSession(): void {
    // Actualizar timestamp
    localStorage.setItem('login_timestamp', Date.now().toString());
  }

  private closeSession(): void {
    // Limpiar sesión y redirigir
    window.name = '';
    sessionStorage.clear();
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('login_timestamp');

    this.router.navigate(['/']);
    this.dataLoginStore.clearDataLogin();
  }
}
