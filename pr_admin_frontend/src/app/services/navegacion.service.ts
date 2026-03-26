import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class BloqueadorNavegacionService {
  constructor() {}

  bloquearRetroceso() {
    history.pushState(null, '', location.href);

    window.onpopstate = () => {
      history.pushState(null, '', location.href);
    };
  }

  permitirRetroceso() {
    window.onpopstate = null;
  }
}
