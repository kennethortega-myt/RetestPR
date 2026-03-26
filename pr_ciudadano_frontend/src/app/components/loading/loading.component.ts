import { ChangeDetectorRef, Component, DestroyRef, inject } from '@angular/core';
import { RouteConfigLoadEnd, RouteConfigLoadStart, Router } from '@angular/router';
import { map } from 'rxjs';
import { LoaderService, LoaderState } from '../../services/elecciones-generales/loading.service';

@Component({
  selector: 'onpe-loading',
  standalone: false,
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.scss'
})
export class LoadingComponent {
  private readonly loaderService: LoaderService = inject(LoaderService);
  private readonly router: Router = inject(Router);
  private readonly cd: ChangeDetectorRef = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);
  show: boolean = false;

  ngOnInit(): void {
    this.listenLazyLoading();
    this.loaderService.loaderState
      .pipe(
        map((state: LoaderState): boolean => state.show),
        // distinctUntilChanged(),
        // takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((show: boolean) => {
        this.show = show;
        // this.cd.detectChanges();
      });
  }

  private listenLazyLoading(): void {
    this.router.events.pipe().subscribe({
      next: (event) => {
        if (event instanceof RouteConfigLoadStart) {
          this.loaderService.show();
          this.cd.detectChanges();
        } else if (event instanceof RouteConfigLoadEnd) {
          this.loaderService.hide();
          this.cd.detectChanges();
        }
      },
      error: () => this.loaderService.hide()
    });
  }
}
