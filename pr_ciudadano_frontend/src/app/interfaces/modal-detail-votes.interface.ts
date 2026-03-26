export interface ModalDetailVotes {
  // Partido Politico
  politicalPartyShow?: boolean;
  politicalPartyImage: string;
  politicalPartyImageShow: boolean;
  politicalPartyName: string;
  // Candidato
  candidateImage: string;
  candidateImageShow: boolean;
  candidateName: string;
  candidateNameShow: boolean;
  // Votos
  votesNumber: number;
  // Votos Emitidos
  votesEmittedPercentage: number;
  votesEmittedShow: boolean;
  // Votos Validos
  votesValidPercentage: number;
  votesValidShow: boolean;
  // Candidatos
  candidatesShow?: boolean;
  candidates?: ModalDetailVotesCandidate[];
}

export interface ModalDetailVotesCandidate {
  list: number;
  fullName: string;
  votes: number;
}
