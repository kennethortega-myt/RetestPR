import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import {
  ILoginConfigData,
  ILoginModalData,
  LOGIN_MODAL_CONFIGS,
} from './alerts.constants';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  standalone: false,
})
export class AlertsComponent implements OnInit {
  public config: ILoginConfigData = {} as ILoginConfigData;

  constructor(@Inject(MAT_DIALOG_DATA) public data: ILoginModalData) {}

  ngOnInit(): void {
    this.config = LOGIN_MODAL_CONFIGS[this.data.type];
  }
}
