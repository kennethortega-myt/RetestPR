import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { StorageService } from './storage.service';
import { jwtDecode } from 'jwt-decode';

export interface IJWTDecodedPayload {
  sub: string;
  usr: string;
  per: string;
  iss: string;
  iat: number;
  exp: number;
  codigoOp: string | number;
  usrname: string;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class JwtDecodeService {
  constructor(private readonly mainStorage: StorageService) { }

  public getJWTDecodedFromLogin(): IJWTDecodedPayload | null {
    const jwt = this.mainStorage.getLocalStorageValue('token');
    if (jwt) {
      return jwtDecode<IJWTDecodedPayload>(jwt);
    } else {
      return null;
    }
  }

  public get isJWTFromLoginValid(): boolean {
    const jwt = this.mainStorage.getLocalStorageValue('token');

    if (jwt) {
      const helper = new JwtHelperService();
      const isTokenExpired = helper.isTokenExpired(jwt);
      return !isTokenExpired;
    }
    return false;
  }
}
