import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from "@angular/core";

@Component({
  selector: "app-pagination",
  templateUrl: "./pagination.component.html",
  styles: [
    `
      .activado {
        background: rgb(41, 113, 185);
        color: rgb(255, 255, 255);
      }
      .disabled {
        opacity: 0.5;
      }
    `,
  ],
  standalone: false,
})
export class PaginationComponent implements OnInit, OnChanges {
  @Input() currentPage = 1;
  @Input() numberOfPages = 2;

  @Output() loadPageEvent = new EventEmitter();

  public listOfPages: number[] = [];
  public selectedPage = 1;
  public disableNextPage = true;
  public disablePrevPage = true;

  ngOnInit(): void {
    let arr = new Array(this.numberOfPages).fill("");
    this.listOfPages = arr.map((_, index) => index + 1);
    this.selectedPage = this.currentPage;
    this.validatePrevAndNextPage();
  }

  ngOnChanges(changes: SimpleChanges): void {
    let arr = new Array(this.numberOfPages).fill("");
    this.listOfPages = arr.map((_, index) => index + 1);
    this.selectedPage = this.currentPage;
    this.validatePrevAndNextPage();
  }

  public selectPage(page: number) {
    if (this.selectedPage != page) {
      this.selectedPage = page;
      this.loadPageEvent.emit(page);
      this.validatePrevAndNextPage();
    }
  }

  public nextPage() {
    if (!this.disableNextPage) {
      this.selectedPage = this.selectedPage + 1;
      this.loadPageEvent.emit(this.selectedPage);
      this.validatePrevAndNextPage();
    }
  }

  public prevPage() {
    if (!this.disablePrevPage) {
      this.selectedPage = this.selectedPage - 1;
      this.loadPageEvent.emit(this.selectedPage);
      this.validatePrevAndNextPage();
    }
  }

  private validatePrevAndNextPage() {
    this.disableNextPage = this.selectedPage >= this.listOfPages.length;
    this.disablePrevPage = this.selectedPage <= 1;
  }
}
