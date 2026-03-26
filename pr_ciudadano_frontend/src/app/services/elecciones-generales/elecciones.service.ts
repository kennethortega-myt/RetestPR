import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { EleccionesApiService } from "./elecciones-api.service";
import { IProcesoElectoralResponse } from "../../interfaces/proceso-electoral.interface";

@Injectable({
  providedIn: "root",
})
export class EleccionesService {
  constructor(private readonly eleccionesApiService: EleccionesApiService) {}

  obtenerEleccionAll(): Observable<any> {
    return this.eleccionesApiService.obtenerEleccionAll();
  }
  listarEleccionesPorIdProcesoElectoral(idProcesoElectoral: number): Observable<any> {
    return this.eleccionesApiService.listarEleccionarPorIdProcesoElectoral(idProcesoElectoral);
  }
  obtenerProcesoElectoralActivo(): Observable<IProcesoElectoralResponse> {
    return this.eleccionesApiService.obtenerProcesoElectoralActivo();
  }
}
