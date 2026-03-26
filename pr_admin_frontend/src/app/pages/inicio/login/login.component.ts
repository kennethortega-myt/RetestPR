import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { AlertsComponent } from '../../../components/alerts/alerts.component';
import { ModalAlertType, ILoginModalData } from '../../../components/alerts/alerts.constants';
import { DialogConfirmComponent } from '../../../components/dialog/dialog-confirm/dialog-confirm.component';
import { Login, Restore } from '../../../interfaces/login';
import { AuthService } from '../../../services/auth-service.service';
import { EncryptionService } from '../../../services/encryption.service';
import { PasswordUpdateComponent } from '../password-update/password-update.component';
import { RestorePasswordComponent } from '../restore-password/restore-password.component';
import { BloqueadorNavegacionService } from '../../../services/navegacion.service';
import { CommonModule } from '@angular/common';
import { ComponentsModule } from '../../../components/components.module';
import { finalize } from 'rxjs';
import { Usuario } from '../../../interfaces/usuario-bean';
import { AuthComponent } from '../../../helpers/auth-component';
import { IdSessionService } from '../../../services/session-id.service';
import { openDialogMensaje } from '../../../utils/funciones';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatIconModule, CommonModule, ComponentsModule],
})
export class LoginComponent extends AuthComponent {
  private readonly formBuilder = inject(FormBuilder);
  public formGroupParent!: FormGroup;
  showPassword = false;
  usuario?: Usuario;
  loginBackgroundImg = '';

  private readonly DIALOG_CONFIG = {
    width: '20%',
    minWidth: '320px',
    maxWidth: '100%'
  };

  private readonly LOGIN_BACKGROUNDS = [
    'assets/img/fondo01.svg',
    'assets/img/fondo02.svg',
    'assets/img/fondo03.svg',
    'assets/img/fondo04.svg',
    'assets/img/fondo05.svg'
  ];

  constructor(
    public dialog: MatDialog,
    private readonly router: Router,
    private readonly authenticationService: AuthService,
    private readonly encryptionService: EncryptionService,
    private readonly bloqueadorNavegacionService: BloqueadorNavegacionService,
    private readonly sesionIdService: IdSessionService
  ) {
    super();
    this.formGroupParent = new FormGroup({});
    this.reloadRandomLoginImage();
  }

  ngOnInit() {
    this.buildForm();
    this.bloqueadorNavegacionService.bloquearRetroceso();
    sessionStorage.clear();
    this.sesionIdService.generarIdSesion();
  }

  private buildForm(): void {
    this.formGroupParent = this.formBuilder.group({
      usuario: new FormControl('', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50),
        Validators.pattern(/^(?!.*[-_]{2})(?![-_])[A-Za-z0-9]+(?:[-_][A-Za-z0-9]+)*(?<![-_])$/)
      ]),
      clave: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(30)
      ]),
    });

    this.formGroupParent.get('usuario')?.valueChanges.subscribe(value => {
      if (!value) return;
      let cleaned = value.replace(/[^a-zA-Z0-9_-]/g, '').toUpperCase();
      if (cleaned.length > 50) {
        cleaned = cleaned.substring(0, 51);
      }
      if (value !== cleaned) {
        this.formGroupParent.get('usuario')?.setValue(cleaned, { emitEvent: false });
        this.formGroupParent.get('usuario')?.updateValueAndValidity();
      }
    });

    this.formGroupParent.get('clave')?.valueChanges.subscribe(value => {
      if (!value) return;
      if (value.length > 30) {
        const truncated = value.substring(0, 31);
        this.formGroupParent.get('clave')?.setValue(truncated, { emitEvent: false });
        this.formGroupParent.get('clave')?.setErrors({ maxlengthExceeded: true });
        this.formGroupParent.get('clave')?.updateValueAndValidity();
      } else if (this.formGroupParent.get('clave')?.hasError('maxlengthExceeded')) {
        const errors = { ...this.formGroupParent.get('clave')?.errors };
        delete errors['maxlengthExceeded'];
        this.formGroupParent.get('clave')?.setErrors(Object.keys(errors).length ? errors : null);
      }
    });
  }

  ngOnDestroy() {
    this.bloqueadorNavegacionService.permitirRetroceso();
  }

  ingresar(): void {
    if (this.formGroupParent.invalid) {
      this.formGroupParent.markAllAsTouched();
      return;
    }
    this.sesionIdService.generarIdSesion();

    const usuarioValue = this.formGroupParent.get('usuario')?.value;
    const claveValue = this.formGroupParent.get('clave')?.value;

    if (!usuarioValue || !claveValue) {
      this.openDialog('empty_credentials');
      return;
    }

    localStorage.clear();
    const login = new Login();
    login.username = usuarioValue.toUpperCase();
    login.password = this.encryptionService.encryptPassword(claveValue);
    this.authentica(login);
  }

  private authentica(login: Login): void {
    this.authenticationService.getToken(login).subscribe({
      next: (result) => this.handleLoginSuccess(result, login),
      error: (err) => openDialogMensaje(this.dialog, err.error.message ?? err.error.username, 2)
    });
  }

  private handleLoginSuccess(result: any, login: Login): void {
    const token = JSON.parse(JSON.stringify(result));
    if (!token) return;

    this.storeTokens(token, login.username);

    if (token['claveNueva'] == 1) {
      this.handlePasswordUpdate(token);
    } else {
      this.navigateToMain();
    }
  }

  private storeTokens(token: any, username?: string): void {
    localStorage.setItem('nombreusuario', username ?? '');
    localStorage.setItem('token', JSON.stringify(token['token']));
    localStorage.setItem('refreshToken', JSON.stringify(token['refreshToken']));
  }

  private handlePasswordUpdate(token: any): void {
    const dialogPasswordUpdate = this.dialog.open(
      PasswordUpdateComponent,
      {
        ...this.DIALOG_CONFIG,
        disableClose: true
      }
    );

    dialogPasswordUpdate.afterClosed().subscribe((result) => {
      if (!result) return;

      const { clave, clave2 } = result;
      if (clave !== clave2) return;

      this.confirmPasswordUpdate(clave, clave2, token);
    });
  }

  private confirmPasswordUpdate(clave: string, clave2: string, token: any): void {
    const dialogConfirm = this.dialog.open(DialogConfirmComponent, this.DIALOG_CONFIG);

    dialogConfirm.afterClosed().subscribe((resultConfirm) => {
      if (!resultConfirm) return;

      const encryptedClave = this.encryptionService.encryptPassword(clave);
      const encryptedClave2 = this.encryptionService.encryptPassword(clave2);

      this.authenticationService
        .passwordUpdate(encryptedClave, encryptedClave2, token['tokenSasa'])
        .pipe(finalize(() => this.formGroupParent.get('clave')?.setValue('')))
        .subscribe({
          next: (resultado) => {
            if (resultado.resultado == 1) {
              openDialogMensaje(this.dialog, resultado.mensaje, 1);
            }
          },
          error: () => {
            openDialogMensaje(this.dialog, 'Error al actualizar la contraseña', 2);
          }
        });
    });
  }

  private navigateToMain(): void {
    this.usuario = this.authentication();
    this.authenticationService.getAccesos({
      idPerfil: this.usuario.perfil.idPerfil,
      idUsuario: this.usuario.userId,
    }).subscribe({
      next: (result: any) => {
        const { datos } = result?.data;
        this.setAccesos(datos);
        this.router.navigate(['main']).then(() => {
          globalThis.location.reload();
        });
      },
      error: (error) => console.error('error:', error)
    });
  }


  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      this.togglePasswordVisibility();
    }
  }

  openDialog(type: ModalAlertType): void {
    this.dialog.open<AlertsComponent, ILoginModalData>(AlertsComponent, {
      ...this.DIALOG_CONFIG,
      data: { type },
    });
  }

  reloadRandomLoginImage(): void {
    const randomValues = new Uint8Array(1);
    crypto.getRandomValues(randomValues);
    const index = randomValues[0] % this.LOGIN_BACKGROUNDS.length;
    this.loginBackgroundImg = this.LOGIN_BACKGROUNDS[index];
  }

  openDialogRestorePassword(): void {
    const dialogRefRestorePassword = this.dialog.open(
      RestorePasswordComponent,
      {
        ...this.DIALOG_CONFIG,
        disableClose: true
      }
    );

    dialogRefRestorePassword.afterClosed().subscribe((resultUser) => {
      if (!resultUser) return;

      this.confirmRestorePassword(resultUser);
    });
  }

  private confirmRestorePassword(resultUser: string): void {
    const dialogConfirm = this.dialog.open(DialogConfirmComponent, this.DIALOG_CONFIG);

    dialogConfirm.afterClosed().subscribe((resultConfirm) => {
      if (!resultConfirm) return;

      const restore: Restore = { usuario: resultUser };
      this.authenticationService.passwordRestore(restore)
        .pipe(finalize(() => this.formGroupParent.get('clave')?.setValue('')))
        .subscribe({
          next: (resultado) => {
            const tipo = resultado.resultado == 1 ? 1 : 2;
            openDialogMensaje(this.dialog, resultado.mensaje, tipo);
          },
          error: (err) => {
            openDialogMensaje(this.dialog, err.error.message ?? err.error.username, 2);
          }
        });
    });
  }

  bloquearEvent(event: Event): void {
    event.preventDefault();
  }
}