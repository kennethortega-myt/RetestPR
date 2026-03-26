import { Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';

@Injectable()
export class CustomPaginatorIntl extends MatPaginatorIntl {
  override changes = new Subject<void>();

  constructor(private translate: TranslateService) {
    super();
    
    // Suscribirse a cambios de idioma
    this.translate.onLangChange.subscribe(() => {
      this.translateLabels();
    });
    
    // Traducir inicialmente
    this.translateLabels();
  }

  private translateLabels(): void {
    this.translate.get('paginator').subscribe((translations: any) => {
      this.itemsPerPageLabel = translations.itemsPerPage;
      this.nextPageLabel = translations.nextPage;
      this.previousPageLabel = translations.previousPage;
      this.firstPageLabel = translations.firstPage;
      this.lastPageLabel = translations.lastPage;
      
      this.changes.next();
    });
  }

  override getRangeLabel = (page: number, pageSize: number, length: number): string => {
    if (length === 0 || pageSize === 0) {
      return this.translate.instant('paginator.page') + ' 0 ' + 
             this.translate.instant('paginator.of') + ' 0';
    }
    
    length = Math.max(length, 0);
    const totalPages = Math.ceil(length / pageSize);
    
    return this.translate.instant('paginator.page') + ' ' + 
           (page + 1) + ' ' + 
           this.translate.instant('paginator.of') + ' ' + 
           totalPages;
  };
}
