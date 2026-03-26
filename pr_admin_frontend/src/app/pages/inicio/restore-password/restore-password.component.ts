import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { ComponentsModule } from '../../../components/components.module';
import { IdSessionService } from '../../../services/session-id.service';

@Component({
  selector: 'app-restore-password',
  templateUrl: './restore-password.component.html',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatIconModule, CommonModule, ComponentsModule],
})
export class RestorePasswordComponent implements OnInit {
  formGroup!: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<RestorePasswordComponent>,
    private formBuilder: FormBuilder,
    private readonly sesionIdService: IdSessionService) { }

  ngOnInit(): void {
    this.formGroup = this.formBuilder.group({
      usuario: new FormControl('', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50),
        Validators.pattern(/^(?!.*[-_]{2})(?![-_])[A-Za-z0-9]+(?:[-_][A-Za-z0-9]+)*(?<![-_])$/)
      ])
    });

    this.sesionIdService.generarIdSesion();
    this.setupUsuarioValueChanges();
  }

  private setupUsuarioValueChanges(): void {
    this.formGroup.get('usuario')?.valueChanges.subscribe(value => {
      let cleaned = value?.replace(/[^a-zA-Z0-9_-]/g, '').toUpperCase() ?? '';
      if (cleaned.length > 50) {
        cleaned = cleaned.substring(0, 51);
      }
      if (value !== cleaned) {
        this.formGroup.get('usuario')?.setValue(cleaned, { emitEvent: false });
        this.formGroup.get('usuario')?.updateValueAndValidity();
      }
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  enviar(): void {
    if (!this.formGroup.valid) {
      this.formGroup.markAllAsTouched();
      return;
    }

    this.dialogRef.close(this.formGroup.get('usuario')?.value);
  }

  bloquearEvent(event: Event): void {
    event.preventDefault();
  }

  checkButton(): boolean {
    return this.formGroup.valid;
  }
}
