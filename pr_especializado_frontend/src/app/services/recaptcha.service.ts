// recaptcha.service.ts
import { Injectable } from '@angular/core';

declare const grecaptcha: any;

interface WindowWithRecaptcha extends Window {
  __recaptcha_sitekey?: string;
}

@Injectable({
  providedIn: 'root'
})
export class RecaptchaService {
  execute(action: string): Promise<string> {
    return new Promise<string>((resolve, reject) => {
      const siteKey: string | undefined = (globalThis as unknown as WindowWithRecaptcha).__recaptcha_sitekey;

      if (!siteKey) {
        reject(new Error('No reCAPTCHA site key found.'));
        return;
      }

      if (grecaptcha === undefined) {
        reject(new Error('reCAPTCHA not loaded.'));
        return;
      }

      grecaptcha.ready(() => {
        grecaptcha.execute(siteKey, { action })
          .then((token: string) => resolve(token))
          .catch((err: Error) => reject(err));
      });
    });
  }
}
