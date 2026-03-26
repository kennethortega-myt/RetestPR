import { CanActivateFn, Router } from "@angular/router";
import { inject } from "@angular/core";

import { isEleccionesGenerales } from "../helpers/storage-helpers/encrypt-storage.helper";
import { PATHS } from "../settings/app.routes.settings";

export const isEleccionesGeneralesGuard: CanActivateFn = (_, __) => {
  const router = inject(Router);

  if (isEleccionesGenerales()) {
    return true;
  }

  router.navigate([PATHS.error_inesperado]);
  return false;
};
