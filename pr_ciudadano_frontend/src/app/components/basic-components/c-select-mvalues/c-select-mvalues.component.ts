import { Component, EventEmitter, Input, OnDestroy, Output } from '@angular/core';
import { Subject } from 'rxjs';
import { GEOGRAPHIC_SCOPE } from '../../../helpers/constantes';
import { mapWithPoliticImage } from '../../../helpers/get-images.helper';
import { getFilterTypeForBackend } from '../../../helpers/ubigeo-level.common';
import {
  PoliticalOrganizationItem,
  PoliticalOrganizationParams,
  PoliticalOrganizationResponse
} from '../../../interfaces/presidenciales.interfaces';
import { PresidencialesService } from '../../../services/elecciones-generales/presidenciales.service';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'app-c-select-mvalues',
  templateUrl: './c-select-mvalues.component.html',
  styleUrl: './c-select-mvalues.component.scss',
  standalone: false
})
export class CSelectMvaluesComponent implements OnDestroy {
  @Input() placeholder: string = '';
  @Input() label: string = '';
  @Input() electionID: number;
  @Output() sendSelectedData = new EventEmitter<any>();
  private readonly destroy$ = new Subject<void>();
  policitalOrganizationItems: PoliticalOrganizationItem[] = [];

  constructor(private readonly presidencialesService: PresidencialesService) {}

  ngOnInit() {
    this.loadParticipantsByPoliticalOrganization(this.defaultParams);
  }

  selectedOp(event: MatSelectChange): void {
    this.sendSelectedData.emit(
      this.policitalOrganizationItems.find((x: PoliticalOrganizationItem) => x.codigoAgrupacionPolitica == event.value)
    );
  }

  private loadParticipantsByPoliticalOrganization(params: PoliticalOrganizationParams) {
    this.presidencialesService
      .getParticipantsByPoliticalOrganization$(params)
      .subscribe((response: PoliticalOrganizationResponse) => {
        if (response.success) {
          const _data = response.data.sort((a, b) =>
            a.nombreAgrupacionPolitica.localeCompare(b.nombreAgrupacionPolitica)
          );
          this.policitalOrganizationItems = mapWithPoliticImage(_data);
        } else {
          console.log(response);
        }
      });
  }

  private get defaultParams(): PoliticalOrganizationParams {
    return {
      idEleccion: this.electionID,
      idAmbitoGeografico: GEOGRAPHIC_SCOPE,
      tipoFiltro: getFilterTypeForBackend(),
      ubigeoNivel1: '',
      ubigeoNivel2: '',
      ubigeoNivel3: ''
    } as PoliticalOrganizationParams;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
