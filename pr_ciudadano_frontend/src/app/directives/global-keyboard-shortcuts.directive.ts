import { Directive, HostListener, OnDestroy, OnInit } from '@angular/core';
import { KeyboardShortcutsService } from '../services/accesibilidad/keyboard-shortcuts.service';

/**
 * Directiva global para manejar atajos de teclado usando @HostListener
 * Esta directiva debe aplicarse al elemento raíz de la aplicación
 */
@Directive({
  selector: '[appGlobalKeyboardShortcuts]',
  standalone: false
})
export class GlobalKeyboardShortcutsDirective implements OnInit, OnDestroy {

  constructor(private keyboardShortcuts: KeyboardShortcutsService) {}

  ngOnInit(): void {
    // La directiva está lista para escuchar eventos
    console.debug('[Global Keyboard Shortcuts] Directiva inicializada');
  }

  /**
   * Escucha eventos de teclado globalmente usando @HostListener
   * @param event - Evento de teclado
   */
  @HostListener('document:keydown', ['$event'])
  onDocumentKeydown(event: KeyboardEvent): void {
    // Delegar al servicio para el manejo de atajos
    this.keyboardShortcuts.handleGlobalKeyPress(event);
  }

  /**
   * Escucha eventos de keyup para casos especiales si es necesario
   * @param event - Evento de teclado
   */
  @HostListener('document:keyup', ['$event'])
  onDocumentKeyup(event: KeyboardEvent): void {
    // Manejar casos especiales de keyup si es necesario
    this.keyboardShortcuts.handleGlobalKeyUp(event);
  }

  ngOnDestroy(): void {
    console.debug('[Global Keyboard Shortcuts] Directiva destruida');
  }
}