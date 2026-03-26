import { CommonModule } from '@angular/common';
import { Component, effect, signal, WritableSignal } from '@angular/core';
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule, MatSelectionListChange } from '@angular/material/list';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
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
} from './accessibility.constant';
import { AccessibilityItem, AccessibilityStep, Language, Profile } from './accessibility.interface';
import { AccessibilityService } from './services/accessibility.service';

@Component({
  selector: 'app-accessibility',
  templateUrl: './accessibility.component.html',
  standalone: true,
  imports: [
    CommonModule,
    MatExpansionModule,
    MatIconModule,
    MatListModule,
    TranslateModule
  ]
})
export class AccessibilityComponent {
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
    private readonly accessibilityService: AccessibilityService
  ) {
    effect(() => {
      this.itemTextSize.stepActive = this.accessibilityService.textSizeActive();
      this.itemContrast.stepActive = this.accessibilityService.contrastActive();
      this.itemReadingMask.stepActive = this.accessibilityService.readingMaskActive();
      this.itemDislexiaFriendly.stepActive = this.accessibilityService.dislexiaFriendlyActive();
      this.itemCursor.stepActive = this.accessibilityService.cursorActive();
      this.itemInterLineado.stepActive = this.accessibilityService.interlineadoActive();
      this.currentProfile = this.accessibilityService.profileActive();
      this.isMobile = this.accessibilityService.isMobile;
    });

    this.currentLang = localStorage.getItem('selectedLang') ?? LANGUAGE_ES;
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
    this._bottomSheet.dismiss(AccessibilityComponent);
  }

  toggleItemTextSize(): void {
    const nextItem = this.getNextItem(this.itemTextSize.stepActive, this.itemTextSize.steps);
    this.accessibilityService.textSizeActiveUpdate(nextItem);
  }

  toggleItemContrast(): void {
    const nextItem = this.getNextItem(this.itemContrast.stepActive, this.itemContrast.steps);
    this.accessibilityService.contrastActiveUpdate(nextItem);
  }

  toggleItemCursor(): void {
    const nextItem = this.getNextItem(this.itemCursor.stepActive, this.itemCursor.steps);
    this.accessibilityService.cursorActiveUpdate(nextItem);
  }

  toggleItemReadingMask(): void {
    const nextItem = this.getNextItem(this.itemReadingMask.stepActive, this.itemReadingMask.steps);
    this.accessibilityService.readingMaskActiveUpdate(nextItem);
  }

  toggleItemDislexiaFriendly(): void {
    const nextItem = this.getNextItem(this.itemDislexiaFriendly.stepActive, this.itemDislexiaFriendly.steps);
    this.accessibilityService.dislexiaFriendlyActiveUpdate(nextItem);
  }

  toggleItemInterLine(): void {
    const nextItem = this.getNextItem(this.itemInterLineado.stepActive, this.itemInterLineado.steps);
    this.accessibilityService.interlineadoActiveUpdate(nextItem);
  }

  toggleProfile(profile: Profile): void {
    this.resetCursorService();
    if (profile.id === this.currentProfile.id) {
      return;
    }

    this.currentProfile = profile;
    this.accessibilityService.profileActiveUpdate(profile);
    if (profile.id === PROFILE_VISION_BAJA.id) {
      this.accessibilityService.textSizeActiveUpdate(ITEM_TEXT_SIZE_STEP_4);
      this.accessibilityService.cursorActiveUpdate(ITEM_CURSOR_STEP_2);
    } else if (profile.id === PROFILE_DISLEXIA.id) {
      this.accessibilityService.dislexiaFriendlyActiveUpdate(ITEM_DISLEXIA_FRIENDLY_STEP_2);
      this.accessibilityService.interlineadoActiveUpdate(ITEM_INTERLINEADO_STEP_3);
    } else if (profile.id === PROFILE_TDAH.id) {
      this.accessibilityService.readingMaskActiveUpdate(ITEM_READING_MASK_STEP_2);
    } else if (profile.id === PROFILE_DALTONISMO.id) {
      this.accessibilityService.contrastActiveUpdate(ITEM_CONTRAST_STEP_2);
    }
  }

  resetAll(): void {
    this.accessibilityService.resetAll();
    this.currentLang = LANGUAGE_ES;
    this.changeLanguage(this.currentLang);
  }

  private resetCursorService(): void {
    this.accessibilityService.textSizeActiveUpdate(ITEM_TEXT_SIZE_STEP_1);
    this.accessibilityService.contrastActiveUpdate(ITEM_CONTRAST_STEP_1);
    this.accessibilityService.readingMaskActiveUpdate(ITEM_READING_MASK_STEP_1);
    this.accessibilityService.dislexiaFriendlyActiveUpdate(ITEM_DISLEXIA_FRIENDLY_STEP_1);
    this.accessibilityService.cursorActiveUpdate(ITEM_CURSOR_STEP_1);
    this.accessibilityService.interlineadoActiveUpdate(ITEM_INTERLINEADO_STEP_1);
    this.accessibilityService.resetAll();
  }

  private getNextItem(selectedItem: AccessibilityStep, steps: AccessibilityStep[]): AccessibilityStep {
    this.accessibilityService.profileActiveUpdate(PROFILE_DEFAULT_EMPTY);
    const index = steps.findIndex((step) => step.id === selectedItem.id);
    const nextIndex = index === -1 || index === steps.length - 1 ? 0 : index + 1;
    return steps[nextIndex];
  }
}
