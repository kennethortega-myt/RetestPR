import { Injectable, signal } from '@angular/core';
import { GeographicalLocationNameItem } from '../../interfaces/presidenciales.interfaces';
import { PRESIDENCIALES_DEFUALT_CONSTANTS } from '../../constants/presidenciales.constanst';

@Injectable({
  providedIn: 'root'
})
export class PresidencialesStoreService {
  private candidate = signal<GeographicalLocationNameItem>({ ...PRESIDENCIALES_DEFUALT_CONSTANTS });

  setCandidate(candidate: GeographicalLocationNameItem): void {
    this.candidate.set(candidate);
  }

  getCandidate(): GeographicalLocationNameItem {
    return this.candidate();
  }
}
