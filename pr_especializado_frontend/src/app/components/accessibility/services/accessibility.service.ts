import { inject, Injectable, signal, WritableSignal } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { encryptStorageEleccion } from '../../../settings/encrypt-storage.settings';
import {
  CONTAINER_ACCESSIBILITY_CLASS_NAME,
  ITEM_CONTRAST_STEP_1,
  ITEM_CURSOR_STEP_1,
  ITEM_DISLEXIA_FRIENDLY_STEP_1,
  ITEM_INTERLINEADO_STEP_1,
  ITEM_READING_MASK_STEP_1,
  ITEM_TEXT_SIZE_STEP_1,
  KEY_STORE_ACCESSIBILITY,
  PROFILE_DEFAULT_EMPTY
} from '../accessibility.constant';
import { AccessibilityStep, Profile } from '../accessibility.interface';
import { AccessibilityComponent } from '../accessibility.component';

@Injectable({ providedIn: 'root' })
export class AccessibilityService {
  bottomSheet: MatBottomSheet = inject(MatBottomSheet);

  private readonly _mode: WritableSignal<number> = signal<number>(this.loadInitialMode());
  textSizeActive: WritableSignal<AccessibilityStep> = signal<AccessibilityStep>(ITEM_TEXT_SIZE_STEP_1);
  contrastActive: WritableSignal<AccessibilityStep> = signal<AccessibilityStep>(ITEM_CONTRAST_STEP_1);
  readingMaskActive: WritableSignal<AccessibilityStep> = signal<AccessibilityStep>(ITEM_READING_MASK_STEP_1);
  dislexiaFriendlyActive: WritableSignal<AccessibilityStep> = signal<AccessibilityStep>(ITEM_DISLEXIA_FRIENDLY_STEP_1);
  cursorActive: WritableSignal<AccessibilityStep> = signal<AccessibilityStep>(ITEM_CURSOR_STEP_1);
  interlineadoActive: WritableSignal<AccessibilityStep> = signal<AccessibilityStep>(ITEM_INTERLINEADO_STEP_1);
  profileActive: WritableSignal<Profile> = signal<Profile>(PROFILE_DEFAULT_EMPTY);
  mode: WritableSignal<number> = this._mode;
  isMobile: WritableSignal<boolean> = signal<boolean>(false);

  show(): void {
    this.bottomSheet.open(AccessibilityComponent, {
      panelClass: CONTAINER_ACCESSIBILITY_CLASS_NAME
    });
  }

  toggleMode(): number {
    const next = (this._mode() % 3) + 1;
    this._mode.set(next);
    localStorage.setItem('conta_paso_cursor', next.toString());
    return next;
  }

  textSizeActiveUpdate(value: AccessibilityStep): void {
    this.textSizeActive.set(value);
    encryptStorageEleccion.setItem(KEY_STORE_ACCESSIBILITY.textSizeActive, value);
  }
  contrastActiveUpdate(value: AccessibilityStep): void {
    this.contrastActive.set(value);
    encryptStorageEleccion.setItem(KEY_STORE_ACCESSIBILITY.contrastActive, value);
  }

  readingMaskActiveUpdate(value: AccessibilityStep): void {
    this.readingMaskActive.set(value);
    encryptStorageEleccion.setItem(KEY_STORE_ACCESSIBILITY.readingMaskActive, value);
  }

  dislexiaFriendlyActiveUpdate(value: AccessibilityStep): void {
    this.dislexiaFriendlyActive.set(value);
    encryptStorageEleccion.setItem(KEY_STORE_ACCESSIBILITY.dislexiaFriendlyActive, value);
  }

  cursorActiveUpdate(value: AccessibilityStep): void {
    this.cursorActive.set(value);
    encryptStorageEleccion.setItem(KEY_STORE_ACCESSIBILITY.cursorActive, value);
  }

  interlineadoActiveUpdate(value: AccessibilityStep): void {
    this.interlineadoActive.set(value);
    encryptStorageEleccion.setItem(KEY_STORE_ACCESSIBILITY.interlineActive, value);
  }

  profileActiveUpdate(value: Profile): void {
    this.profileActive.set(value);
    encryptStorageEleccion.setItem(KEY_STORE_ACCESSIBILITY.profileActive, value);
  }

  loadDataFromLocalStorage(): void {
    const _textSizeActive = encryptStorageEleccion.getItem<string>(KEY_STORE_ACCESSIBILITY.textSizeActive);
    if (_textSizeActive) {
      this.textSizeActive.set(JSON.parse(_textSizeActive));
    }

    const _contrastActive = encryptStorageEleccion.getItem<string>(KEY_STORE_ACCESSIBILITY.contrastActive);
    if (_contrastActive) {
      this.contrastActive.set(JSON.parse(_contrastActive));
    }

    const _readingMaskActive = encryptStorageEleccion.getItem<string>(KEY_STORE_ACCESSIBILITY.readingMaskActive);
    if (_readingMaskActive) {
      this.readingMaskActive.set(JSON.parse(_readingMaskActive));
    }

    const _dislexiaFriendlyActive = encryptStorageEleccion.getItem<string>(KEY_STORE_ACCESSIBILITY.dislexiaFriendlyActive);
    if (_dislexiaFriendlyActive) {
      this.dislexiaFriendlyActive.set(JSON.parse(_dislexiaFriendlyActive));
    }

    const _cursorActive = encryptStorageEleccion.getItem<string>(KEY_STORE_ACCESSIBILITY.cursorActive);
    if (_cursorActive) {
      this.cursorActive.set(JSON.parse(_cursorActive));
    }

    const _interlineadoActive = encryptStorageEleccion.getItem<string>(KEY_STORE_ACCESSIBILITY.interlineActive);
    if (_interlineadoActive) {
      this.interlineadoActive.set(JSON.parse(_interlineadoActive));
    }

    const _profileActive = encryptStorageEleccion.getItem<string>(KEY_STORE_ACCESSIBILITY.profileActive);
    if (_profileActive) {
      this.profileActive.set(JSON.parse(_profileActive));
    }
  }

  resetAll(): void {
    encryptStorageEleccion.removeItem(KEY_STORE_ACCESSIBILITY.textSizeActive);
    encryptStorageEleccion.removeItem(KEY_STORE_ACCESSIBILITY.contrastActive);
    encryptStorageEleccion.removeItem(KEY_STORE_ACCESSIBILITY.readingMaskActive);
    encryptStorageEleccion.removeItem(KEY_STORE_ACCESSIBILITY.dislexiaFriendlyActive);
    encryptStorageEleccion.removeItem(KEY_STORE_ACCESSIBILITY.cursorActive);
    encryptStorageEleccion.removeItem(KEY_STORE_ACCESSIBILITY.interlineActive);
    encryptStorageEleccion.removeItem(KEY_STORE_ACCESSIBILITY.profileActive);
    this.textSizeActive.set(ITEM_TEXT_SIZE_STEP_1);
    this.contrastActive.set(ITEM_CONTRAST_STEP_1);
    this.readingMaskActive.set(ITEM_READING_MASK_STEP_1);
    this.dislexiaFriendlyActive.set(ITEM_DISLEXIA_FRIENDLY_STEP_1);
    this.cursorActive.set(ITEM_CURSOR_STEP_1);
    this.interlineadoActive.set(ITEM_INTERLINEADO_STEP_1);
    this.profileActive.set(PROFILE_DEFAULT_EMPTY);
  }

  private loadInitialMode(): number {
    const stored = Number(localStorage.getItem('conta_paso_cursor'));
    return Number.isNaN(stored) ? 1 : stored;
  }
}
