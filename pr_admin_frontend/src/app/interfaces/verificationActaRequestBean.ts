import { VerificationSaveSignRequestBean } from './verificationSaveSignRequestBean';
import { VerificationSaveVoteRequestBean } from './verificationSaveVoteRequestBean';
import { VerificationSaveDatetimeBean } from './verificationSaveDatetimeBean';
import { VerificationSaveObservationRequestBean } from './verificationSaveObservationRequestBean';

export interface VerificationActaRequestBean {
  token: string;
  signSection: VerificationSaveSignRequestBean;
  voteSection: VerificationSaveVoteRequestBean;
  observationSection: VerificationSaveObservationRequestBean;
  dateSectionResponse: VerificationSaveDatetimeBean;
}
