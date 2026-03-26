import { Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { Subject } from 'rxjs';
import { TAMANIO_PAGINA } from '../../../../helpers/constantes';

@Injectable()
export class MyCustomPaginatorIntl implements MatPaginatorIntl {
  changes = new Subject<void>();

  firstPageLabel = `Primera página: `;
  itemsPerPageLabel = `Registros por página: `;
  lastPageLabel = `Ultima página: `;

  nextPageLabel = 'Página siguiente';
  previousPageLabel = 'Página anterior';

  getRangeLabel(page: number, pageSize: number, length: number): string {
    if (length === 0) {
      return `1 de 1`;
    }
    const amountPages = Math.ceil(length / TAMANIO_PAGINA);
    return `${page + 1} de ${amountPages}`;
  }
}
