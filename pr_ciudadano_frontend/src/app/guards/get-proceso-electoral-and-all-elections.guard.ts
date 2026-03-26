import { inject } from "@angular/core";
import { CanActivateFn } from "@angular/router";
import { map, of, switchMap } from "rxjs";

import { MENU_ELECTIONS_KEY, setEncryptStorageEleccionValue } from "../helpers/encrypt-storage-eleccion";
import { EleccionesService } from "../services/elecciones-generales/elecciones.service";
import { IProcesoElectoralData } from "../interfaces/proceso-electoral.interface";
import { getProcesoElectoralUrl } from "../helpers/redirections-helpers/proceso-electoral-redirection.helper";
import { ListaMenuResponse } from "../helpers/constantes";

export const getProcesoElectoralAndAllElectionsGuard: CanActivateFn = (_, __) => {
  const electionService = inject(EleccionesService);

  return electionService.obtenerProcesoElectoralActivo().pipe(
    switchMap((response) => {
      if (isValidData(response.data)) {
        setEncryptStorageEleccionValue("PROCESO_ELECTORAL_ACTIVO", response.data);
        setEncryptStorageEleccionValue("FECHA_DE_PROCESO", response.data.fechaProceso);
        setEncryptStorageEleccionValue("TIPO_DE_PROCESO_ELECTORAL_A_CARGAR", response.data.tipoProcesoElectoral);
        setEncryptStorageEleccionValue("ID_DE_ELECCION_PRINCIPAL", response.data.idEleccionPrincipal);
        setEncryptStorageEleccionValue("ACTIVO_FECHA_PROCESO", response.data.activoFechaProceso.toString());

        let urlToRedirect = getProcesoElectoralUrl();
        if (urlToRedirect) {
          return electionService.listarEleccionesPorIdProcesoElectoral(response.data.id).pipe(
            map((response2: ListaMenuResponse) => {
              setEncryptStorageEleccionValue(MENU_ELECTIONS_KEY, response2.data);
              return true;
            })
          );
        }
        return of(false);
      }
      return of(false);
    })
  );
};

const isValidData = (data?: IProcesoElectoralData) => {
  return data && data.idEleccionPrincipal && data.tipoProcesoElectoral;
};
