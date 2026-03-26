import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogClose } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { TipoEscrutinioModel, TablaEscrutinioModeloUno } from '../tabla-escrutinio-modelo-uno/tabla-escrutinio-modelo-uno.model';
import { CommonService } from '../../services/common/common.service';
import { CommonModule } from '@angular/common';
import { CandidatoModeloDos, TablaEscrutinioModeloDos } from '../tabla-escrutinio-modelo-dos/tabla-escrutinio-modelo-dos.model';
import { PorcentajeFormatPipe } from '../../pipes/porcentaje-format.pipe';

@Component({
  selector: 'app-modal-detail-votes',
  imports: [TranslateModule, MatDialogClose, CommonModule, PorcentajeFormatPipe],
  templateUrl: './modal-detail-votes.component.html',
  styleUrl: './modal-detail-votes.component.scss',
})
export class ModalDetailVotesComponent implements OnInit {
  private readonly commonService = inject(CommonService);
  readonly dialogData = inject<TipoEscrutinioModel>(MAT_DIALOG_DATA);
  
  nombreCompleto = 'Sin candidato';
  imagenCandidato = '';
  imagenOrganizacion = '';
  tipoModal = true;
  mostrarLista = false;
  listaCandidatos: CandidatoModeloDos[] = [];
  showCandidates = false;

  ngOnInit(): void {
    this.cargarValores();
  }

  toggleShowCandidates(): void {
    this.showCandidates = !this.showCandidates;
  }

  private cargarValores(): void {
    if (!this.dialogData?.tablaModel) {
      return;
    }
    const datosModal = this.dialogData.tablaModel;
    this.mostrarLista = false;
    if (this.dialogData.tipoModel === null) {
      this.tipoModal = false;
      this.nombreCompleto = datosModal.nombrePartidoPolitico || 'Sin organización';
    }
    else{     
      this.tipoModal = true;
      if(this.dialogData.tipoModel) {
        this.cargarDatosCandidato(datosModal as TablaEscrutinioModeloUno);
      }
      else{
        this.cargarDatosList(datosModal as TablaEscrutinioModeloDos);
      }
      this.imagenOrganizacion = datosModal.ccodigo
        ? this.commonService.getImagePoliticalOrganization(datosModal.ccodigo)
        : '';
    }
  }

  private cargarDatosList(datosModal: TablaEscrutinioModeloDos): void {
    this.nombreCompleto = "Total de candidatos: " + datosModal.totalCandidatos; 
    this.listaCandidatos = datosModal?.candidatos ?? [];
    this.mostrarLista = true;
  }

  private cargarDatosCandidato(datosModal: TablaEscrutinioModeloUno): void {
    const candidato = datosModal.candidato?.[0];
    
    if (candidato) {
      this.nombreCompleto = [
        candidato.apellidoPaterno,
        candidato.apellidoMaterno,
        candidato.nombres
      ].filter(Boolean).join(' ') || 'Sin candidato';
      
      this.imagenCandidato = candidato.cdocumentoIdentidad 
        ? this.commonService.getImageCandidate(candidato.cdocumentoIdentidad)
        : '';
    }
  }

  tieneCandidato(): boolean {
    const tabla = this.dialogData.tablaModel;

    if ('candidato' in tabla) {
      return (tabla.candidato?.length ?? 0) > 0;
    }

    if ('candidatos' in tabla) {
      return (tabla.candidatos?.length ?? 0) > 0;
    }

    return false;
  }
}