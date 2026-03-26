import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Observable, finalize } from 'rxjs';
import { LoadingService } from '../components/loading/loading.service';

@Injectable()
export class HttpLoadingInterceptorService implements HttpInterceptor {
  constructor(private readonly loadingService: LoadingService) {}

  // 🔹 URLs que no deben activar el loading
  private readonly ignoredUrls: string[] = [
    '/assets/i18n/',              // Traducciones
    '/assets/mapas/amcharts5/',   // Mapas
    '/reportes/generar',          // Generando reporte
  ];

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // Verifica si la URL actual debe ser ignorada
    const isIgnored = this.ignoredUrls.some((url) => req.url?.toLowerCase().includes(url.toLowerCase()));

    // Si está en la lista de excepciones, solo pasa la petición sin mostrar loading
    if (isIgnored) {
      return next.handle(req);
    }
    this.loadingService.show();

    return next.handle(req).pipe(
      finalize(() => {
        setTimeout(() => this.loadingService.hide(), 250)
      })
    );
  }
}
