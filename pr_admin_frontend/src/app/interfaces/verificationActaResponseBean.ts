import { VerificationSignSectionResponseBean } from './verificationSignSectionResponseBean';
import { VerificationVoteSectionResponseBean } from './verificationVoteSectionResponseBean';
import { VerificationObservationSectionResponseBean } from './verificationObservationSectionResponseBean';
import { VerificationDatetimeSectionResponseBean } from './verificationDatetimeSectionResponseBean';

export interface VerificationActaResponseBean {
  token?: string;
  estadoActa: string;
  signSection: VerificationSignSectionResponseBean;
  voteSection?: VerificationVoteSectionResponseBean;
  observationSection?: VerificationObservationSectionResponseBean;
  dateSectionResponse?: VerificationDatetimeSectionResponseBean;
}
