import { MatPaginatorIntl } from '@angular/material/paginator';

// Returns a Material paginator configured with Spanish labels and page indicator
export function PersonalizacionPaginatorMaterial(): MatPaginatorIntl {
  const customPaginatorIntl = new MatPaginatorIntl();
  customPaginatorIntl.itemsPerPageLabel = 'Registros por página';
  customPaginatorIntl.getRangeLabel = (
    page: number,
    pageSize: number,
    length: number
  ) => {
    if (length === 0 || pageSize === 0) {
      return `0 de ${length}`;
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;

    const endIndex = (page + 1) * pageSize;
    const totalPagina = Math.ceil(length / pageSize);

    return `${page + 1} de ${Number(totalPagina).toLocaleString('en-GB')}`;
  };
  return customPaginatorIntl;
}
