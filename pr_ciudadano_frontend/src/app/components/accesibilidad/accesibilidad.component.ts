import { Component, effect, signal, WritableSignal } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatSelectionListChange } from '@angular/material/list';
import { TranslateService } from '@ngx-translate/core';
import { CursorService } from '../../services/accesibilidad/cursor.service';
import {
  ITEM_CONTRAST,
  ITEM_CONTRAST_STEP_1,
  ITEM_CONTRAST_STEP_2,
  ITEM_CURSOR,
  ITEM_CURSOR_STEP_1,
  ITEM_CURSOR_STEP_2,
  ITEM_DISLEXIA_FRIENDLY,
  ITEM_DISLEXIA_FRIENDLY_STEP_1,
  ITEM_DISLEXIA_FRIENDLY_STEP_2,
  ITEM_INTERLINEADO,
  ITEM_INTERLINEADO_STEP_1,
  ITEM_INTERLINEADO_STEP_3,
  ITEM_READING_MASK,
  ITEM_READING_MASK_STEP_1,
  ITEM_READING_MASK_STEP_2,
  ITEM_TEXT_SIZE,
  ITEM_TEXT_SIZE_STEP_1,
  ITEM_TEXT_SIZE_STEP_4,
  LANGUAGE_ES,
  LANGUAGES,
  PROFILE_DALTONISMO,
  PROFILE_DEFAULT_EMPTY,
  PROFILE_DISLEXIA,
  PROFILE_TDAH,
  PROFILE_VISION_BAJA,
  PROFILES
} from './accesibilidad.constant';
import { AccessibilityItem, AccessibilityStep, Language, Profile } from './accesibilidad.interface';

@Component({
  selector: 'app-accesibilidad',
  templateUrl: './accesibilidad.component.html',
  standalone: false
})
export class AccesibilidadComponent {
  languages: Language[] = [...LANGUAGES];
  currentLang: string = 'es'; // valor por defecto
  itemTextSize: AccessibilityItem = { ...ITEM_TEXT_SIZE };
  itemContrast: AccessibilityItem = { ...ITEM_CONTRAST };
  itemReadingMask: AccessibilityItem = { ...ITEM_READING_MASK };
  itemDislexiaFriendly: AccessibilityItem = { ...ITEM_DISLEXIA_FRIENDLY };
  itemCursor: AccessibilityItem = { ...ITEM_CURSOR };
  itemInterLineado: AccessibilityItem = { ...ITEM_INTERLINEADO };
  profiles: WritableSignal<Profile[]> = signal<Profile[]>([...PROFILES]);
  currentProfile: Profile = { ...PROFILE_DEFAULT_EMPTY };
  isMobile: WritableSignal<boolean> = signal<boolean>(false);
  profileTdah: Profile = { ...PROFILE_TDAH };

  constructor(
    private readonly _bottomSheet: MatBottomSheet,
    private readonly translateService: TranslateService,
    private readonly cursorService: CursorService
  ) {
    effect(() => {
      this.itemTextSize.stepActive = this.cursorService.textSizeActive();
      this.itemContrast.stepActive = this.cursorService.contrastActive();
      this.itemReadingMask.stepActive = this.cursorService.readingMaskActive();
      this.itemDislexiaFriendly.stepActive = this.cursorService.dislexiaFriendlyActive();
      this.itemCursor.stepActive = this.cursorService.cursorActive();
      this.itemInterLineado.stepActive = this.cursorService.interlineadoActive();
      this.currentProfile = this.cursorService.profileActive();
      this.isMobile = this.cursorService.isMobile;
    });

    this.currentLang = this.translateService.currentLang ?? localStorage.getItem('selectedLang') ?? LANGUAGE_ES;
  }

  getTranslatedLangName(code: string): string {
    const lang = this.languages.find((l) => l.code === code);
    return lang ? this.translateService.instant(lang.name) : code;
  }

  changeLanguage(lang: string) {
    this.translateService.use(lang);
    localStorage.setItem('selectedLang', lang);
    this.currentLang = lang;
  }

  onSelectionChange(event: MatSelectionListChange): void {
    this.currentLang = event.options[0].value;
    this.changeLanguage(this.currentLang);
  }

  closeBottomSheet(): void {
    this._bottomSheet.dismiss(AccesibilidadComponent);
  }

  toggleItemTextSize(): void {
    const nextItem = this.getNextItem(this.itemTextSize.stepActive, this.itemTextSize.steps);
    this.cursorService.textSizeActiveUpdate(nextItem);
  }

  toggleItemContrast(): void {
    const nextItem = this.getNextItem(this.itemContrast.stepActive, this.itemContrast.steps);
    this.cursorService.contrastActiveUpdate(nextItem);
  }

  toggleItemCursor(): void {
    const nextItem = this.getNextItem(this.itemCursor.stepActive, this.itemCursor.steps);
    this.cursorService.cursorActiveUpdate(nextItem);
  }

  toggleItemReadingMask(): void {
    const nextItem = this.getNextItem(this.itemReadingMask.stepActive, this.itemReadingMask.steps);
    this.cursorService.readingMaskActiveUpdate(nextItem);
  }

  toggleItemDislexiaFriendly(): void {
    const nextItem = this.getNextItem(this.itemDislexiaFriendly.stepActive, this.itemDislexiaFriendly.steps);
    this.cursorService.dislexiaFriendlyActiveUpdate(nextItem);
  }

  toggleItemInterLine(): void {
    const nextItem = this.getNextItem(this.itemInterLineado.stepActive, this.itemInterLineado.steps);
    this.cursorService.interlineadoActiveUpdate(nextItem);
  }

  toggleProfile(profile: Profile): void {
    this.resetCursorService();
    if (profile.id === this.currentProfile.id) {
      return;
    }

    this.currentProfile = profile;
    this.cursorService.profileActiveUpdate(profile);
    if (profile.id === PROFILE_VISION_BAJA.id) {
      this.cursorService.textSizeActiveUpdate(ITEM_TEXT_SIZE_STEP_4);
      this.cursorService.cursorActiveUpdate(ITEM_CURSOR_STEP_2);
    } else if (profile.id === PROFILE_DISLEXIA.id) {
      this.cursorService.dislexiaFriendlyActiveUpdate(ITEM_DISLEXIA_FRIENDLY_STEP_2);
      this.cursorService.interlineadoActiveUpdate(ITEM_INTERLINEADO_STEP_3);
    } else if (profile.id === PROFILE_TDAH.id) {
      this.cursorService.readingMaskActiveUpdate(ITEM_READING_MASK_STEP_2);
    } else if (profile.id === PROFILE_DALTONISMO.id) {
      this.cursorService.contrastActiveUpdate(ITEM_CONTRAST_STEP_2);
    }
  }

  resetAll(): void {
    this.cursorService.resetAll();
    this.currentLang = LANGUAGE_ES;
    this.changeLanguage(this.currentLang);
  }

  private resetCursorService(): void {
    this.cursorService.textSizeActiveUpdate(ITEM_TEXT_SIZE_STEP_1);
    this.cursorService.contrastActiveUpdate(ITEM_CONTRAST_STEP_1);
    this.cursorService.readingMaskActiveUpdate(ITEM_READING_MASK_STEP_1);
    this.cursorService.dislexiaFriendlyActiveUpdate(ITEM_DISLEXIA_FRIENDLY_STEP_1);
    this.cursorService.cursorActiveUpdate(ITEM_CURSOR_STEP_1);
    this.cursorService.interlineadoActiveUpdate(ITEM_INTERLINEADO_STEP_1);
    this.cursorService.resetAll();
  }

  private getNextItem(selectedItem: AccessibilityStep, steps: AccessibilityStep[]): AccessibilityStep {
    this.cursorService.profileActiveUpdate(PROFILE_DEFAULT_EMPTY);
    const index = steps.findIndex((step) => step.id === selectedItem.id);
    const nextIndex = index === -1 || index === steps.length - 1 ? 0 : index + 1;
    return steps[nextIndex];
  }
}
