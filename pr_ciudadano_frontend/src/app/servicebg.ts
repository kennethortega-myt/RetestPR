// servicebg.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject,Observable  } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Servicebg {
  private readonly backgroundClassSubject: BehaviorSubject<string>;
  public readonly backgroundClass$: Observable<string>;

  constructor() {
    // Cargar el valor inicial del localStorage
    const savedContaPaso = localStorage.getItem('conta_paso') ?? '1';
    let initialClass = 'background-color-base'; // Valor predeterminado
    
    const contaPaso = parseInt(savedContaPaso, 10);
    const backgrounds = ["background-color-base", "background-color-base-2", "background-color-base-3"];
    initialClass = backgrounds[contaPaso - 1] || initialClass;
    
    this.backgroundClassSubject = new BehaviorSubject<string>(initialClass);
    this.backgroundClass$ = this.backgroundClassSubject.asObservable();
  }

  updateBackgroundClass(className: string) {
    console.log('Actualizando clase en servicio:', className);
    this.backgroundClassSubject.next(className);
  }
}
