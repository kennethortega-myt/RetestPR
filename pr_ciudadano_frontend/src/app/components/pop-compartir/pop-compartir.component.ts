import { Component, inject, OnInit } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { SnackbarService } from "../../services/elecciones-generales/snackbar.service";
import { IFRAMES } from "./pop-compartir.constants";
import { FormBuilder } from "@angular/forms";
import { SHARE_ELECTION_TITLE } from "../../helpers/share-election-title.helper";
import { MENU_ELECTION_ICONS_KEYS } from "../../settings/icon-keys.settings";
import { getEncryptStorageEleccionValue } from "../../helpers/encrypt-storage-eleccion";
import { PROCESOS_ELECTORALES_EXISTENTES } from "../../settings/procesos-electorales.settings";
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: "app-pop-compartir",
  templateUrl: "./pop-compartir.component.html",
  styles: [
    `
      .textarea {
        height: auto !important;
        min-height: 10em;
      }
    `,
  ],
  standalone: false,
})
export class PopCompartirComponent implements OnInit {
  public codigoGeneradoKey = 'pop-compartir.codigoGenerado';
  public instruccionKey = 'pop-compartir.instruccion';
  public cerrarKey = 'pop-compartir.cerrar';
  public copiarKey = 'pop-compartir.copiar';

  private snackBar = inject(SnackbarService);

  TITLE = SHARE_ELECTION_TITLE;
  PROCESO = PROCESOS_ELECTORALES_EXISTENTES;
  ELECTION = MENU_ELECTION_ICONS_KEYS;

  iframes = IFRAMES;
  selectedIFrames = "";

  form = this.fb.group({
    presidencialesCheck: this.fb.control<boolean>(false),
    diputadosCheck: this.fb.control<boolean>(false),
    parlamentoCheck: this.fb.control<boolean>(false),
    distritoElectoralMultiple: this.fb.control<boolean>(false),
    distritoElectoralUnico: this.fb.control<boolean>(false),
  });

  idTipoProceso:string = '';

  constructor(public dialogRef: MatDialogRef<PopCompartirComponent>, private readonly fb: FormBuilder) {
    this.idTipoProceso = getEncryptStorageEleccionValue('TIPO_DE_PROCESO_ELECTORAL_A_CARGAR') ?? '';
  }

  ngOnInit(): void {
    this.form.controls.presidencialesCheck.valueChanges.subscribe(() => {
      this.buildCustomIFrame();
    });
    this.form.controls.diputadosCheck.valueChanges.subscribe(() => {
      this.buildCustomIFrame();
    });
    this.form.controls.parlamentoCheck.valueChanges.subscribe(() => {
      this.buildCustomIFrame();
    });
    this.form.controls.distritoElectoralMultiple.valueChanges.subscribe(() => {
      this.buildCustomIFrame();
    });
    this.form.controls.distritoElectoralUnico.valueChanges.subscribe(() => {
      this.buildCustomIFrame();
    });
  }

  public closeDialog() {
    this.dialogRef.close();
  }

  public copyCustomIFrame() {
    navigator.clipboard.writeText(this.selectedIFrames).then(() => {
      this.snackBar.showSnackbarWithSuccessMessage("El texto se copió correctamente!");
    });
  }

  private buildCustomIFrame() {
    const { presidencialesCheck, diputadosCheck, parlamentoCheck, distritoElectoralMultiple, distritoElectoralUnico } =
      this.form.controls;
    const { diputados, distrito_electoral_multiple, distrito_electoral_unico, parlamento_andino, presidenciales } =
      this.iframes;

    this.selectedIFrames = "";

    this.selectedIFrames = presidencialesCheck.value ? presidenciales : "";
    this.selectedIFrames = this.selectedIFrames + (diputadosCheck.value ? diputados : "");
    this.selectedIFrames = this.selectedIFrames + (parlamentoCheck.value ? parlamento_andino : "");
    this.selectedIFrames = this.selectedIFrames + (distritoElectoralMultiple.value ? distrito_electoral_multiple : "");
    this.selectedIFrames = this.selectedIFrames + (distritoElectoralUnico.value ? distrito_electoral_unico : "");
  }
}
