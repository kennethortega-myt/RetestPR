import { Component, inject } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { DataLoginStore } from '../../states/data-login.store';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-popup-proximanente',
  imports: [TranslateModule],
  templateUrl: './popup-proximanente.component.html'
})
export class PopupProximanenteComponent {
  private readonly router = inject(Router);
  private readonly dataLoginStore = inject(DataLoginStore);
  private readonly dialogRef = inject(MatDialogRef<PopupProximanenteComponent>);

  entendido(): void {
    this.dataLoginStore.clearDataLogin();
    this.dialogRef.close();
    this.router.navigate(['/']);
  }
}
