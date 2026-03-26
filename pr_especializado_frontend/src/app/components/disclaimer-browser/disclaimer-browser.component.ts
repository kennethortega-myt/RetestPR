import { CommonModule } from '@angular/common';
import { Component, output, ViewEncapsulation } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'onpe-disclaimer-browser',
  templateUrl: './disclaimer-browser.component.html',
  styleUrl: './disclaimer-browser.component.scss',
  encapsulation: ViewEncapsulation.None,
  host: { class: 'onpe-disclaimer-browser' },
  imports: [CommonModule, TranslateModule],
})
export class DisclaimerBrowserComponent {
  close = output<void>();

  closeDisclaimer(): void {
    this.close.emit();
  }
}
