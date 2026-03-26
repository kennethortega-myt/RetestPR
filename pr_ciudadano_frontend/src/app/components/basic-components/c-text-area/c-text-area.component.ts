import { Component, Input, ViewChild } from '@angular/core';

@Component({
  selector: 'app-c-text-area',
  templateUrl: './c-text-area.component.html',
  standalone: false
})
export class CTextAreaComponent {
  @ViewChild('textareaChild') textareaChild!: any;

  // Params
  @Input() label = "";
  @Input() isRequired: boolean = false;
  @Input() placeHolder = "";
  @Input() upperCase: boolean = false;
  @Input() minLength: number = 1
  @Input() maxLength: number = 5
  @Input() pattern: string = "" //  this for the entire input value
  @Input() regexPattern: string = ""; // This is only for allowed characters
  @Input() regexTextAllowedPaste: string = "";

  // Input Data
  @Input() inputValue: string = '';

  // Input State
  @Input() submited: boolean = false;
  hasError: boolean = false;
  errorMessage: string = "";
}
