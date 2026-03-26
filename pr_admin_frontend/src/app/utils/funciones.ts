import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { DialogComponent } from "../components/dialog/dialog/dialog.component";

const DIALOG_CONFIG = {
  width: '20%',
  minWidth: '320px',
  maxWidth: '100%'
};

export function openDialogMensaje(
  dialog: MatDialog, 
  mensajeError: string, 
  tipo: number = 0
): MatDialogRef<DialogComponent> {
  
  return dialog.open(DialogComponent, {
    ...DIALOG_CONFIG,
    data: { mensaje: mensajeError, tipo },
  });
  
}