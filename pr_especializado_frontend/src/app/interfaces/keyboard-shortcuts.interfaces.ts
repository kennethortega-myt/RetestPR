// Tipos de teclas válidas para atajos de teclado
export type ModifierKey = 'Control' | 'Alt' | 'Meta' | 'Shift';
export type AlphanumericKey = 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' | '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
export type SymbolKey = '+' | '-' | '=' | '[' | ']' | '\\' | ';' | '\'' | ',' | '.' | '/' | '`';
export type ArrowKey = 'ArrowUp' | 'ArrowDown' | 'ArrowLeft' | 'ArrowRight';
export type FunctionKey = 'F1' | 'F2' | 'F3' | 'F4' | 'F5' | 'F6' | 'F7' | 'F8' | 'F9' | 'F10' | 'F11' | 'F12';
export type SpecialKey = 'Enter' | 'Escape' | 'Space' | 'Tab' | 'Backspace' | 'Delete' | 'Home' | 'End' | 'PageUp' | 'PageDown';

export type ValidKey = ModifierKey | AlphanumericKey | SymbolKey | ArrowKey | FunctionKey | SpecialKey;

export interface KeyboardShortcut {
  id: string;
  description: string;
  keys: ValidKey[]; // Tipado estricto para teclas válidas
  callback: () => void;
  enabled: boolean;
  context?: string; // Contexto donde aplica (opcional)
}

export interface ShortcutConfig {
  globalEnabled: boolean;
  shortcuts: { [key: string]: boolean };
}

// Información sobre shortcuts reservados del navegador/sistema
export interface ReservedShortcut {
  keys: ValidKey[];
  description: string;
  platform: 'all' | 'windows' | 'mac' | 'linux';
  severity: 'error' | 'warning' | 'info';
}
