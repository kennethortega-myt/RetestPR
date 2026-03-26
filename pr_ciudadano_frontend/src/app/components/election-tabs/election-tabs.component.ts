import { Component, EventEmitter, Input, Output, OnInit, HostListener } from "@angular/core";

@Component({
  selector: "app-election-tabs",
  templateUrl: "./election-tabs.component.html",
  styles: [],
  standalone: false,
})
export class ElectionTabsComponent implements OnInit {

  @Output() openTabEvent = new EventEmitter<string>();
  @Input() activeTab: string = "tab1";

  public Resultadoubicaciongeografica = "election-tabs.Resultadoubicaciongeografica"
  public Resultadocandidato = "election-tabs.Resultadocandidato"
  public Resultadoorganizacionpolitica = "election-tabs.Resultadoorganizacionpolitica"

  scrollPosition = 0;
  tabWidth = 300;
  canScrollLeft = false;
  canScrollRight = true;
  isMobile = false;

  ngOnInit() {
    this.checkIfMobile();
    if (this.isMobile) {
      this.updateArrowsState(this.activeTab);
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkIfMobile();
    if (!this.isMobile) {
      this.scrollPosition = 0;
    } else {
      this.updateArrowsState(this.activeTab);
    }
  }

  private checkIfMobile() {
    this.isMobile = window.innerWidth <= 768; 
  }

  openTab(tabName: string) {
    this.activeTab = tabName;
    this.openTabEvent.emit(tabName);

    if (this.isMobile) {
      this.updateArrowsState(tabName);
    }
  }

  scrollTabs(direction: 'left' | 'right') {
    if (!this.isMobile) return;
    
    if (direction === 'left' && this.canScrollLeft) {
      this.scrollPosition = Math.min(0, this.scrollPosition + this.tabWidth);
      
      if (this.activeTab === 'tab2') {
        this.openTab('tab1');
      } else if (this.activeTab === 'tab3') {
        this.openTab('tab2');
      }
    } else if (direction === 'right' && this.canScrollRight) {
      this.scrollPosition = Math.max(-120, this.scrollPosition - this.tabWidth);
      
      if (this.activeTab === 'tab1') {
        this.openTab('tab2');
      } else if (this.activeTab === 'tab2') {
        this.openTab('tab3');
      }
    }
  }

  private updateArrowsState(activeTab: string) {
    if (!this.isMobile) return;
    
    this.canScrollLeft = this.scrollPosition < 0 || activeTab !== 'tab1';
    this.canScrollRight = this.scrollPosition > -120 || activeTab !== 'tab3';
    
    switch(activeTab) {
      case 'tab1':
        this.scrollPosition = 0;
        this.canScrollLeft = false;
        break;
      case 'tab2':
        this.scrollPosition = -70;
        this.canScrollLeft = true;
        this.canScrollRight = true;
        break;
      case 'tab3':
        this.scrollPosition = -150;
        this.canScrollRight = false;
        break;
    }
  }

  getTransformStyle(): string {
    return this.isMobile ? `translateX(${this.scrollPosition}px)` : 'translateX(0px)';
  }
}