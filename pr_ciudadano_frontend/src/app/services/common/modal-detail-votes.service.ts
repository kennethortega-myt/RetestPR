import { Injectable, signal } from '@angular/core';
import { ModalDetailVotes } from '../../interfaces/modal-detail-votes.interface';
import { MODAL_DETAIL_VOTES_DEFAULT } from '../../constants/modal-detail-votes.constants';

@Injectable({
  providedIn: 'root'
})
export class ModalDetailVotesService {
  private data = signal<ModalDetailVotes>({ ...MODAL_DETAIL_VOTES_DEFAULT });

  setData(data: Partial<ModalDetailVotes>): void {
    this.data.set({...this.data(), ...data});
  }

  getData(): ModalDetailVotes {
    return this.data();
  }

  resetData(): void {
    this.data.set({ ...MODAL_DETAIL_VOTES_DEFAULT });
  }
}
