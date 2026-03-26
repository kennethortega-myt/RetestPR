import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { JwtDecodeService } from '../services/jwt-decode.service';

export const checkAuthGuard: CanActivateFn = (_, __) => {
  const router = inject(Router);
  const jwtDecodeService = inject(JwtDecodeService);
  if (jwtDecodeService.isJWTFromLoginValid) {
    return true;
  }
  router.navigateByUrl('/inicio');
  return false;
};
