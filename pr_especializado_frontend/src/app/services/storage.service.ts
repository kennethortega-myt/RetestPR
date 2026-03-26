import { Injectable } from '@angular/core';

export type LocalStorageKey = 'token';
export type SessionStorageKey = '';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  constructor() {}

  // LOCAL STORAGE

  public setLocalStorageValue(key: LocalStorageKey, value: string) {
    localStorage.setItem(key, value);
  }

  public getLocalStorageValue(key: LocalStorageKey): string | null {
    return localStorage.getItem(key);
  }

  public cleanLocalStorage() {
    localStorage.clear();
  }

  // SESSION STORAGE

  public setSessionStorageValue(key: SessionStorageKey, value: string) {
    sessionStorage.setItem(key, value);
  }

  public getSessionStorageValue(key: SessionStorageKey): string | null {
    return sessionStorage.getItem(key);
  }

  public cleanSessionStorage() {
    sessionStorage.clear();
  }
}
