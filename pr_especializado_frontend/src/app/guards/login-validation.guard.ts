import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { JwtDecodeService } from '../services/jwt-decode.service';
import { ResumenGeneralApiService } from '../services/resumen-general-api.service';

export const loginValidationGuard: CanActivateFn = async () => {
  const router = inject(Router);
  const jwtDecodeService = inject(JwtDecodeService);
  const resumenGeneralApiService = inject(ResumenGeneralApiService);
  const redirectToLogin = () => {
    localStorage.clear();
    router.navigateByUrl('/');
  };

  if (!jwtDecodeService.isJWTFromLoginValid) {
    redirectToLogin();
    return false;
  }

  const idEleccion = await resumenGeneralApiService.getIdEleccionPrincipal();
  if (!idEleccion) {
    return false;
  }

  return true;
};

export const redirectIfAuthenticatedGuard: CanActivateFn = async () => {
  const router = inject(Router);
  const jwtDecodeService = inject(JwtDecodeService);

  if (jwtDecodeService.isJWTFromLoginValid) {
    await router.navigateByUrl('/home/inicio');
    return false;
  }

  return true;
};
