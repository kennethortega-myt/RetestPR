import { Injectable } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";
import { DownloadMessageComponent } from "../../components/snackbar-messages/download-message/download-message.component";
import { WarningDownloadMessageComponent } from "../../components/snackbar-messages/warning-download-message/warning-download-message.component";
import { SnackbarMessageType, SnackbarData, SNACKBAR_MESSAGES } from "../../interfaces/snackbar.interfaces";

@Injectable({
  providedIn: "root",
})
export class SnackbarService {
  private readonly durationInSeconds = 5;

  constructor(private readonly snackBar: MatSnackBar) {}

  public showSnackbarWithSuccessMessage(mensaje: string = "", color: string = "") {
    this.snackBar.openFromComponent(DownloadMessageComponent, {
      data: { mensaje: mensaje, color: color },
      duration: this.durationInSeconds * 1000,
      horizontalPosition: "right",
      verticalPosition: "top",
      panelClass: color == "ambar" ? "ambar" : "snackbar-success-message",
    });
  }

  public showSnackbarWithWarningMessage(messageType: SnackbarMessageType = "default") {
    this.snackBar.openFromComponent<WarningDownloadMessageComponent, SnackbarData>(WarningDownloadMessageComponent, {
      duration: this.durationInSeconds * 1000,
      horizontalPosition: "right",
      verticalPosition: "top",
      panelClass: "snackbar-warning-message",
      data: { message: SNACKBAR_MESSAGES[messageType] },
    });
  }
}
