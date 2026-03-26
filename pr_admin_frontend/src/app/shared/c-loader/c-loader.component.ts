import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../services/loading.service';

@Component({
  selector: 'app-c-loader',
  templateUrl: './c-loader.component.html',
  imports: [CommonModule]
})
export class CLoaderComponent {
  
  loader$: Observable<{ active: boolean, message?: string }>;

  constructor(private readonly loaderService: LoadingService) {
    this.loader$ = this.loaderService.loader$;
  }

  ngOnDestroy(): void {
    this.loaderService.limpiarTemporal();
  }
}