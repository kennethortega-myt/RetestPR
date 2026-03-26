import { Injectable } from "@angular/core";
import { Constantes } from "../helpers/constantes";

@Injectable({
  providedIn: 'root'
})
export class IdSessionService {
  
  generarIdSesion(): string {
    let idSesion = sessionStorage.getItem(Constantes.SESSION_KEY);
    
    if (!idSesion) {
      idSesion = crypto.randomUUID();
      sessionStorage.setItem(Constantes.SESSION_KEY, idSesion);
    }
    
    return idSesion;
  }
}