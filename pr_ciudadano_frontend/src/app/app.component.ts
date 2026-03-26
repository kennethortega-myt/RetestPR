import { OverlayContainer } from '@angular/cdk/overlay';
import { DOCUMENT } from '@angular/common';
import {
  AfterViewInit,
  Component,
  computed,
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
import { MatBottomSheet } from '@angular/material/bottom-sheet';
import { MatIconRegistry } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarHorizontalPosition } from '@angular/material/snack-bar';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { AccesibilidadComponent } from './components/accesibilidad/accesibilidad.component';
import {
  ITEM_CONTRAST_STEP_1,
  ITEM_CURSOR_STEP_2,
  ITEM_DISLEXIA_FRIENDLY_STEP_1,
  ITEM_READING_MASK_STEP_1,
  ITEM_READING_MASK_STEP_2,
  PROFILE_DEFAULT_EMPTY,
  PROFILE_TDAH
} from './components/accesibilidad/accesibilidad.constant';
import { MAT_ICON_REGISTRY } from './constants/mat-icons';
import { isBrowserChrome } from './helpers/browser';
import { CursorService } from './services/accesibilidad/cursor.service';
import { KeyboardShortcutsService } from './services/accesibilidad/keyboard-shortcuts.service';
import { DEFAULT_PATHS_TO_REDIRECT } from './settings/app.routes.settings';
import { DisclaimerBrowserComponent } from './components/disclaimer-browser/disclaimer-browser.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  host: { '[class]': 'rootClass()' }
})
export class AppComponent implements OnInit, AfterViewInit {
  private readonly _overlayContainer = inject(OverlayContainer);
  private readonly _renderer = inject(Renderer2);
  private readonly _document = inject(DOCUMENT);
  private readonly _snackBar = inject(MatSnackBar);
  @ViewChild('cursorContainer') cursorContainer!: ElementRef<HTMLElement>;
  @ViewChild('disclaimerTpl') disclaimerTpl!: TemplateRef<DisclaimerBrowserComponent>;
  idleTimeout: any;
  lastX: number = 0;
  lastY: number = 0;
  grandCursor: boolean = false;
  readingMaskActive: boolean = false;
  contrastActive: string = ITEM_CONTRAST_STEP_1.id;
  dislexiaFriendlyActive: string = ITEM_DISLEXIA_FRIENDLY_STEP_1.id;
  rootClass: Signal<string> = computed(() => {
    return `${this.cursorService.contrastActive().id} ${this.cursorService.dislexiaFriendlyActive().id} ${this.cursorService.interlineadoActive().id}`;
  });

  constructor(
    private readonly _bottomSheet: MatBottomSheet,
    private readonly cursorService: CursorService,
    private readonly iconRegistry: MatIconRegistry,
    private readonly sanitizer: DomSanitizer,
    private readonly keyboardShortcuts: KeyboardShortcutsService,
    private router: Router
  ) {
    this.cursorService.loadDataFromLocalStorage();
    effect(() => {
      const textSizeActive = this.cursorService.textSizeActive();
      this.applyTextSize(textSizeActive.id);
    });
    effect(() => {
      const cursorActive = this.cursorService.cursorActive();
      const readingMaskActive = this.cursorService.readingMaskActive();
      this.grandCursor = cursorActive.id === ITEM_CURSOR_STEP_2.id;
      this.readingMaskActive = readingMaskActive.id === ITEM_READING_MASK_STEP_2.id;
      this.applyBigCursorMode();
      this.applyReadingMaskOverlay();
    });
    effect(() => {
      const isMobile = this.cursorService.isMobile();
      this.validBrowserRecomended(isMobile);
    });
  }

  @HostListener('window:mousemove', ['$event']) onMouseMove(event: MouseEvent): void {
    if (!this.readingMaskActive) {
      return;
    }
    this.lastX = event.clientX;
    this.lastY = event.clientY;

    this.updateCursorPosition(this.lastX, this.lastY);
  }

  @HostListener('window:resize') onResize(): void {
    const isMobile = window.innerWidth <= 768;
    if (isMobile) {
      this.cursorService.readingMaskActiveUpdate(ITEM_READING_MASK_STEP_1);
      if (this.cursorService.profileActive().id === PROFILE_TDAH.id) {
        this.cursorService.profileActiveUpdate(PROFILE_DEFAULT_EMPTY);
      }
    }

    if (isMobile !== this.cursorService.isMobile()) {
      this.cursorService.isMobile.set(isMobile);
    }
  }

  ngOnInit(): void {
    this.registerIcons();
    this.registerKeyboardShortcuts();
  }

  ngAfterViewInit(): void {
    this.validBrowserRecomended();
  }

  closeDisclaimer(): void {
    this._snackBar.dismiss();
  }

  public openBottomSheet(): void {
    this._bottomSheet.open(AccesibilidadComponent, {
      panelClass: 'barra-accesibilidad'
    });
  }

  private updateCursorPosition(x: number, y: number): void {
    const _maskActive = this.cursorContainer.nativeElement;

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

  private registerIcons(): void {
    const pathIcons = 'assets/img/accesibilidad/';

    for (const icon of MAT_ICON_REGISTRY) {
      this.iconRegistry.addSvgIcon(
        icon.alias,
        this.sanitizer.bypassSecurityTrustResourceUrl(`${pathIcons}${icon.fileName}`)
      );
    }
  }

  private registerKeyboardShortcuts(): void {
    this.keyboardShortcuts.registerShortcut({
      id: 'open-accessibility',
      description: 'Abrir panel de accesibilidad (Alt+Shift+A / Cmd+Shift+A)',
      keys: ['Alt', 'Shift', 'a'],
      callback: () => this.openBottomSheet(),
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

    // Alt + Shift + M: Ir al menú (PC) / Cmd + Shift + M (Mac) - SEGURO
    this.keyboardShortcuts.registerShortcut({
      id: 'go-menu',
      description: 'Ir al menú principal (Alt+Shift+M / Cmd+Shift+M)',
      keys: ['Alt', 'Shift', 'm'],
      callback: () => this.goToMenu(),
      enabled: true
    });
  }

  private goToHome(): void {
    // Usar Angular Router para navegación al inicio
    this.router.navigate(['/']).catch((error) => {
      console.error('Error navegando al inicio:', error);
    });
  }

  private goToMenu(): void {
    // Usar Angular Router para navegación al menú principal (resumen general)
    // Navegar a la página principal del proceso electoral
    this.router.navigateByUrl(DEFAULT_PATHS_TO_REDIRECT.elecciones_generales_o_bicameralidad).catch((error) => {
      console.error('Error navegando al menú principal:', error);
    });
  }

  validBrowserRecomended(isMobile: boolean = false): void {
    if (this.disclaimerTpl && !isBrowserChrome()) {
      const horizontalPosition: MatSnackBarHorizontalPosition = isMobile ? 'start' : 'center';

      this._snackBar.openFromTemplate(this.disclaimerTpl, {
        panelClass: 'disclaimer-snackbar-container',
        horizontalPosition,
        verticalPosition: 'bottom'
      });
    }
  }
}
