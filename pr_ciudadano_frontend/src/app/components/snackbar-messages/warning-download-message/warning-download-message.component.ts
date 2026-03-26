import { Component, Inject } from "@angular/core";
import { MatSnackBarRef, MAT_SNACK_BAR_DATA } from "@angular/material/snack-bar";
import { SnackbarData } from "../../../interfaces/snackbar.interfaces";

@Component({
  selector: "app-warning-download-message",
  templateUrl: "./warning-download-message.component.html",
  styleUrls: ["./warning-download-message.component.scss"],
  standalone: false,
})
export class WarningDownloadMessageComponent {
  constructor(
    public snackBarRef: MatSnackBarRef<WarningDownloadMessageComponent>,
    @Inject(MAT_SNACK_BAR_DATA) public data: SnackbarData
  ) {}
}
