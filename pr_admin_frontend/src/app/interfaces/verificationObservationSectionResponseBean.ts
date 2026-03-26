import { VerificationObservationBean } from './verificationObservationBean';

export interface VerificationObservationSectionResponseBean {
  token: string;
  count: VerificationObservationBean;
  install: VerificationObservationBean;
  vote: VerificationObservationBean;
}
