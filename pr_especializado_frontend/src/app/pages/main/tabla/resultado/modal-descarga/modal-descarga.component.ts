import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {
  IModalDescargaData,
  MODAL_MESSGAGE,
  IDownloadStatusType,
} from './modal-message';
import { TranslateModule } from '@ngx-translate/core';


@Component({
  selector: 'app-modal-descarga',
  templateUrl: './modal-descarga.component.html',
  imports: [TranslateModule],
})
export class ModalDescargaComponent implements OnInit {
  public defaultMessage = MODAL_MESSGAGE.init_download;
  public htmlMessage = '';
  public showAdvertencia = false;

  constructor(
    public dialogRef: MatDialogRef<ModalDescargaComponent>,
    @Inject(MAT_DIALOG_DATA) private readonly data: IModalDescargaData
  ) {}

  ngOnInit(): void {
    this.htmlMessage = this.data?.message ?? this.defaultMessage;

    const downloadStatus: IDownloadStatusType = this.getDownloadStatus();
    this.showAdvertencia = downloadStatus === 'init_download';
  }

  getDownloadStatus(): IDownloadStatusType {
    // Aquí puedes obtener el estado real desde los datos
    return this.data?.message === MODAL_MESSGAGE.in_progress
      ? 'in_progress'
      : 'init_download';
  }

  closeDialog() {
    this.dialogRef.close();
  }
}
