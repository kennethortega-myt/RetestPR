import { Component, OnDestroy, OnInit } from '@angular/core';
import { ModalDetailVotes } from '../../interfaces/modal-detail-votes.interface';
import { PorcentajeFormatPipe } from '../../pipes/porcentaje-format.pipe';
import { ModalDetailVotesService } from '../../services/common/modal-detail-votes.service';

@Component({
  selector: 'app-modal-detail-votes',
  templateUrl: './modal-detail-votes.component.html',
  standalone: false
})
export class ModalDetailVotesComponent implements OnInit, OnDestroy {
  data: ModalDetailVotes;
  votesValidPercentage: string;
  votesEmittedPercentage: string;
  showCandidates = false;
  private percentagePipe = new PorcentajeFormatPipe();

  constructor(private readonly modalDetailVotesService: ModalDetailVotesService) {
    this.data = this.modalDetailVotesService.getData();
  }

  ngOnInit(): void {
    this.setVotesEmmitedsAndValids();
  }

  toggleShowCandidates(): void {
    this.showCandidates = !this.showCandidates;
  }

  private setVotesEmmitedsAndValids(): void {
    const { votesValidPercentage, votesEmittedPercentage } = this.data;
    this.votesValidPercentage = this.parseTransformValue(votesValidPercentage);
    this.votesEmittedPercentage = this.parseTransformValue(votesEmittedPercentage);
  }

  private parseTransformValue(value: number | null | undefined): string {
    return value === undefined ? '-' : this.percentagePipe.transform(value || 0);
  }

  ngOnDestroy(): void {
    this.modalDetailVotesService.resetData();
  }
}
