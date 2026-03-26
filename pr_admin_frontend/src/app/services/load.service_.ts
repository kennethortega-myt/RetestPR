import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoadService {
  private mostrarLoadingObs$ = new BehaviorSubject<boolean>(false);
  private activeRequests = 0; // 👈 contador de requests activos

  getAccionLoading(): Observable<boolean> {
    return this.mostrarLoadingObs$.asObservable();
  }

  show(): void {
    this.activeRequests++;
    if (this.activeRequests === 1) {
      this.mostrarLoadingObs$.next(true);
      sessionStorage.setItem('loading', 'true');
    }
  }

  hide(): void {
    if (this.activeRequests > 0) {
      this.activeRequests--;
    }
    if (this.activeRequests === 0) {
      this.mostrarLoadingObs$.next(false);
      sessionStorage.setItem('loading', 'false');
    }
  }
}
