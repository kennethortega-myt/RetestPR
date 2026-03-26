import { Injectable, Inject, OnDestroy, signal } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import {
  KeyboardShortcut,
  ShortcutConfig,
  ValidKey,
  ModifierKey
} from '../../../interfaces/keyboard-shortcuts.interfaces';
import { KEY_STORE_ACCESSIBILITY } from '../accessibility.constant';
import { encryptStorageEleccion } from '../../../settings/encrypt-storage.settings';

@Injectable({
  providedIn: 'root'
})
export class KeyboardShortcutsService implements OnDestroy {
  private readonly shortcuts: Map<string, KeyboardShortcut> = new Map();
  private readonly configSubject = new BehaviorSubject<ShortcutConfig>(this.loadConfig());
  private readonly destroy$ = new Subject<void>();

  // Usar signals para el estado (Angular feature)
  public globalEnabled = signal<boolean>(true);
  public config$: Observable<ShortcutConfig> = this.configSubject.asObservable();

  constructor(@Inject(DOCUMENT) private readonly document: Document) {
    // Inicializar signal con el valor cargado
    const config = this.loadConfig();
    this.globalEnabled.set(config.globalEnabled);
    // Nota: El listener global ahora se maneja via @HostListener en GlobalKeyboardShortcutsDirective
  }

  /**
   * Registra un atajo de teclado
   * WCAG 2.1.4: Solo acepta combinaciones con modificadores (Ctrl, Alt, Meta)
   */
  registerShortcut(shortcut: KeyboardShortcut): void {
    // Validar que use modificadores (WCAG 2.1.4)
    if (!this.hasModifierKey(shortcut.keys)) {
      console.warn(`[WCAG 2.1.4] El atajo "${shortcut.id}" debe incluir una tecla modificadora (Ctrl, Alt, Meta).`);
      return;
    }

    const config = this.configSubject.value;
    shortcut.enabled = config.shortcuts[shortcut.id] ?? true;
    this.shortcuts.set(shortcut.id, shortcut);
  }

  /**
   * Desregistra un atajo
   */
  unregisterShortcut(id: string): void {
    this.shortcuts.delete(id);
  }

  /**
   * Habilita o deshabilita un atajo específico
   */
  toggleShortcut(id: string, enabled: boolean): void {
    const shortcut = this.shortcuts.get(id);
    if (shortcut) {
      shortcut.enabled = enabled;
      this.updateConfig({ [id]: enabled });
    }
  }

  /**
   * Habilita o deshabilita todos los atajos globalmente usando signals
   */
  toggleGlobalShortcuts(enabled: boolean): void {
    this.globalEnabled.set(enabled);
    const config = this.configSubject.value;
    config.globalEnabled = enabled;
    this.configSubject.next(config);
    this.saveConfig(config);
  }

  /**
   * Obtiene todos los atajos registrados
   */
  getAllShortcuts(): KeyboardShortcut[] {
    return Array.from(this.shortcuts.values());
  }

  /**
   * Verifica si un atajo tiene teclas modificadoras
   */
  private hasModifierKey(keys: ValidKey[]): boolean {
    const modifiers = new Set<ModifierKey>(['Control', 'Alt', 'Meta', 'Shift']);
    return keys.some((key) => modifiers.has(key as ModifierKey));
  }

  /**
   * Maneja eventos de teclado globales (llamado desde GlobalKeyboardShortcutsDirective)
   * Usa @HostListener a través de la directiva para mejor integración con Angular
   */
  public handleGlobalKeyPress(event: KeyboardEvent): void {
    // Verificar si los atajos globales están habilitados
    if (!this.globalEnabled()) {
      return;
    }

    // Filtrar elementos de entrada para evitar interferencias
    const target = event.target as HTMLElement | null;
    if (this.isInputElement(target)) {
      return;
    }

    // Procesar el evento de teclado
    this.handleKeyPress(event);
  }

  /**
   * Maneja eventos de keyup globales si es necesario
   * @param event - Evento de keyup
   */
  public handleGlobalKeyUp(event: KeyboardEvent): void {
    // Implementar lógica de keyup si es necesaria en el futuro
    // Por ahora, solo para casos especiales
  }

  /**
   * Destruye el servicio y limpia las suscripciones (Angular lifecycle)
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Maneja la pulsación de teclas
   */
  private handleKeyPress(event: KeyboardEvent): void {
    for (const shortcut of this.shortcuts.values()) {
      if (!shortcut.enabled) continue;

      if (this.matchesShortcut(event, shortcut.keys)) {
        event.preventDefault();
        shortcut.callback();
        break;
      }
    }
  }

  /**
   * Verifica si el evento coincide con las teclas del atajo
   * Soporta múltiples modificadores para compatibilidad Mac/PC
   */
  private matchesShortcut(event: KeyboardEvent, keys: ValidKey[]): boolean {
    const pressedKeys: ValidKey[] = [];

    if (event.ctrlKey) pressedKeys.push('Control');
    if (event.altKey) pressedKeys.push('Alt');
    if (event.metaKey) pressedKeys.push('Meta');
    if (event.shiftKey) pressedKeys.push('Shift');

    pressedKeys.push(event.key as ValidKey);

    // Verificar coincidencia exacta
    if (keys.length === pressedKeys.length && keys.every((key) => pressedKeys.includes(key))) {
      return true;
    }

    // Verificar equivalencias Mac/PC para compatibilidad cruzada
    return this.matchesCrossPlatform(event, keys);
  }

  /**
   * Verifica equivalencias entre Mac (Meta) y PC (Alt) para el mismo atajo
   */
  private matchesCrossPlatform(event: KeyboardEvent, keys: ValidKey[]): boolean {
    const pressedKeys = this.getPressedKeys(event);

    // Si el atajo usa Meta (Mac), también permitir Alt (PC)
    if (keys.includes('Meta')) {
      return this.checkPlatformCompatibility(keys, pressedKeys, 'Meta', 'Alt');
    }

    // Si el atajo usa Alt (PC), también permitir Meta (Mac)
    if (keys.includes('Alt')) {
      return this.checkPlatformCompatibility(keys, pressedKeys, 'Alt', 'Meta');
    }

    return false;
  }

  private checkPlatformCompatibility(
    keys: ValidKey[], 
    pressedKeys: ValidKey[], 
    originalKey: ValidKey, 
    alternativeKey: ValidKey
  ): boolean {
    const compatibleKeys = keys.map((key) => 
      key === originalKey ? (alternativeKey as ValidKey) : key
    );
    return compatibleKeys.length === pressedKeys.length && 
           compatibleKeys.every((key) => pressedKeys.includes(key));
  }

  /**
   * Obtiene el array de teclas presionadas del evento
   */
  private getPressedKeys(event: KeyboardEvent): ValidKey[] {
    const pressedKeys: ValidKey[] = [];

    if (event.ctrlKey) pressedKeys.push('Control');
    if (event.altKey) pressedKeys.push('Alt');
    if (event.metaKey) pressedKeys.push('Meta');
    if (event.shiftKey) pressedKeys.push('Shift');
    pressedKeys.push(event.key as ValidKey);

    return pressedKeys;
  }

  /**
   * Verifica si el elemento es un campo de entrada
   */
  private isInputElement(element: HTMLElement | null): boolean {
    return element?.tagName.toLowerCase() === 'input' || 
           element?.tagName.toLowerCase() === 'textarea' || 
           element?.tagName.toLowerCase() === 'select' || 
           element?.isContentEditable || false;
  }

  /**
   * Carga la configuración desde localStorage encriptado
   */
  private loadConfig(): ShortcutConfig {
    const stored = encryptStorageEleccion.getItem<string>(KEY_STORE_ACCESSIBILITY.keyboardShortcuts);
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch (e) {
        console.error('Error al cargar configuración de atajos:', e);
      }
    }
    return { globalEnabled: true, shortcuts: {} };
  }

  /**
   * Guarda la configuración en localStorage encriptado
   */
  private saveConfig(config: ShortcutConfig): void {
    encryptStorageEleccion.setItem(KEY_STORE_ACCESSIBILITY.keyboardShortcuts, JSON.stringify(config));
  }

  /**
   * Actualiza la configuración de un atajo específico
   */
  private updateConfig(shortcuts: { [key: string]: boolean }): void {
    const config = this.configSubject.value;
    config.shortcuts = { ...config.shortcuts, ...shortcuts };
    this.configSubject.next(config);
    this.saveConfig(config);
  }
}
