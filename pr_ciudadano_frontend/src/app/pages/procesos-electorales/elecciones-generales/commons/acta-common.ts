import { IActasComponent } from "../../../../interfaces/actas.interfaces";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { RandomImageService } from "../../../../services/elecciones-generales/random-image.service";
import { ActaResumenGeneralTotalesResponse } from "../../../../interfaces/response/acta-resumen-general-response.interface";

export class ActaCommon implements IActasComponent {

  deshabilitarBotonFiltrar: boolean = true;
  deshabilitarBotonLimpiar: boolean = true;
  protected resumenTotales: ActaResumenGeneralTotalesResponse;

  controlAmbito: any;
  public randomImageUrl: string;

  public myFormUbigeo: FormGroup = this.fb.group({
    region: [1, Validators.required],
    eleccion: [0, Validators.required],
    departamento: ["0", Validators.required],
    provincia: ["0", Validators.required],
    distrito: ["0", Validators.required],
    cent_educativo: [0, Validators.required],
  });

  constructor(
    private readonly fb: FormBuilder,
    public readonly route: ActivatedRoute,
    public readonly randomImageService: RandomImageService
  ) {}

  iniciar() {
    this.controlAmbito = this.myFormUbigeo.controls["region"];
    this.randomImageUrl = this.randomImageService.getRandomImage();
  }
}