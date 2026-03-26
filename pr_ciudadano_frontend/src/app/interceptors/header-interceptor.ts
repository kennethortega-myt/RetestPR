import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize, Subscription, timer } from 'rxjs';
import { LoaderService } from '../services/elecciones-generales/loading.service';

let totalRequests = 0;
let hideTimer: Subscription | null = null;

export const headerInterceptor: HttpInterceptorFn = (req, next) => {
  const loaderService = inject(LoaderService);

  const clonedRequest = req.clone({
    setHeaders: {
      Accept: '*/*',
      'Content-Type': 'application/json'
    }
  });

  totalRequests++;
  loaderService.show();
  if (hideTimer) {
    hideTimer.unsubscribe();
    hideTimer = null;
  }

  return next(clonedRequest).pipe(
    finalize(() => {
      totalRequests--;
      if (totalRequests === 0) {
        hideTimer = timer(250).subscribe(() => {
          loaderService.hide();
          hideTimer = null;
        });
      }
    })
  );
};
