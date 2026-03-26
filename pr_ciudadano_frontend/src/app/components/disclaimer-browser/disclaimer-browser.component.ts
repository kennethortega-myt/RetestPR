import { Component, output, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'onpe-disclaimer-browser',
  standalone: false,
  templateUrl: './disclaimer-browser.component.html',
  styleUrl: './disclaimer-browser.component.scss',
  encapsulation: ViewEncapsulation.None,
  host: { class: 'onpe-disclaimer-browser' }
})
export class DisclaimerBrowserComponent {
  close = output<void>();

  closeDisclaimer(): void {
    this.close.emit();
  }
}
