import { CanActivateFn, Router } from "@angular/router";
import { inject } from "@angular/core";

import { isRevocatoria } from "../helpers/storage-helpers/encrypt-storage.helper";
import { PATHS } from "../settings/app.routes.settings";

export const isRevocatoriaGuard: CanActivateFn = (_, __) => {
  const router = inject(Router);

  if (isRevocatoria()) {
    return true;
  }

  router.navigate([PATHS.error_inesperado]);
  return false;
};
