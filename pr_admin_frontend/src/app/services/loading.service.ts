import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";

@Injectable({ providedIn: 'root' })
export class LoadingService {

    private readonly loaderSource = new BehaviorSubject<{active: boolean, message?: string}>({active: false});
    loader$ = this.loaderSource.asObservable();
  
    private activeRequests = 0;
  
    startRequest() {
      this.activeRequests++;    
      if (this.activeRequests === 1) {
        this.loaderSource.next({ active: true });
      }
    }
  
    endRequest() {
      this.activeRequests = Math.max(0, this.activeRequests - 1);    
      if (this.activeRequests === 0) {
        setTimeout(() => {
          this.loaderSource.next({ active: false });
        }, 300);
      }
    }
  
    startSyncRequest() {
      this.activeRequests++;
      this.loaderSource.next({ active: true });
    }
  
    endSyncRequest() {
      this.activeRequests = Math.max(0, this.activeRequests - 1);
  
      if (this.activeRequests === 0) {
        this.loaderSource.next({ active: false });
      }
    }
  
    limpiarTemporal() {
      this.loaderSource.complete();
    }
  }
  