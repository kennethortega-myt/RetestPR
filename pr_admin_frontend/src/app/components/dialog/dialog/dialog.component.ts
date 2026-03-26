import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { IDialogComponentData } from './dialog.constants';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  standalone: false,
})
export class DialogComponent {
  constructor(
    public dialog: MatDialogRef<DialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDialogComponentData
  ) {
    // Aceptar ambos shapes: { mensaje, tipo } (legacy español) o { message, type } (IDialogConfigData)
    this.data = this.data || {};

    // Normalizar texto: preferir `mensaje`, luego `message`, luego `title`.
    this.data.mensaje = this.data.mensaje ?? this.data.message ?? this.data.title ?? '';

    // Normalizar tipo: preferir `tipo`, luego `type`, fallback a 0
    this.data.tipo = this.data.tipo ?? this.data.type ?? 0;
  }

  ngOnInit(): void {
    // Nada extra necesario: la normalización ya se hizo en el constructor.
  }

  cerrarDialogo(result?: boolean): void {
    try {
      // Si viene un callback onConfirm y el usuario confirma, ejecutarlo
      if (result && typeof this.data?.onConfirm === 'function') {
        try {
          this.data.onConfirm();
        } catch (err) {
          console.error('Error ejecutando onConfirm del diálogo:', err);
        }
      }
    } finally {
      // Pasamos el resultado al cerrar para que afterClosed() emita true/false
      this.dialog.close(result);
    }
  }
}
