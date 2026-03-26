import { bootstrapApplication } from '@angular/platform-browser';
import { enableProdMode } from '@angular/core';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { environment } from './environments/environment';

interface WindowWithRecaptcha extends Window {
  __recaptcha_sitekey?: string;
  __recaptcha_key?: string;
}

declare const window: WindowWithRecaptcha;

if (environment.production) {
  enableProdMode();
}

(async () => {
  async function loadRecaptchaScript(siteKey: string, retries = 2): Promise<void> {
    if (document.querySelector('script[src*="https://www.google.com/recaptcha/api.js"]')) {
      return;
    }

    for (let i = 0; i <= retries; i++) {
      try {
        await new Promise<void>((resolve, reject) => {
          const script = document.createElement('script');
          script.src = `https://www.google.com/recaptcha/api.js?render=${siteKey}`;
          script.async = true;
          script.defer = true;
          script.onload = () => resolve();
          script.onerror = () => reject(new Error('Failed to load reCAPTCHA script'));
          document.head.appendChild(script);
        });
        return;
      } catch {
        if (i === retries) throw new Error('Error loading reCAPTCHA script after retries');
        await new Promise(res => setTimeout(res, 500));
      }
    }
  }

  try {
    const response = await fetch(`${environment.apiUrl}/recurso/apigoogle`);
    const data = await response.json();

    window.__recaptcha_sitekey = data.api;
    window.__recaptcha_key = data.key;

    await loadRecaptchaScript(data.api);

    await bootstrapApplication(AppComponent, {
      ...appConfig
    });
  } catch (error) {
    console.error('Error during app initialization:', error);
  }
})();
