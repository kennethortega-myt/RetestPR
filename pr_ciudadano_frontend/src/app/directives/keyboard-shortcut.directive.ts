import { Directive, ElementRef, EventEmitter, HostListener, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ValidKey, SpecialKey, ReservedShortcut } from '../interfaces/keyboard-shortcuts.interfaces';

/**
 * Directiva para atajos de teclado locales (WCAG 2.1.4 compliant)
 * Solo se activa cuando el elemento tiene el foco
 * 
 * Características de accesibilidad:
 * - Agrega automáticamente tabindex="0" si el elemento no es focusable (configurable)
 * - Detecta elementos naturalmente focusables (button, input, a[href], etc.)
 * - Respeta elementos deshabilitados y enlaces sin href
 * 
 * Control de activación:
 * - Habilitar/deshabilitar dinámicamente con [enabled]="boolean"
 * - Métodos programáticos: enable(), disable(), toggle()
 * - Eventos de monitoreo: (shortcutAttempted) para intentos de uso
 * - Razones de deshabilitación para debugging
 * 
 * Validación contra shortcuts reservados:
 * - Detecta automáticamente conflictos con shortcuts del navegador/sistema
 * - Diferencia entre Windows, Mac y Linux
 * - Niveles de severidad: error (crítico), warning (advertencia), info (menor)
 * - Use [allowReservedShortcuts]="true" para permitir conflictos (no recomendado)
 * 
 * Manejo especial de teclas:
 * - Teclas especiales (Enter, Escape, Space, Tab) requieren modificadores por defecto
 * - Use allowSpecialKeysWithoutModifiers=true para permitir teclas especiales sin modificadores
 * - Tab, Enter y Escape tienen comportamientos especiales para preservar navegación
 * 
 * Uso básico:
 * <div appKeyboardShortcut 
 *      [shortcutKeys]="['Control', 's']" 
 *      (shortcutTriggered)="onSave()">
 * </div>
 * 
 * Uso con control de activación:
 * <div appKeyboardShortcut 
 *      [shortcutKeys]="['Control', 's']" 
 *      [enabled]="canSave"
 *      [disabledReason]="'Formulario inválido'"
 *      (shortcutTriggered)="onSave()"
 *      (shortcutAttempted)="onShortcutAttempt($event)">
 * </div>
 * 
 * Uso con referencia de template:
 * <div appKeyboardShortcut 
 *      #shortcutRef="appKeyboardShortcut"
 *      [shortcutKeys]="['Escape']" 
 *      (shortcutTriggered)="onClose()">
 * </div>
 * <button (click)="shortcutRef.toggle('Modo edición')">Toggle Shortcut</button>
 * 
 * Uso con shortcuts potencialmente reservados:
 * <div appKeyboardShortcut 
 *      [shortcutKeys]="['Control', 's']" 
 *      [allowReservedShortcuts]="true"
 *      (shortcutTriggered)="onSave()">
 *   <!-- ADVERTENCIA: Ctrl+S puede interferir con "Guardar página" del navegador -->
 * </div>
 * 
 * Monitoreo de conflictos:
 * <div>Conflictos: {{ shortcutRef.getStatus().reservedConflicts | json }}</div>
 */
@Directive({
  selector: '[appKeyboardShortcut]',
  standalone: false,
  exportAs: 'appKeyboardShortcut'
})
export class KeyboardShortcutDirective implements OnInit, OnDestroy {
  @Input() shortcutKeys: ValidKey[] = [];
  @Input() allowSpecialKeysWithoutModifiers: boolean = false; // Permitir teclas especiales sin modificadores
  @Input() autoAddTabindex: boolean = true; // Agregar automáticamente tabindex si falta
  @Input() enabled: boolean = true; // Controlar si la directiva está activa
  @Input() disabledReason?: string; // Razón por la cual está deshabilitada (para debugging)
  @Input() allowReservedShortcuts: boolean = false; // Permitir shortcuts reservados del navegador (peligroso)
  @Output() shortcutTriggered = new EventEmitter<void>();
  @Output() shortcutAttempted = new EventEmitter<{keys: ValidKey[], enabled: boolean, reason?: string}>(); // Evento cuando se intenta usar un atajo deshabilitado

  // Lista de shortcuts reservados del navegador/sistema
  private readonly reservedShortcuts: ReservedShortcut[] = [
    // Navegación y pestañas
    { keys: ['Control', 't'], description: 'Nueva pestaña', platform: 'all', severity: 'error' },
    { keys: ['Control', 'w'], description: 'Cerrar pestaña', platform: 'all', severity: 'error' },
    { keys: ['Control', 'n'], description: 'Nueva ventana', platform: 'all', severity: 'error' },
    { keys: ['Control', 'r'], description: 'Recargar página', platform: 'all', severity: 'error' },
    { keys: ['Control', 'l'], description: 'Enfocar barra de direcciones', platform: 'all', severity: 'error' },
    { keys: ['Control', 'd'], description: 'Agregar marcador', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'h'], description: 'Historial', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'j'], description: 'Descargas', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'k'], description: 'Búsqueda', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'u'], description: 'Ver código fuente', platform: 'all', severity: 'warning' },
    
    // Edición
    { keys: ['Control', 'a'], description: 'Seleccionar todo', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'c'], description: 'Copiar', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'v'], description: 'Pegar', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'x'], description: 'Cortar', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'z'], description: 'Deshacer', platform: 'all', severity: 'warning' },
    { keys: ['Control', 'y'], description: 'Rehacer', platform: 'all', severity: 'warning' },
    
    // Zoom y vista
    { keys: ['Control', '+'], description: 'Zoom in', platform: 'all', severity: 'info' },
    { keys: ['Control', '-'], description: 'Zoom out', platform: 'all', severity: 'info' },
    { keys: ['Control', '0'], description: 'Zoom reset', platform: 'all', severity: 'info' },
    { keys: ['F11'], description: 'Pantalla completa', platform: 'all', severity: 'warning' },
    { keys: ['F12'], description: 'Herramientas de desarrollador', platform: 'all', severity: 'info' },
    
    // Mac específicos
    { keys: ['Meta', 't'], description: 'Nueva pestaña (Mac)', platform: 'mac', severity: 'error' },
    { keys: ['Meta', 'w'], description: 'Cerrar pestaña (Mac)', platform: 'mac', severity: 'error' },
    { keys: ['Meta', 'n'], description: 'Nueva ventana (Mac)', platform: 'mac', severity: 'error' },
    { keys: ['Meta', 'r'], description: 'Recargar página (Mac)', platform: 'mac', severity: 'error' },
    { keys: ['Meta', 'l'], description: 'Enfocar barra de direcciones (Mac)', platform: 'mac', severity: 'error' },
    { keys: ['Meta', 'a'], description: 'Seleccionar todo (Mac)', platform: 'mac', severity: 'warning' },
    { keys: ['Meta', 'c'], description: 'Copiar (Mac)', platform: 'mac', severity: 'warning' },
    { keys: ['Meta', 'v'], description: 'Pegar (Mac)', platform: 'mac', severity: 'warning' },
    { keys: ['Meta', 'x'], description: 'Cortar (Mac)', platform: 'mac', severity: 'warning' },
    { keys: ['Meta', 'z'], description: 'Deshacer (Mac)', platform: 'mac', severity: 'warning' },
  ];

  constructor(private elementRef: ElementRef) {}

  ngOnInit(): void {
    this.ensureElementIsFocusable();
    this.validateAgainstReservedShortcuts();
  }

  /**
   * Asegura que el elemento sea focusable para accesibilidad (WCAG 2.1.4)
   */
  private ensureElementIsFocusable(): void {
    const element = this.elementRef.nativeElement;
    
    // Verificar si el elemento ya es naturalmente focusable
    if (this.isNaturallyFocusable(element)) {
      console.debug('[Keyboard Shortcut] Elemento naturalmente focusable:', element.tagName);
      return;
    }

    // Verificar si ya tiene tabindex
    if (element.hasAttribute('tabindex')) {
      console.debug('[Keyboard Shortcut] Elemento ya tiene tabindex:', element.getAttribute('tabindex'));
      return;
    }

    // Agregar tabindex automáticamente si está habilitado
    if (this.autoAddTabindex) {
      element.setAttribute('tabindex', '0');
      console.info(
        '[WCAG 2.1.4] Se agregó automáticamente tabindex="0" al elemento para hacerlo accesible.',
        element
      );
    } else {
      console.warn(
        '[WCAG 2.1.4] El elemento con appKeyboardShortcut debe tener tabindex para ser accesible. ' +
        'Use [autoAddTabindex]="true" para agregarlo automáticamente.',
        element
      );
    }
  }

  /**
   * Verifica si un elemento es naturalmente focusable
   */
  private isNaturallyFocusable(element: HTMLElement): boolean {
    const focusableTags = ['INPUT', 'BUTTON', 'SELECT', 'TEXTAREA', 'A'];
    const tagName = element.tagName.toUpperCase();
    
    // Elementos naturalmente focusables
    if (focusableTags.includes(tagName)) {
      // Verificar que no estén deshabilitados
      const isDisabled = element.hasAttribute('disabled');
      
      // Para enlaces, verificar que tengan href
      if (tagName === 'A') {
        return element.hasAttribute('href') && !isDisabled;
      }
      
      return !isDisabled;
    }

    // Elementos con contenteditable
    if (element.isContentEditable) {
      return true;
    }

    return false;
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(event: KeyboardEvent): void {
    // Solo activar si el elemento tiene el foco
    if (document.activeElement !== this.elementRef.nativeElement) {
      return;
    }

    // Manejo especial para teclas que pueden interferir con la navegación
    if (this.shouldPreventSpecialKeyDefault(event)) {
      return;
    }

    if (this.matchesShortcut(event)) {
      // Validar si el atajo puede ejecutarse
      const validation = this.canExecuteShortcut();
      
      if (!validation.canExecute) {
        console.debug(
          '[Keyboard Shortcut] Atajo no puede ejecutarse:', 
          this.shortcutKeys, 
          `Razón: ${validation.reason}`
        );
        
        // Emitir evento de intento fallido
        this.shortcutAttempted.emit({
          keys: this.shortcutKeys,
          enabled: false,
          reason: validation.reason
        });
        
        return;
      }

      // Manejo especial para teclas específicas
      this.handleSpecialKeyBehavior(event);
      event.preventDefault();
      event.stopPropagation();
      
      // Emitir evento de uso exitoso
      this.shortcutAttempted.emit({
        keys: this.shortcutKeys,
        enabled: true
      });
      
      this.shortcutTriggered.emit();
    }
  }

  private matchesShortcut(event: KeyboardEvent): boolean {
    const pressedKeys: ValidKey[] = [];
    
    if (event.ctrlKey) pressedKeys.push('Control');
    if (event.altKey) pressedKeys.push('Alt');
    if (event.metaKey) pressedKeys.push('Meta');
    if (event.shiftKey) pressedKeys.push('Shift');
    
    // Validar que la tecla presionada sea válida antes de agregarla
    const key = event.key as ValidKey;
    if (this.isValidKey(key)) {
      // Validación especial para teclas especiales sin modificadores
      if (this.isSpecialKey(key) && !this.hasModifiers(event) && !this.allowSpecialKeysWithoutModifiers) {
        console.warn(
          `[WCAG 2.1.4] La tecla especial "${key}" requiere modificadores o allowSpecialKeysWithoutModifiers=true`
        );
        return false;
      }
      pressedKeys.push(key);
    }

    return this.shortcutKeys.length === pressedKeys.length &&
           this.shortcutKeys.every(key => pressedKeys.includes(key));
  }

  private isValidKey(key: string): key is ValidKey {
    const validKeys = [
      // Modificadores
      'Control', 'Alt', 'Meta', 'Shift',
      // Alfanuméricos
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      // Símbolos
      '+', '-', '=', '[', ']', '\\', ';', '\'', ',', '.', '/', '`',
      // Flechas
      'ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight',
      // Funciones
      'F1', 'F2', 'F3', 'F4', 'F5', 'F6', 'F7', 'F8', 'F9', 'F10', 'F11', 'F12',
      // Especiales
      'Enter', 'Escape', 'Space', 'Tab', 'Backspace', 'Delete', 'Home', 'End', 'PageUp', 'PageDown'
    ];
    return validKeys.includes(key);
  }

  /**
   * Verifica si una tecla es especial (requiere manejo cuidadoso)
   */
  private isSpecialKey(key: ValidKey): key is SpecialKey {
    const specialKeys: SpecialKey[] = [
      'Enter', 'Escape', 'Space', 'Tab', 'Backspace', 'Delete', 
      'Home', 'End', 'PageUp', 'PageDown'
    ];
    return specialKeys.includes(key as SpecialKey);
  }

  /**
   * Verifica si el evento tiene modificadores
   */
  private hasModifiers(event: KeyboardEvent): boolean {
    return event.ctrlKey || event.altKey || event.metaKey || event.shiftKey;
  }

  /**
   * Determina si se debe prevenir el comportamiento por defecto de teclas especiales
   */
  private shouldPreventSpecialKeyDefault(event: KeyboardEvent): boolean {
    const key = event.key as ValidKey;
    
    // No interferir con navegación básica si no hay modificadores
    if (!this.hasModifiers(event)) {
      switch (key) {
        case 'Tab':
          // Permitir navegación por Tab a menos que sea parte del atajo
          return !this.shortcutKeys.includes('Tab');
        case 'Enter':
          // Permitir Enter en elementos interactivos
          const element = this.elementRef.nativeElement;
          if (element.tagName === 'BUTTON' || element.tagName === 'A') {
            return !this.shortcutKeys.includes('Enter');
          }
          break;
        case 'Escape':
          // Escape generalmente debe funcionar para cerrar modales/menús
          return false;
      }
    }
    
    return false;
  }

  /**
   * Manejo especial para comportamientos específicos de teclas
   */
  private handleSpecialKeyBehavior(event: KeyboardEvent): void {
    const key = event.key as ValidKey;
    
    switch (key) {
      case 'Space':
        // Space en elementos focusables puede activar clicks
        if (this.elementRef.nativeElement.tagName === 'BUTTON') {
          console.debug('[Keyboard Shortcut] Space en botón - comportamiento personalizado');
        }
        break;
      case 'Enter':
        // Enter puede tener comportamiento especial en formularios
        console.debug('[Keyboard Shortcut] Enter detectado - comportamiento personalizado');
        break;
      case 'Escape':
        // Escape puede cerrar elementos
        console.debug('[Keyboard Shortcut] Escape detectado - comportamiento personalizado');
        break;
    }
  }

  /**
   * Habilita la directiva programáticamente
   */
  enable(): void {
    this.enabled = true;
    this.disabledReason = undefined;
    console.debug('[Keyboard Shortcut] Directiva habilitada para:', this.shortcutKeys);
  }

  /**
   * Deshabilita la directiva programáticamente
   */
  disable(reason?: string): void {
    this.enabled = false;
    this.disabledReason = reason;
    console.debug('[Keyboard Shortcut] Directiva deshabilitada para:', this.shortcutKeys, reason ? `Razón: ${reason}` : '');
  }

  /**
   * Alterna el estado de habilitación
   */
  toggle(reason?: string): boolean {
    if (this.enabled) {
      this.disable(reason);
    } else {
      this.enable();
    }
    return this.enabled;
  }

  /**
   * Verifica si la directiva está habilitada
   */
  isEnabled(): boolean {
    return this.enabled;
  }

  /**
   * Obtiene la razón de deshabilitación si existe
   */
  getDisabledReason(): string | undefined {
    return this.disabledReason;
  }

  /**
   * Actualiza las teclas del atajo dinámicamente
   */
  updateShortcutKeys(newKeys: ValidKey[]): void {
    const oldKeys = [...this.shortcutKeys];
    this.shortcutKeys = newKeys;
    console.debug('[Keyboard Shortcut] Teclas actualizadas de:', oldKeys, 'a:', newKeys);
    
    // Validar nuevas teclas contra shortcuts reservados
    this.validateAgainstReservedShortcuts();
  }

  /**
   * Valida si las teclas configuradas colisionan con shortcuts reservados del navegador
   */
  private validateAgainstReservedShortcuts(): void {
    if (!this.shortcutKeys || this.shortcutKeys.length === 0) {
      return;
    }

    const currentPlatform = this.detectPlatform();
    const conflicts = this.findReservedShortcutConflicts(currentPlatform);

    conflicts.forEach(conflict => {
      const keysStr = conflict.keys.join('+');
      const message = `[Shortcut Conflict] "${keysStr}" está reservado para: ${conflict.description}`;

      switch (conflict.severity) {
        case 'error':
          if (!this.allowReservedShortcuts) {
            console.error(message + ' - Este atajo puede no funcionar correctamente.');
          }
          break;
        case 'warning':
          console.warn(message + ' - Considere usar una combinación diferente.');
          break;
        case 'info':
          console.info(message + ' - Posible conflicto menor.');
          break;
      }
    });

    // Si hay conflictos críticos y no están permitidos, deshabilitar
    const criticalConflicts = conflicts.filter(c => c.severity === 'error');
    if (criticalConflicts.length > 0 && !this.allowReservedShortcuts) {
      this.disable(`Conflicto con shortcuts reservados: ${criticalConflicts.map(c => c.keys.join('+')).join(', ')}`);
    }
  }

  /**
   * Detecta la plataforma actual
   */
  private detectPlatform(): 'windows' | 'mac' | 'linux' {
    const userAgent = navigator.userAgent.toLowerCase();
    if (userAgent.includes('mac')) return 'mac';
    if (userAgent.includes('linux')) return 'linux';
    return 'windows';
  }

  /**
   * Encuentra conflictos con shortcuts reservados
   */
  private findReservedShortcutConflicts(platform: 'windows' | 'mac' | 'linux'): ReservedShortcut[] {
    return this.reservedShortcuts.filter(reserved => {
      // Verificar si aplica a la plataforma actual
      if (reserved.platform !== 'all' && reserved.platform !== platform) {
        return false;
      }

      // Verificar si las teclas coinciden exactamente
      return this.arraysEqual(this.shortcutKeys, reserved.keys);
    });
  }

  /**
   * Compara dos arrays de teclas para igualdad
   */
  private arraysEqual(a: ValidKey[], b: ValidKey[]): boolean {
    if (a.length !== b.length) return false;
    
    const sortedA = [...a].sort();
    const sortedB = [...b].sort();
    
    return sortedA.every((val, index) => val === sortedB[index]);
  }

  /**
   * Obtiene información sobre conflictos con shortcuts reservados
   */
  getReservedShortcutConflicts(): ReservedShortcut[] {
    const platform = this.detectPlatform();
    return this.findReservedShortcutConflicts(platform);
  }

  /**
   * Verifica si el atajo actual tiene conflictos críticos
   */
  hasCriticalConflicts(): boolean {
    const conflicts = this.getReservedShortcutConflicts();
    return conflicts.some(c => c.severity === 'error');
  }

  /**
   * Valida si el atajo puede ejecutarse en el contexto actual
   */
  private canExecuteShortcut(): { canExecute: boolean; reason?: string } {
    if (!this.enabled) {
      return { 
        canExecute: false, 
        reason: this.disabledReason || 'Directiva deshabilitada' 
      };
    }

    // Validar que las teclas estén configuradas
    if (!this.shortcutKeys || this.shortcutKeys.length === 0) {
      return { 
        canExecute: false, 
        reason: 'No hay teclas configuradas' 
      };
    }

    // Validar que el elemento sea focusable
    const element = this.elementRef.nativeElement;
    if (!this.isNaturallyFocusable(element) && !element.hasAttribute('tabindex')) {
      return { 
        canExecute: false, 
        reason: 'Elemento no es focusable' 
      };
    }

    // Validar conflictos con shortcuts reservados
    if (!this.allowReservedShortcuts && this.hasCriticalConflicts()) {
      const conflicts = this.getReservedShortcutConflicts()
        .filter(c => c.severity === 'error')
        .map(c => c.keys.join('+'))
        .join(', ');
      return {
        canExecute: false,
        reason: `Conflicto crítico con shortcuts reservados: ${conflicts}`
      };
    }

    return { canExecute: true };
  }

  /**
   * Obtiene información de estado de la directiva
   */
  getStatus(): {
    enabled: boolean;
    canExecute: boolean;
    shortcutKeys: ValidKey[];
    disabledReason?: string;
    validationReason?: string;
    reservedConflicts: ReservedShortcut[];
    hasCriticalConflicts: boolean;
    platform: string;
  } {
    const validation = this.canExecuteShortcut();
    const conflicts = this.getReservedShortcutConflicts();
    
    return {
      enabled: this.enabled,
      canExecute: validation.canExecute,
      shortcutKeys: [...this.shortcutKeys],
      disabledReason: this.disabledReason,
      validationReason: validation.reason,
      reservedConflicts: conflicts,
      hasCriticalConflicts: this.hasCriticalConflicts(),
      platform: this.detectPlatform()
    };
  }

  ngOnDestroy(): void {
    // Cleanup si es necesario
  }
}
