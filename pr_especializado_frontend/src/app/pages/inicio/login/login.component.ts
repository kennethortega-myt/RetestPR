import { Component, inject, OnInit, Renderer2 } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { finalize, Subject, takeUntil } from 'rxjs';

import { AuthService } from '../../../services/auth-service.service';
import { EncryptionService } from '../../../services/encryption.service';
import { ROUTE_PATHS } from '../../../settings/app-routing.settings';
import { Login, LoginResponse, PasswordUpdateResponse, SesionActiva } from '../../../interfaces/login';
import { RecaptchaService } from '../../../services/recaptcha.service';
import { BloqueadorNavegacionService } from '../../../services/navegacion.service';
import { DialogService } from '../../../services/dialog.service';
import { ActualizarContrasenaComponent } from '../actualizar-contrasena/actualizar-contrasena.component';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { DataLoginStore } from '../../../states/data-login.store';
import { RecuperarContrasenaComponent } from '../recuperar-contrasena/recuperar-contrasena.component';
import { InactivityTimeoutService } from '../../../services/inactivity-timeout.service';
import { TranslateModule } from '@ngx-translate/core';
import { IdSessionService } from '../../../services/session-id.service';
import { ERROR_SESSION_UNICA, FIELD_LIMITS } from '../../../helpers/constantes';
import { GenericResponse } from '../../../services/common/response.common';
import { HttpErrorResponse } from '@angular/common/http';
import { MaeImportarService } from '../../../services/maeimportar.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  standalone: true,
  imports: [TranslateModule, ReactiveFormsModule, RouterModule, MatFormFieldModule, MatInputModule, MatIconModule, CommonModule],
})
export class LoginComponent implements OnInit {

  public routePaths = ROUTE_PATHS;
  public formGroupParent!: FormGroup;
  public errorMessage: string = '';
  public imgSrc: string = '#';
  showPassword = false;
  onKeyDown: any;
  private destroy$ = new Subject<void>();

  private readonly dataLoginStore = inject(DataLoginStore);
  private readonly dialogService = inject(DialogService);
  private readonly fechaService = inject(MaeImportarService);
  readonly FIELD_LIMITS = FIELD_LIMITS;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly encryptionService: EncryptionService,
    private readonly router: Router,
    private readonly recaptchaService: RecaptchaService,
    private readonly renderer: Renderer2,
    private readonly bloqueadorNavegacionService: BloqueadorNavegacionService,
    private readonly inactivityService: InactivityTimeoutService,
    private readonly sesionIdService: IdSessionService) {
    this.formGroupParent = new FormGroup({});
  }

  ngOnInit() {
    this.sesionIdService.generarIdSesion();
    this.buildForm();
    this.loadImageRandom();
    this.bloqueadorNavegacionService.bloquearRetroceso();
    this.renderer.addClass(document.body, 'recaptcha');
    localStorage.clear();
    // Marcar campos como touched cuando pierden el foco y están vacíos
    this.formGroupParent.get('clave')?.valueChanges.subscribe(value => {
      if (!value || value.trim() === '') {
        this.formGroupParent.get('clave')?.markAsTouched();
      }
    });
  }

  private buildForm(): void {
    this.formGroupParent = this.fb.group({
      usuario: new FormControl('', [
        Validators.required,
        Validators.minLength(FIELD_LIMITS.USUARIO_MIN_LENGTH),
        Validators.maxLength(FIELD_LIMITS.USUARIO_MAX_LENGTH),
        Validators.pattern(/^(?!.*[-_]{2})(?![-_])[A-Za-z0-9]+(?:[-_][A-Za-z0-9]+)*(?<![-_])$/)
      ]),
      clave: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(30)
      ]),
    });

    this.formGroupParent.get('usuario')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        if (!value) return;
        let cleaned = value.replace(/[^a-zA-Z0-9_-]/g, '').toUpperCase();
        if (cleaned.length > FIELD_LIMITS.USUARIO_MAX_LENGTH_FAKE) {
          cleaned = cleaned.substring(0, FIELD_LIMITS.USUARIO_MAX_LENGTH_FAKE);
        }
        if (value !== cleaned) {
          this.formGroupParent.get('usuario')?.setValue(cleaned, {
            emitEvent: false
          });

          this.formGroupParent.get('usuario')?.updateValueAndValidity({
            emitEvent: false
          });
        }
      });

    this.formGroupParent.get('clave')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        if (!value) return;

        if (value.length > FIELD_LIMITS.CLAVE_MAX_LENGTH_FAKE) {
          const truncated = value.substring(0, FIELD_LIMITS.CLAVE_MAX_LENGTH_FAKE);
          this.formGroupParent.get('clave')?.setValue(truncated, {
            emitEvent: false
          });

          this.formGroupParent.get('clave')?.setErrors({
            maxlengthExceeded: true
          });

          this.formGroupParent.get('clave')?.updateValueAndValidity({
            emitEvent: false
          });
        }
        else if (this.formGroupParent.get('clave')?.hasError('maxlengthExceeded')) {
          const errors = { ...this.formGroupParent.get('clave')?.errors };
          delete errors['maxlengthExceeded'];
          this.formGroupParent.get('clave')?.setErrors(
            Object.keys(errors).length ? errors : null
          );
        }
      });

    this.formGroupParent.get('clave')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        if (!value || value.trim() === '') {
          this.formGroupParent.get('clave')?.markAsTouched();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  public ingresar(): void {
    this.sesionIdService.generarIdSesion();
    this.errorMessage = '';
    if (this.formGroupParent.invalid) {
      this.errorMessage = 'El usuario o contraseña es requerido';
      this.dialogService.mostrarMensajeError(`${this.errorMessage}`);
      return;
    }

    const login = this.getLoginPayload();
    this.authentica(login);
  }

  private getLoginPayload(): Login {
    const usuario = this.formGroupParent.get('usuario')?.value || '';
    const clave = this.formGroupParent.get('clave')?.value || '';
    return {
      username: usuario.toUpperCase(),
      password: this.encryptionService.encryptPassword(clave)
    };
  }

  private authentica(login: Login): void {
    this.recaptchaService.execute('importantAction').then(token => {
      const loginReq = {
        ...login,
        recaptcha: token
      };

      this.authService.getToken(loginReq)
        .subscribe({
          next: (result: LoginResponse) => this.handleAuthSuccess(result, String(login.username)),
          error: (error) => {
            const errorMessage = error.error;
            if (errorMessage.errorCode == ERROR_SESSION_UNICA) {
              this.dialogService.mostrarMensajeConfirmacion(errorMessage?.message || '')
                .subscribe(aceptar => {
                  if (aceptar) {
                    const sesionActiva: SesionActiva = {
                      usuario: login.username
                    };
                    this.onSesionActiva(sesionActiva);
                  }
                });
            }
            else {
              this.handleAuthError(error);
            }
          }
        });

    }).catch(err => {
      console.error('Error con reCAPTCHA: ' + err);
    });
  }

  onSesionActiva(input: SesionActiva): void {
    this.authService.cerrarSessionActiva(input).subscribe({
      next: (response: GenericResponse) => {
        if (response.success) {
          this.formGroupParent.get('clave')?.setValue('');
          this.dialogService.mostrarMensajeExito(response.message ?? '');
        }
        else {
          this.handleAuthError(response.message);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.handleAuthError(error);
      }
    });
  }

  private handleAuthSuccess(result: LoginResponse, username: string): void {
    const data = result?.data;
    if (!data) return;

    const { datos } = data;
    const { token, refreshToken } = datos.usuario;

    if (result.success && token) {
      datos.usuario.username = username;
      this.dataLoginStore.setDataLogin(datos);
      localStorage.setItem('token', JSON.stringify(token));
      localStorage.setItem('refreshToken', JSON.stringify(refreshToken));

      if (result.data.datos.usuario.claveNueva == 1) {
        this.actualizarClave(token);
      } else {
        this.authService.getAccesos({
          idPerfil: datos.perfiles![0].idPerfil,
          idUsuario: datos.usuario.idUsuario,
        })
          .subscribe({
            next: (result: LoginResponse) => {
              const data = result?.data;
              if (!data) return;

              const { datos } = data;
              this.dataLoginStore.updateProp('modulos', datos.modulos);
              this.inactivityService.startMonitoring();

              this.fechaService.validarProcesoImportar$().subscribe({
                next: (res) => {
                  const isValid = res.data ?? false;
                  this.dataLoginStore.updateProp('isProcessValid', isValid);

                  if (isValid) {
                    this.router.navigate(['home']);
                  } else {
                    this.router.navigate(['home/inicio']);
                    this.dialogService.mostrarPopupProximamente();
                  }
                },
                error: () => {
                  this.dataLoginStore.updateProp('isProcessValid', false);
                  this.router.navigate(['home/inicio']);
                  this.dialogService.mostrarPopupProximamente();
                }
              });
            },
            error: (error) => {
              this.handleAuthError(error)
            }
          });
      }

    } else {
      sessionStorage.clear();
      this.sesionIdService.generarIdSesion();
    }
  }

  private handleAuthError(error: any): void {
    const { message } = error?.error || {};
    this.errorMessage = message || 'Error desconocido al iniciar sesión';
    this.dialogService.mostrarMensajeError(`${this.errorMessage}`);
  }

  private actualizarClave(token: any) {
    const isMobile = window.innerWidth <= 600;
    this.dialogService.openComponent(ActualizarContrasenaComponent,
      {
        width: isMobile ? '90vw' : '320px',
        minWidth: 'auto',
        maxWidth: isMobile ? 'min(96vw, 400px)' : '320px'
      }
    ).subscribe((result) => {
      const { clave, clave2 } = result;
      const encryptedClave = this.encryptionService.encryptPassword(clave);
      const encryptedClave2 = this.encryptionService.encryptPassword(clave2);

      this.authService.passwordUpdate(encryptedClave, encryptedClave2, token)
        .pipe(
          finalize(() => {
            this.formGroupParent.get('clave')?.setValue('');
          })
        ).subscribe({
          next: (resultado: PasswordUpdateResponse) => {
            if (resultado.resultado == 1) {
              this.dialogService.mostrarMensajeExito(`${resultado.mensaje}`);
            }
          },
          error: (error) => {
            let mensajeError = 'Error al actualizar la contraseña: ';
            this.dialogService.mostrarMensajeError(`${mensajeError}`);
            console.error(
              'Error al actualizar la contraseña: ',
              error
            );
          },
          complete: () => {
          },
        });

    });
  }

  private loadImageRandom(): void {
    const images = [
      'assets/img/fondo01.svg',
      'assets/img/fondo02.svg',
      'assets/img/fondo03.svg',
      'assets/img/fondo04.svg',
      'assets/img/fondo05.svg'
    ];
    const randomValues = new Uint32Array(1);
    crypto.getRandomValues(randomValues);
    const index = randomValues[0] % images.length;
    this.imgSrc = images[index];
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  bloquearEvent(event: any) {
    event.preventDefault();
  }

  openDialogRestorePassword(): void {
    const isMobile = window.innerWidth <= 600;
    this.dialogService.openComponent(
      RecuperarContrasenaComponent,
      {
        width: isMobile ? '90vw' : '320px',
        minWidth: 'auto',
        maxWidth: isMobile ? 'min(96vw, 400px)' : '320px'
      }
    );
  }


  disableInputSubmit(): boolean {
    const usuarioControl = this.formGroupParent.get('usuario');
    const claveControl = this.formGroupParent.get('clave');

    // El botón se deshabilita si:
    // 1. El formulario es inválido
    // 2. El usuario está vacío
    // 3. La contraseña está vacía
    // 4. La contraseña tiene menos de 8 caracteres
    return this.formGroupParent.invalid ||
      !usuarioControl?.value?.trim() ||
      !claveControl?.value?.trim() ||
      claveControl?.value?.length < 8;
  }
}
