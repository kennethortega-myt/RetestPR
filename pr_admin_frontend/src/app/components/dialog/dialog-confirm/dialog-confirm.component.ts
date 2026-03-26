import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-confirm',
  templateUrl: './dialog-confirm.component.html',
  standalone: false,
})
export class DialogConfirmComponent {
  constructor(
    public dialog: MatDialogRef<DialogConfirmComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  cerrarDialogo(): void {
    this.dialog.close(false);
  }
  confirmado(): void {
    this.dialog.close(true);
  }
}
