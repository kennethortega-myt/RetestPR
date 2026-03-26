import {
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  OnInit,
  QueryList,
  ViewChildren
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, take } from 'rxjs';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

import { IChartBarInfo } from '../../../../interfaces/chart-bar-info.interface';
import {
  GeneralSummaryTotals,
  AllInformationAboutElection,
  IElectionType
} from '../../../../interfaces/elections.interfaces';
import { ElectionsService } from '../../../../services/elecciones-generales/elections.service';
import { URL_PATHS_TO_REDIRECT } from '../../../../settings/app.routes.settings';
import { DistritalElectionComponent } from './distrital-election/distrital-election.component';
import { setResumenGeneralUrlInSessionStorage } from '../../../../helpers/session-storage-helper';
import { KeyboardShortcutsService } from '../../../../services/accesibilidad/keyboard-shortcuts.service';

@Component({
  selector: 'app-resumen-general',
  templateUrl: './resumen-general.component.html',
  standalone: false
})
export class ResumenGeneralComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChildren(DistritalElectionComponent, { read: ElementRef }) electionSectionList: QueryList<ElementRef>;

  mostrarFavorito = false;
  public myForm: FormGroup = this.fb.group({
    filtroAgruPolitica: ['']
  });

  private electionsService = inject(ElectionsService);
  public presidentialElectionInfo = {} as GeneralSummaryTotals;
  public allInformationAboutElection = {} as AllInformationAboutElection;

  public dataForDistrict: IChartBarInfo[] = [];
  public scalesForDistrict: number[] = [];

  public showPresidentialElection = false;
  public showCongressionalElection = false;
  public showProvincialElection = false;
  public showDistrictElection = true;
  public showNationalReferendumElection = false;
  public showAndeanParliamentElection = false;
  public showRegionalElection = false;
  public showRevocationElection = false;

  public isLoadedElectionTypes = false;
  public electionTypes: IElectionType[] = [];

  public isResponsive = false;
  private breakpointSubscription: Subscription;
  private currentSectionIndex = 0;

  constructor(
    private readonly fb: FormBuilder,
    private router: Router,
    private el: ElementRef,
    private readonly breakpointObserver: BreakpointObserver,
    private readonly keyboardShortcuts: KeyboardShortcutsService
  ) {
    sessionStorage.setItem('favorito', '0');
    this.breakpointSubscription = this.breakpointObserver
      .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
      .subscribe((result) => {
        this.isResponsive = result.matches;
      });
  }

  ngOnInit() {
    setResumenGeneralUrlInSessionStorage();
    this.mostrarFavorito = sessionStorage.getItem('favorito') !== '0';
    this.loadElectionList();
    this.registerKeyboardShortcuts();
  }

  ngAfterViewInit() {
    const index = history.state?.sectionIndex;

    if (index !== undefined) {
      history.replaceState({ ...history.state, sectionIndex: undefined }, '');
      this.tryScroll(index);
    }
  }

  private tryScroll(index: number): void {
    if (this.electionSectionList?.length > index) {
      this.scrollToElementByIndex(index);
    } else {
      setTimeout(() => this.tryScroll(index), 500);
    }
  }

  ngOnDestroy(): void {
    this.breakpointSubscription.unsubscribe();
    this.unregisterKeyboardShortcuts();
  }

  private loadElectionList() {
    this.electionsService
      .getElectionTypesForGeneralSummary$()
      .pipe(take(1))
      .subscribe((response) => {
        this.isLoadedElectionTypes = true;
        if (response.success) {
          this.electionTypes = this.getCustomArrayElectionTypes(response.data);
        } else {
          console.log('loadElectionList error');
        }
      });
  }

  scrollToElement(elementId: string): void {
    const element = this.el.nativeElement.querySelector(`#${elementId}`);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  public get filteredDataForDistrict(): IChartBarInfo[] {
    return this.dataForDistrict.filter((_) => _);
  }

  public scrollToElementByIndex(index: number): void {
    const currentElement = this.electionSectionList.get(index).nativeElement;
    if (currentElement) {
      currentElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  verActas() {
    this.router.navigate([URL_PATHS_TO_REDIRECT.actas]);
  }

  vercongresales() {
    this.router.navigate([URL_PATHS_TO_REDIRECT.diputados]);
  }

  verPresidenciales() {
    this.router.navigate([URL_PATHS_TO_REDIRECT.presidenciales]);
  }
  verParlamemto() {
    this.router.navigate([URL_PATHS_TO_REDIRECT.parlamento_andino]);
  }

  private getCustomArrayElectionTypes(electionTypes: IElectionType[]): IElectionType[] {
    return electionTypes; //customElectionTypes;
  }

  private registerKeyboardShortcuts(): void {
    // Alt + Shift + ↓: Siguiente sección (PC) / Cmd + Shift + ↓ (Mac) - SEGURO
    this.keyboardShortcuts.registerShortcut({
      id: 'next-section',
      description: 'Ir a la siguiente sección (Alt+Shift+↓ / Cmd+Shift+↓)',
      keys: ['Alt', 'Shift', 'ArrowDown'],
      callback: () => this.goToNextSection(),
      enabled: true
    });

    // Alt + Shift + ↑: Sección anterior (PC) / Cmd + Shift + ↑ (Mac) - SEGURO
    this.keyboardShortcuts.registerShortcut({
      id: 'prev-section',
      description: 'Ir a la sección anterior (Alt+Shift+↑ / Cmd+Shift+↑)',
      keys: ['Alt', 'Shift', 'ArrowUp'],
      callback: () => this.goToPreviousSection(),
      enabled: true
    });

    // Alt + Shift + 1,2,3 (PC) / Cmd + Shift + 1,2,3 (Mac) - SEGURO
    this.keyboardShortcuts.registerShortcut({
      id: 'go-section-1',
      description: 'Ir a la primera sección (Alt+Shift+1 / Cmd+Shift+1)',
      keys: ['Alt', 'Shift', '1'],
      callback: () => this.goToSectionByIndex(0),
      enabled: true
    });

    this.keyboardShortcuts.registerShortcut({
      id: 'go-section-2',
      description: 'Ir a la segunda sección (Alt+Shift+2 / Cmd+Shift+2)',
      keys: ['Alt', 'Shift', '2'],
      callback: () => this.goToSectionByIndex(1),
      enabled: true
    });

    this.keyboardShortcuts.registerShortcut({
      id: 'go-section-3',
      description: 'Ir a la tercera sección (Alt+Shift+3 / Cmd+Shift+3)',
      keys: ['Alt', 'Shift', '3'],
      callback: () => this.goToSectionByIndex(2),
      enabled: true
    });
  }

  private unregisterKeyboardShortcuts(): void {
    this.keyboardShortcuts.unregisterShortcut('next-section');
    this.keyboardShortcuts.unregisterShortcut('prev-section');
    this.keyboardShortcuts.unregisterShortcut('go-section-1');
    this.keyboardShortcuts.unregisterShortcut('go-section-2');
    this.keyboardShortcuts.unregisterShortcut('go-section-3');
  }

  private goToNextSection(): void {
    const totalSections = this.electionSectionList.length;
    if (totalSections > 0) {
      this.currentSectionIndex = (this.currentSectionIndex + 1) % totalSections;
      this.scrollToElementByIndex(this.currentSectionIndex);
    }
  }

  private goToPreviousSection(): void {
    const totalSections = this.electionSectionList.length;
    if (totalSections > 0) {
      this.currentSectionIndex = this.currentSectionIndex === 0 ? totalSections - 1 : this.currentSectionIndex - 1;
      this.scrollToElementByIndex(this.currentSectionIndex);
    }
  }

  private goToSectionByIndex(index: number): void {
    const totalSections = this.electionSectionList.length;
    if (index >= 0 && index < totalSections) {
      this.currentSectionIndex = index;
      this.scrollToElementByIndex(index);
    }
  }
}
