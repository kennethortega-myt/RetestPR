import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CommonService {
  private _isImageRealDirectory = false;

  updateIsImageRealDirectory(isEnabled: boolean): void {
    this._isImageRealDirectory = isEnabled;
  }

  getImageCandidate(dni: string): string {
    const _directory = this._isImageRealDirectory ? '_candidatos' : 'candidatos';
    return `assets/${_directory}/${dni}.jpg`;
  }

  getImagePoliticalOrganization(codePoliticalGroup: string): string {
    const _code = codePoliticalGroup?.toString().padStart(8, '0');
    const _directory = this._isImageRealDirectory ? '_partidos' : 'partidos';
    return `assets/${_directory}/${_code}.jpg`;
  }

  getImageRevocationAuthorities(dni: string): string {
    const _directory = this._isImageRealDirectory ? '_revocation-authorities' : 'revocation-authorities';
    return `assets/${_directory}/${dni}.jpg`;
  }
}
