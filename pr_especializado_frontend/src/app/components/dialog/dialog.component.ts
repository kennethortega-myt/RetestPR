import { Component, Inject, OnInit, TemplateRef, AfterViewInit, AfterContentInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { IDialogConfigData, DIALOG_CONFIGS, DialogKey, TYPE_ICON_MAP } from './dialog.constants';
import { CommonModule, NgIf } from '@angular/common';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss'],
  imports: [MatDialogModule, NgIf, CommonModule],
})
export class DialogComponent implements OnInit, AfterViewInit, AfterContentInit {
  public dialogConfig: IDialogConfigData;
  public contentTemplate?: TemplateRef<any>;

  hasContent = false;

  constructor(
    public dialogRef: MatDialogRef<DialogComponent>,
    @Inject(MAT_DIALOG_DATA) public initialData: IDialogConfigData,
  ) {
    this.dialogConfig = { ...initialData };
    this.contentTemplate = this.dialogConfig.contentTemplate;
  }

  ngOnInit() {
    let predefinedProps: IDialogConfigData | undefined;


    if (this.dialogConfig.key && this.dialogConfig.key in DIALOG_CONFIGS) {
      predefinedProps = DIALOG_CONFIGS[this.dialogConfig.key as DialogKey];
    }

    if (predefinedProps) {
      this.dialogConfig = { ...predefinedProps, ...this.initialData };
    }

    this.dialogConfig.key = this.dialogConfig.key ?? 'info';
    this.dialogConfig.type = this.dialogConfig.type ?? 3;
    this.dialogConfig.title = this.dialogConfig.title ?? 'Mensaje';
    this.dialogConfig.confirm = this.dialogConfig.confirm ?? false;

    if (!this.dialogConfig.iconPath) {
      this.dialogConfig.iconPath = TYPE_ICON_MAP[this.dialogConfig.type];
    }
  }

  ngAfterViewInit() {
    // Esperar un ciclo de renderizado para que los botones estén montados
    setTimeout(() => {
      const el = document.activeElement;
      if (el instanceof HTMLElement) {
        el.blur();
      }
    });
  }

  ngAfterContentInit() {
    this.hasContent = !!this.contentTemplate;
  }

  cerrar(): void {
    this.dialogRef.close(false);
  }

  confirmar(): void {
    // Ejecutar callback si existe
    if (this.dialogConfig.onConfirm) {
      this.dialogConfig.onConfirm();
    }
    this.dialogRef.close(true);
  }
}
