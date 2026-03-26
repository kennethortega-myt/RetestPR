import { OverlayContainer } from '@angular/cdk/overlay';
import { DOCUMENT } from '@angular/common';
import {
  AfterViewInit,
  Component,
  computed,
  DestroyRef,
  effect,
  ElementRef,
  HostListener,
  inject,
  OnInit,
  Renderer2,
  Signal,
  TemplateRef,
  ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatIconRegistry } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarHorizontalPosition } from '@angular/material/snack-bar';
import { DomSanitizer } from '@angular/platform-browser';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import {
  ITEM_CURSOR_STEP_2,
  ITEM_READING_MASK_STEP_1,
  ITEM_READING_MASK_STEP_2,
  PROFILE_DEFAULT_EMPTY,
  PROFILE_TDAH
} from './components/accessibility/accessibility.constant';
import { AccessibilityService, KeyboardShortcutsService } from './components/accessibility/services';
import { ComponentsModule } from './components/components.module';
import { DisclaimerBrowserComponent } from './components/disclaimer-browser/disclaimer-browser.component';
import { MAT_ICON_REGISTRY } from './constants/mat-icons';
import { isBrowserChrome } from './helpers/funciones';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ComponentsModule],
  templateUrl: './app.component.html',
  host: { '[class]': 'rootClass()' }
})
export class AppComponent implements OnInit, AfterViewInit {
  private readonly _overlayContainer = inject(OverlayContainer);
  private readonly _renderer = inject(Renderer2);
  private readonly _document = inject(DOCUMENT);
  private readonly iconRegistry: MatIconRegistry = inject(MatIconRegistry);
  private readonly sanitizer: DomSanitizer = inject(DomSanitizer);
  private readonly router = inject(Router);
  private readonly accessibilityService = inject(AccessibilityService);
  private readonly keyboardShortcuts = inject(KeyboardShortcutsService);
  private readonly _snackBar = inject(MatSnackBar);
  private readonly destroyRef = inject(DestroyRef);
  @ViewChild('cursorContainer') cursorContainer!: ElementRef<HTMLElement>;
  @ViewChild('disclaimerTpl') disclaimerTpl!: TemplateRef<DisclaimerBrowserComponent>;
  title = 'modulo-especializado-v2';
  lastX: number = 0;
  lastY: number = 0;
  grandCursor: boolean = false;
  readingMaskActive: boolean = false;
  isLogin: boolean = false;

  rootClass: Signal<string> = computed(() => {
    return `${this.accessibilityService.contrastActive().id} ${this.accessibilityService.dislexiaFriendlyActive().id} ${this.accessibilityService.interlineadoActive().id}`;
  });

  constructor() {
    this.accessibilityService.loadDataFromLocalStorage();
    effect(() => {
      const textSizeActive = this.accessibilityService.textSizeActive();
      this.applyTextSize(textSizeActive.id);
    });

    effect(() => {
      const cursorActive = this.accessibilityService.cursorActive();
      const readingMaskActive = this.accessibilityService.readingMaskActive();
      this.grandCursor = cursorActive.id === ITEM_CURSOR_STEP_2.id;
      this.readingMaskActive = readingMaskActive.id === ITEM_READING_MASK_STEP_2.id;
      this.applyBigCursorMode();
      this.applyReadingMaskOverlay();
    });
    effect(() => {
      const isMobile = this.accessibilityService.isMobile();
      this.validBrowserRecomended(isMobile);
    });
  }

  ngOnInit(): void {
    this.registerKeyboardShortcuts();
    this.updateIsMobile();
    this.registerIcons();
    this.router.events.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.isLogin = event.urlAfterRedirects === '/';
      }
    });
  }

  @HostListener('window:mousemove', ['$event'])
  onMouseMove(event: MouseEvent): void {
    if (!this.readingMaskActive) {
      return;
    }
    this.lastX = event.clientX;
    this.lastY = event.clientY;
    this.updateCursorPosition(this.lastX, this.lastY);
  }

  @HostListener('window:resize')
  onResize(): void {
    this.updateIsMobile();
    const isMobile = window.innerWidth <= 768;
    if (isMobile) {
      this.accessibilityService.readingMaskActiveUpdate(ITEM_READING_MASK_STEP_1);
      if (this.accessibilityService.profileActive().id === PROFILE_TDAH.id) {
        this.accessibilityService.profileActiveUpdate(PROFILE_DEFAULT_EMPTY);
      }
    }
  }

  ngAfterViewInit(): void {
    this.validBrowserRecomended();
  }

  closeDisclaimer(): void {
    this._snackBar.dismiss();
  }

  openBottomSheet(): void {
    this.accessibilityService.show();
  }

  private validBrowserRecomended(isMobile: boolean = false): void {
    if (this.disclaimerTpl && !isBrowserChrome()) {
      const horizontalPosition: MatSnackBarHorizontalPosition = isMobile ? 'start' : 'center';

      this._snackBar.openFromTemplate(this.disclaimerTpl, {
        panelClass: 'disclaimer-snackbar-container',
        horizontalPosition,
        verticalPosition: 'bottom'
      });
    }
  }

  private updateIsMobile(): void {
    const isMobile = window.innerWidth <= 768;
    if (isMobile !== this.accessibilityService.isMobile()) {
      this.accessibilityService.isMobile.set(isMobile);
    }
  }

  private updateCursorPosition(x: number, y: number): void {
    const _maskActive = this.cursorContainer?.nativeElement;
    if (_maskActive) {
      _maskActive.style.position = 'fixed';
      _maskActive.style.left = `${x}px`;
      _maskActive.style.top = `${y}px`;
    }
  }

  private applyBigCursorMode(): void {
    if (this.grandCursor) {
      this._overlayContainer.getContainerElement().classList.add('big-cursor');
    } else {
      this._overlayContainer.getContainerElement().classList.remove('big-cursor');
    }
  }

  private applyReadingMaskOverlay(): void {
    const overlayEl = this._overlayContainer.getContainerElement();
    if (this.readingMaskActive) {
      overlayEl.classList.add('overlay-reading-mask');
    } else {
      overlayEl.classList.remove('overlay-reading-mask');
    }
  }

  private applyTextSize(value: string): void {
    this._renderer.setStyle(this._document.documentElement, 'font-size', `${value}%`);
  }

  private registerKeyboardShortcuts(): void {
    // Alt + Shift + A: Abrir accessibility (PC) / Cmd + Shift + A (Mac) - SEGURO
    this.keyboardShortcuts.registerShortcut({
      id: 'open-accessibility',
      description: 'Abrir panel de accessibility (Alt+Shift+A / Cmd+Shift+A)',
      keys: ['Alt', 'Shift', 'a'],
      callback: () => this.accessibilityService.show(),
      enabled: true
    });

    // Alt + Shift + H: Ir al inicio (PC) / Cmd + Shift + H (Mac) - SEGURO
    this.keyboardShortcuts.registerShortcut({
      id: 'go-home',
      description: 'Ir al inicio (Alt+Shift+H / Cmd+Shift+H)',
      keys: ['Alt', 'Shift', 'h'],
      callback: () => this.goToHome(),
      enabled: true
    });
  }

  private goToHome(): void {
    this.router.navigate(['/']).catch((error) => {
      console.error('Error navegando al inicio:', error);
    });
  }

  private registerIcons(): void {
    const basePath = 'assets/accessibility/';

    MAT_ICON_REGISTRY.forEach(({ alias, fileName }) => {
      if (!/^[a-zA-Z0-9._-]+\.svg$/.test(fileName)) {
        console.warn('Icon fileName inválido:', fileName);
        return;
      }

      const url = `${basePath}${fileName}`;

      this.iconRegistry.addSvgIcon(
        alias,
        this.sanitizer.bypassSecurityTrustResourceUrl(url) // NOSONAR - whitelist fija en código
      );
    });
  }
}
