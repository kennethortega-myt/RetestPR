import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { JwtDecodeService } from '../services/jwt-decode.service';

export const withAuthGuard: CanActivateFn = (_, __) => {
  const router = inject(Router);
  const jwtDecodeService = inject(JwtDecodeService);

  if (jwtDecodeService.isJWTFromLoginValid) {
    router.navigateByUrl('/main'); // Redirige a main si ya está logeado
    return false;
  }

  return true; // Permite el acceso si NO está logueado
};
