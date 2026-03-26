import { Component, Inject } from "@angular/core";
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from "@angular/material/snack-bar";
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: "app-download-message",
  templateUrl: "./download-message.component.html",
  styleUrls: ["./download-message.component.scss"],
  standalone: false,
})
export class DownloadMessageComponent {

  public ReporteKey = 'download-message.reporte';

  constructor(
    public snackBarRef: MatSnackBarRef<DownloadMessageComponent>,
    @Inject(MAT_SNACK_BAR_DATA) public data: any
  ) {}
}
