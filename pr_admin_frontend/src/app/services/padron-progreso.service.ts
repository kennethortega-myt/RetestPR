import { Injectable } from '@angular/core';

import { PadronProgresoApiService } from './padron-progreso-api.service';

@Injectable({
  providedIn: 'root',
})
export class PadronProgresoService {
  constructor(private readonly padronProgresoApiService: PadronProgresoApiService) {}

  finalizo() {
    return this.padronProgresoApiService.finalizo();
  }
}
