import { AccessibilityItem, Language, Profile } from './accesibilidad.interface';

const ICON_PATH = 'assets/img/accesibilidad/';

export const KEY_STORE_ACCESSIBILITY = {
  textSizeActive: 'ACCESSIBILITY_TEXT_SIZE_ACTIVE',
  contrastActive: 'ACCESSIBILITY_CONTRAST_ACTIVE',
  readingMaskActive: 'ACCESSIBILITY_READING_MASK_ACTIVE',
  dislexiaFriendlyActive: 'ACCESSIBILITY_DISLEXIA_FRIENDLY_ACTIVE',
  cursorActive: 'ACCESSIBILITY_CURSOR_ACTIVE',
  interlineActive: 'ACCESSIBILITY_INTERLINE_ACTIVE',
  profileActive: 'ACCESSIBILITY_PROFILE_ACTIVE',
  keyboardShortcuts: 'ACCESSIBILITY_KEYBOARD_SHORTCUTS_CONFIG'
};

export const ITEM_READING_MASK_STEP_1 = { id: '1', description: 'Accesibilidad.Mascara' };
export const ITEM_READING_MASK_STEP_2 = { id: '2', description: 'Accesibilidad.Mascara' };
export const ITEM_READING_MASK: AccessibilityItem = {
  icon: 'mascara',
  steps: [ITEM_READING_MASK_STEP_1, ITEM_READING_MASK_STEP_2],
  stepActive: ITEM_READING_MASK_STEP_1
};

export const ITEM_CURSOR_STEP_1 = { id: '1', description: 'Accesibilidad.Cursor' };
export const ITEM_CURSOR_STEP_2 = { id: '2', description: 'Accesibilidad.CursorGrande' };
export const ITEM_CURSOR: AccessibilityItem = {
  icon: 'cursor',
  steps: [ITEM_CURSOR_STEP_1, ITEM_CURSOR_STEP_2],
  stepActive: ITEM_CURSOR_STEP_1
};

export const ITEM_TEXT_SIZE_STEP_1 = { id: '72', description: 'Accesibilidad.TamanoTexto' };
export const ITEM_TEXT_SIZE_STEP_2 = { id: '80', description: 'Accesibilidad.TamanoTexto' };
export const ITEM_TEXT_SIZE_STEP_3 = { id: '85', description: 'Accesibilidad.TamanoTexto' };
export const ITEM_TEXT_SIZE_STEP_4 = { id: '90', description: 'Accesibilidad.TamanoTexto' };
export const ITEM_TEXT_SIZE: AccessibilityItem = {
  icon: 'txt-icon-1',
  steps: [ITEM_TEXT_SIZE_STEP_1, ITEM_TEXT_SIZE_STEP_2, ITEM_TEXT_SIZE_STEP_3, ITEM_TEXT_SIZE_STEP_4],
  stepActive: ITEM_TEXT_SIZE_STEP_1
};

export const ITEM_CONTRAST_STEP_1 = { id: 'background-color-base', description: 'Accesibilidad.Contrastes' };
export const ITEM_CONTRAST_STEP_2 = { id: 'background-color-base-2', description: 'Accesibilidad.Contrastes' };
export const ITEM_CONTRAST_STEP_3 = { id: 'background-color-base-3', description: 'Accesibilidad.Contrastes' };
export const ITEM_CONTRAST: AccessibilityItem = {
  icon: 'inverter-icon',
  steps: [ITEM_CONTRAST_STEP_1, ITEM_CONTRAST_STEP_2, ITEM_CONTRAST_STEP_3],
  stepActive: ITEM_CONTRAST_STEP_1
};

export const ITEM_DISLEXIA_FRIENDLY_STEP_1 = { id: '', description: 'Accesibilidad.DislexiaAmigable' };
export const ITEM_DISLEXIA_FRIENDLY_STEP_2 = { id: 'dislexia-friendly', description: 'Accesibilidad.DislexiaAmigable' };
export const ITEM_DISLEXIA_FRIENDLY: AccessibilityItem = {
  icon: 'dislexia',
  steps: [ITEM_DISLEXIA_FRIENDLY_STEP_1, ITEM_DISLEXIA_FRIENDLY_STEP_2],
  stepActive: ITEM_DISLEXIA_FRIENDLY_STEP_1
};

export const ITEM_INTERLINEADO_STEP_1 = { id: 'inter-line-normal', description: 'Accesibilidad.Interlineado' };
export const ITEM_INTERLINEADO_STEP_2 = { id: 'inter-line-42px', description: 'Accesibilidad.Interlineado' };
export const ITEM_INTERLINEADO_STEP_3 = { id: 'inter-line-48px', description: 'Accesibilidad.Interlineado' };
export const ITEM_INTERLINEADO: AccessibilityItem = {
  icon: 'interlineado',
  steps: [ITEM_INTERLINEADO_STEP_1, ITEM_INTERLINEADO_STEP_2, ITEM_INTERLINEADO_STEP_3],
  stepActive: ITEM_INTERLINEADO_STEP_1
};

export const LANGUAGES: Language[] = [
  { code: 'es', name: 'languages.es', flag: 'assets/flags/pe.svg' },
  { code: 'en', name: 'languages.en', flag: 'assets/flags/en.svg' },
  { code: 'qu', name: 'languages.qu', flag: 'assets/flags/qu.svg' }
];

export const PROFILE_VISION_BAJA: Profile = { id: '1', name: 'Accesibilidad.VisionBaja', icon: `${ICON_PATH}iconVision.svg` };
export const PROFILE_DISLEXIA: Profile = { id: '2', name: 'Accesibilidad.Dislexia', icon: `${ICON_PATH}iconDislexia.svg` };
export const PROFILE_TDAH: Profile = { id: '3', name: 'Accesibilidad.TDAH', icon: `${ICON_PATH}iconTDAH.svg` };
export const PROFILE_DALTONISMO: Profile = { id: '4', name: 'Accesibilidad.Daltonismo', icon: `${ICON_PATH}iconDaltonismo.svg` };
export const PROFILE_DEFAULT_EMPTY: Profile = { id: '0', name: '', icon: '' };
export const PROFILES: Profile[] = [PROFILE_VISION_BAJA, PROFILE_DISLEXIA, PROFILE_TDAH, PROFILE_DALTONISMO];

export const CONTAINER_ACCESSIBILITY_CLASS_NAME = 'accessibility-container';

export const LANGUAGE_ES = 'es';