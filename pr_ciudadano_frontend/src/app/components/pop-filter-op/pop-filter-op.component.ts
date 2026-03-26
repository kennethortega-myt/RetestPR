import { Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MAT_BOTTOM_SHEET_DATA, MatBottomSheet } from '@angular/material/bottom-sheet';
import { PoliticalOrganizationItem } from '../../interfaces/presidenciales.interfaces';

@Component({
  selector: 'app-pop-filter-op',
  templateUrl: './pop-filter-op.component.html',
  styleUrl: './pop-filter-op.component.scss',
  standalone: false
})
export class PopFilterOpComponent {
  tituloKey = 'pop-filter.titulo';
  descripcionKey = 'pop-filter.descripcion';
  aceptarDescKey = 'pop-filter.aceptarDesc';
  botonCancelarKey = 'pop-filter.cancelar';
  botonFiltrarKey = 'popup-filtro.filtrar';
  myForm: FormGroup;
  candidate: PoliticalOrganizationItem | null = null;

  constructor(
    @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
    private readonly _bottomSheet: MatBottomSheet
  ) {}

  closeFilterOp(event?): void {
    event?.preventDefault(); // Prevents any default action triggered by the event
    this._bottomSheet.dismiss();
  }

  aceptarFiltro(event?): void {
    event?.preventDefault();

    this._bottomSheet.dismiss(this.candidate);
  }

  selectedData(candidate: PoliticalOrganizationItem): void {
    this.candidate = { ...candidate };
  }
}
