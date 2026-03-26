import { TemplateRef } from "@angular/core";

export type DialogKey = 'warning' | 'success' | 'error' | 'info' | 'confirm' | 'empty_credentials' | 'incorrect_credentials';

export interface IDialogConfigData {
  key?: string;
  type?: 0 | 1 | 2 | 3;
  title?: string;
  message?: string;
  confirm?: boolean;
  iconPath?: string;  
  contentTemplate?: TemplateRef<any>;
  onConfirm?: () => void;
}

// Data shape accepted by the DialogComponent. Extends the service config but
// keeps the legacy Spanish field names `mensaje`/`tipo` for backwards compat.
export interface IDialogComponentData extends IDialogConfigData {
  mensaje?: string;
  tipo?: 0 | 1 | 2 | 3;
}

export const  TYPE_ICON_MAP: { [key: number]: string } = {
  0: 'assets/img/alert/ico-warning.svg', // Warning
  1: 'assets/img/alert/ico-check.svg',   // Success
  2: 'assets/img/alert/ico-error.svg',   // Error
  3: 'assets/img/alert/ico-info.svg'     // Info
};

export type DialogConfigs = { [key in DialogKey]: IDialogConfigData };

export const DIALOG_CONFIGS: DialogConfigs = {
  warning: {
    type: 0, // 0 for warning
    title: 'Advertencia',
    iconPath: TYPE_ICON_MAP[0],
  },
  success: {
    type: 1, // 1 for success
    title: 'Éxito',
    message: 'Realizado Satisfactoriamente',
    iconPath: TYPE_ICON_MAP[1],
  },
  error: {
    type: 2, // 2 for error
    title: 'Error',
    message: 'Error',
    iconPath: TYPE_ICON_MAP[2],
  },
  info: {
    type: 3, // 3 for info
    title: 'Información',
    message: 'Realizado Satisfactoriamente',
    iconPath: TYPE_ICON_MAP[3],
  },

  empty_credentials: {
    type: 0, // warning
    title: 'Formulario incompleto',
    message: 'Debe completar de manera obligatoria los campos <b>Usuario</b> y <b>Contraseña</b>.',
  },
  incorrect_credentials: {
    type: 2, // error
    title: 'Datos incorrectos',
    message: 'Credenciales incorrectas. Por favor verifique su nombre de <b>usuario</b> y/o <b>contraseña</b> e intente iniciar sesión nuevamente.',
  },
  confirm: {
    type: 3, // info (often used for confirmation, can be a specific icon)
    title: 'Confirmación',
    message: '¿Está seguro de realizar la operación?',
    confirm: true,
  },
};

export const DEFAULT_DIALOG_SIZE = {
  width: '320px',
  minWidth: '280px',
  maxWidth: '90%',
};
