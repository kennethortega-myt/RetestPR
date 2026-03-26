import { ModalDetailVotes } from '../interfaces/modal-detail-votes.interface';

export const MODAL_DETAIL_VOTES_DEFAULT: ModalDetailVotes = {
  // Partido Politico
  politicalPartyShow: true,
  politicalPartyImage: '',
  politicalPartyImageShow: true,
  politicalPartyName: '',
  // Candidato
  candidateImage: '',
  candidateImageShow: true,
  candidateName: '',
  candidateNameShow: true,
  // Votos
  votesNumber: 0,
  // Votos Emitidos
  votesEmittedPercentage: 0,
  votesEmittedShow: true,
  // Votos Validos
  votesValidPercentage: 0,
  votesValidShow: true,
  // Votos Nulos

  // Candidatos
  candidatesShow: false,
  candidates: []
};
