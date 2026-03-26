export type ModalAlertType = 'empty_credentials' | 'incorrect_credentials';

export interface ILoginConfigData {
  title: string;
  icon: string;
  message: string;
}

export type AlertElementsType = { [key in ModalAlertType]: ILoginConfigData };

export const LOGIN_MODAL_CONFIGS: AlertElementsType = {
  empty_credentials: {
    title: 'Formulario incompleto',
    icon: 'assets/img/icons/icono_alert.svg',
    message:
      'Debe completar de manera obligatoria los campos <b>Usuario</b> y <b>Contraseña</b>.',
  },
  incorrect_credentials: {
    title: 'Datos incorrectos',
    icon: 'assets/img/icons/icono_x.svg',
    message:
      'Credenciales incorrectas. Por favor verifique su nombre de <b>usuario</b> y/o <b>contraseña</b> e intente iniciar sesión nuevamente.',
  },
};

export interface ILoginModalData {
  type: ModalAlertType;
}
