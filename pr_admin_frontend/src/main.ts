import { bootstrapApplication } from '@angular/platform-browser';
import { enableProdMode } from '@angular/core';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { environment } from './environments/environment';

if (environment.production) {
  enableProdMode();
}

(async () => {
  try {
    const response = await fetch(`${environment.apiUrl}/recurso/apigoogle`);
    const data = await response.json();

    (window as any)['__sitekey'] = data.api;
    (window as any)['__key'] = data.key;

  } catch (error) {
    console.error('Error during app initialization:', error);
  }
  await bootstrapApplication(AppComponent, {
    ...appConfig
  });
})();    
