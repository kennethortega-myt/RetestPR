import { Injectable } from "@angular/core";
import { SESSION_KEY } from "../helpers/constantes";

@Injectable({
  providedIn: 'root'
})
export class IdSessionService {
  
  generarIdSesion(): string {
    let idSesion = sessionStorage.getItem(SESSION_KEY);
    if (!idSesion) {
      idSesion = crypto.randomUUID();
      sessionStorage.setItem(SESSION_KEY, idSesion);
    }
    
    return idSesion;
  }
}