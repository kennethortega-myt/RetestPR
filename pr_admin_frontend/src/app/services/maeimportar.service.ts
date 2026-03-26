import { Injectable } from '@angular/core';
import { MaeimportarApiService } from './maeimportar-api.service';

@Injectable({
  providedIn: 'root',
})
export class MaeimportarService {
  
  constructor(
    private readonly maeImportarApiService: MaeimportarApiService
  ) {}

  obtener() {
    return this.maeImportarApiService.listar();
  }
}
