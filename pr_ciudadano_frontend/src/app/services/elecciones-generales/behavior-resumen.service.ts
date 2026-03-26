import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class BehaviorResumenService {
  private readonly actualizarDatosResumenObs$: BehaviorSubject<number> = new BehaviorSubject(0);
  constructor() {}

  getActualizarResumen(): Observable<number> {
    return this.actualizarDatosResumenObs$.asObservable();
  }
  
  setActualizarResumen(value: number) {
    this.actualizarDatosResumenObs$.next(value);
  }
}
