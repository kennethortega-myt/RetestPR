import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { filter, from, Subject, switchMap, take, takeUntil } from 'rxjs';

import { AuthService } from '../../../services/auth-service.service';
import { RecaptchaService } from '../../../services/recaptcha.service';
import { Restore } from '../../../interfaces/login';
import { DialogService } from '../../../services/dialog.service';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { DialogRef } from '@angular/cdk/dialog';
import { IdSessionService } from '../../../services/session-id.service';
import { TranslateModule } from '@ngx-translate/core';
import { FIELD_LIMITS } from '../../../helpers/constantes';

@Component({
  selector: 'app-recuperar-contrasena',
  templateUrl: './recuperar-contrasena.component.html',
  styleUrls: ['./recuperar-contrasena.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, MatInputModule, CommonModule, TranslateModule],
})
export class RecuperarContrasenaComponent implements OnInit, OnDestroy {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly dialogService = inject(DialogService);
  private readonly destroy$ = new Subject<void>();
  public form!: FormGroup;
  readonly FIELD_LIMITS = FIELD_LIMITS;

  constructor(
      private readonly recaptchaService: RecaptchaService,
      private readonly dialogRef: DialogRef<RecuperarContrasenaComponent>,
      private readonly sesionIdService: IdSessionService) {
      this.form = new FormGroup({});
  }

  ngOnInit() {
    this.sesionIdService.generarIdSesion(); 
    this.buildForm();
  }

  private buildForm(): void {
    this.form = this.fb.group({
      usuario: this.fb.control<string>('', [
        Validators.required,
        Validators.minLength(FIELD_LIMITS.USUARIO_MIN_LENGTH),
        Validators.maxLength(FIELD_LIMITS.USUARIO_MAX_LENGTH),
        Validators.pattern(/^(?!.*[-_]{2})(?![-_])[A-Za-z0-9]+(?:[-_][A-Za-z0-9]+)*(?<![-_])$/)
      ]),
    });

    this.form.get('usuario')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(value => {
        const cleaned = value?.replaceAll(/[^a-zA-Z0-9_-]/g, '').toUpperCase() ?? '';
        if (value !== cleaned) {
          this.form.get('usuario')?.setValue(cleaned, { emitEvent: false });
        }
      });
  }

  get usuario() {
    return this.form.get('usuario');
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  public notifyRecoveryPassword(): void {
    const usuarioValue = this.usuario?.value;

    if (this.form.invalid || !usuarioValue) {
      this.usuario?.markAsTouched();
      return;
    }

    this.dialogService.mostrarMensajeConfirmacion('¿Está seguro de realizar la operación?')
      .pipe(
        take(1),
        filter(Boolean),
        switchMap(() =>
          from(this.recaptchaService.execute('importantAction'))
        ),
        switchMap((token) => {
          const restore: Restore = {
            usuario: usuarioValue,
            recaptcha: token
          };

          return this.authService.passwordRestore(restore).pipe(take(1));
        })
      )
      .subscribe({
        next: (response) => {
          if (response.resultado !== 1) {
            this.dialogService.mostrarMensajeError(response.mensaje);
            console.warn('⚠️ Falló la recuperación:', response.message);
            return;
          }

          this.dialogService.mostrarMensajeExitoConCallback(
            response.mensaje,
            () => this.dialogRef.close()
          );
        },
        error: (error) => {
          console.error('❌ Error en recuperación de contraseña', error);
          this.dialogRef.close();
        }
      });
  }


  volver() {
    this.dialogRef.close();
  }

  bloquearEvent(event: any) {
    event.preventDefault();
  }
}
