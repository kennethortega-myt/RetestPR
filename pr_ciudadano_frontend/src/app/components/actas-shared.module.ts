import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { PdfViewerModule } from 'ng2-pdf-viewer';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { PipesModule } from '../pipes/pipes.module';

const MATERIAL_MODULES = [
  MatSelectModule,
  MatFormFieldModule,
  MatInputModule,
  MatButtonModule,
  MatDialogModule,
  MatTooltipModule,
  MatAutocompleteModule,
  MatIconModule,
  MatProgressSpinnerModule,
  MatTableModule,
  MatMenuModule,
  MatSnackBarModule,
  MatCheckboxModule
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PdfViewerModule,
    NgxMaskDirective,
    NgxMaskPipe,
    TranslateModule,
    PipesModule,
    ...MATERIAL_MODULES
  ],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    PdfViewerModule,
    NgxMaskDirective,
    NgxMaskPipe,
    TranslateModule,
    PipesModule,
    ...MATERIAL_MODULES
  ]
})
export class ActasSharedModule {}
