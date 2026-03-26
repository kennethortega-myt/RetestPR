import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { of, switchMap } from "rxjs";

import { setEncryptStorageEleccionValue } from "../helpers/encrypt-storage-eleccion";
import { EleccionesService } from "../services/elecciones-generales/elecciones.service";
import { IProcesoElectoralData } from "../interfaces/proceso-electoral.interface";

export const getTipoDeProcesoElectoralGuard: CanActivateFn = (_, __) => {
  const electionService = inject(EleccionesService);

  return electionService.obtenerProcesoElectoralActivo().pipe(
    switchMap((response) => {
      if (isValidData(response.data)) {
        setEncryptStorageEleccionValue("TIPO_DE_PROCESO_ELECTORAL_A_CARGAR", response.data.tipoProcesoElectoral);
        return of(true);
      }
      return of(false);
    })
  );
};

const isValidData = (data?: IProcesoElectoralData) => {
  return data && data.idEleccionPrincipal && data.tipoProcesoElectoral;
};
