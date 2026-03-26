// src/app/states/data-login.store.ts
import { Injectable, computed, signal } from '@angular/core';
import { DataLogin } from '../interfaces/login';

@Injectable({ providedIn: 'root' })
export class DataLoginStore {
  private readonly dataLoginSignal = signal<DataLogin | null>(loadDataLoginFromStorage());

  readonly dataLogin = computed(() => this.dataLoginSignal());
  readonly usuario = computed(() => this.dataLoginSignal()?.usuario ?? null);
  readonly perfiles = computed(() => this.dataLoginSignal()?.perfiles ?? []);
  readonly modulos = computed(() => this.dataLoginSignal()?.modulos ?? []);
  readonly isProcessValid = computed(() => this.dataLoginSignal()?.isProcessValid ?? true);
  readonly isLoggedIn = computed(() => !!this.dataLoginSignal()?.usuario.token);

  setDataLogin(data: DataLogin): void {
    const encrypted = btoa(JSON.stringify(data));
    localStorage.setItem('dl', encrypted);
    this.dataLoginSignal.set(data);
  }

  updateProp<K extends keyof DataLogin>(key: K, value: DataLogin[K]): void {
    const current = this.dataLoginSignal();
    if (!current) return;

    const updated: DataLogin = {
      ...current,
      [key]: value,
    };

    const encrypted = btoa(JSON.stringify(updated));
    localStorage.setItem('dl', encrypted);
    this.dataLoginSignal.set(updated);
  }

  clearDataLogin(): void {
    localStorage.removeItem('dl');
    this.dataLoginSignal.set(null);
    localStorage.clear();
    sessionStorage.clear();
  }
}

function loadDataLoginFromStorage(): DataLogin | null {
  const raw = localStorage.getItem('dl');
  return raw ? JSON.parse(atob(raw)) : null;
}
