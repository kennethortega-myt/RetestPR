import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

export interface LoaderState {
  show: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class LoaderService {
  private readonly loaderSubject = new Subject<LoaderState>();
  private _show = false;
  loaderState: Observable<LoaderState> = this.loaderSubject.asObservable();
  automatic = true;

  show(): void {
    if (this.automatic && !this._show) {
      this._show = true;
      this.loaderSubject.next({ show: this._show });
    }
  }

  hide(): void {
    if (this.automatic && this._show) {
      this._show = false;
      this.loaderSubject.next({ show: this._show });
    }
  }
}
