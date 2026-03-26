import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { LoadingService } from './loading.service';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.scss',
})
export class LoadingComponent implements OnInit, OnDestroy {
  private readonly unsubscribe$: Subject<boolean> = new Subject();
  showLoading?: boolean;

  constructor(private readonly loadingService: LoadingService) {}

  ngOnInit(): void {
    this.loadingService
      .getAccionLoading()
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((value) => {
        this.showLoading = value;
      });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next(true);
    this.unsubscribe$.complete();
  }
}
