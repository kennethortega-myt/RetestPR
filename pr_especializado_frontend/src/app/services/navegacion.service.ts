import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class BloqueadorNavegacionService {
  constructor() {}

  bloquearRetroceso() {
    history.pushState(null, '', location.href);
    globalThis.onpopstate = () => {
      history.pushState(null, '', location.href);
    };
  }

  permitirRetroceso() {
    globalThis.onpopstate = null;
  }
}
