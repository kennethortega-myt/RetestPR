import { Component, HostListener, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { finalize, take } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

import { IMaeImportarCandidato } from '../../../interfaces/importar.interface';
import { TipoEleccion } from '../../../interfaces/output/tipo-eleccion.model';
import { EleccionesCandidatosRequest } from '../../../interfaces/eleccionesCandidatosRequest';
import { EnumIdEleccion } from '../../../helpers/enums';

import { DialogComponent } from '../../../components/dialog/dialog/dialog.component';
import { DialogoConfirmacionComponent } from '../dialogo-confirmacion/dialogo-confirmacion.component';

import { LoadingService } from '../../../services/loading.service';
import { ResumenGeneralApiService } from '../../../services/resumen-general-api.service';
import { EleccionService } from '../../../services/eleccion.service';
import { SettingReportService } from '../../../services/setting-report.service';

@Component({
  selector: 'app-maestro',
  templateUrl: './maestro.component.html',
  imports: [ReactiveFormsModule, CommonModule, MatProgressBarModule]
})
export class MaestroComponent implements OnInit {
  
  private readonly fb = inject(FormBuilder);
  private readonly dialog = inject(MatDialog);
  private readonly toastService = inject(ToastrService);
  private readonly loaderService = inject(LoadingService);
  private readonly eleccionService = inject(EleccionService);
  private readonly settingReportService = inject(SettingReportService);
  private readonly resumenGeneralApiService = inject(ResumenGeneralApiService);

  readonly importarForm: FormGroup<{
    opt: FormControl<number | null>;
  }> = this.fb.group({
    opt: this.fb.control<number | null>(null),
  });
  
  public listEleccion: TipoEleccion[] = [];
  public seleccion: boolean = true;  

  complementos : IMaeImportarCandidato[] = [
    {
      value: EnumIdEleccion.ID_ELECCION_PRESIDENCIAL,
      icono: 'assets/icons/tipos-eleccion/ico-presidencial-min.svg',
      descripcion: '',
    },
    {
      value: EnumIdEleccion.ID_ELECCION_SENADORES_MULTIPLE,
      icono: 'assets/icons/tipos-eleccion/senador_DEM.svg',
      descripcion: '',
    },
    {
      value: EnumIdEleccion.ID_ELECCION_SENADORES_UNICO,
      icono: 'assets/icons/tipos-eleccion/senador_DEU.svg',
      descripcion: '',
    },
    {
      value: EnumIdEleccion.ID_ELECCION_DIPUTADOS,
      icono: 'assets/icons/tipos-eleccion/ico-diputados.svg',
      descripcion: '',
    },
    {
      value: EnumIdEleccion.ID_ELECCION_PARLAMENTO_ANDINO,
      icono: 'assets/icons/tipos-eleccion/ico-parlamento.svg',
      descripcion: '',
    }
  ];

  constructor() { }

  ngOnInit() {
    this.cargarElecciones();
  }

  seleccionarTipo(valor: number) {
    this.importarForm.get('opt')!.setValue(valor);
    this.seleccion = false;
  }

  esActivo(valor: number): boolean {
    return this.importarForm.get('opt')!.value === valor;
  }

  cargarElecciones(): void {
    this.resumenGeneralApiService.listarElecciones({
        idProceso: 2
      })
      .pipe(take(1))
      .subscribe({
      next: (resp) => {
        this.listEleccion = resp.data!.map((item: any) => {
          const complemento = this.complementos.find(
            (d) => d.value === item.value
          );
          
          return {
            ...item,
            icono: complemento?.icono,
            descripcion: complemento?.descripcion ?? item.descripcion
          };
        });
      },
    });
  }

  alertImportar(): void {
    const seleccionada = this.listEleccion.find(
      (e) => e.value === this.importarForm.value.opt
    );

    if (!seleccionada) return;

    this.dialog
      .open(DialogoConfirmacionComponent, {
        data: `¿Está seguro de que desea generar el archivo de la elección: ${seleccionada.text}?`,
      })
      .afterClosed()
      .subscribe((confirmado) => {
        if (confirmado) {
          this.importar(seleccionada.value);
        }
      });
  }

  public importar(idEleccion: number): void {
    this.loaderService.startRequest();
    const params: EleccionesCandidatosRequest = {
      usuario: this.settingReportService.getUserFromToken()?.usr ?? '',
      idEleccion: idEleccion
    };
    this.eleccionService
      .eleccionGenerar(params)
      .pipe(take(1),
      finalize(() => {
        this.loaderService.endRequest();
        this.seleccion = true;
        this.importarForm.reset();
        this.cargarElecciones();
      }))
      .subscribe({
        next: (resp) => {
          if (resp.success) {
            this.toastService.success(resp.message ?? 'Proceso iniciado correctamente');
          } else {
            this.mostrarError(resp.message);
          }
        },
        error: () => {
          this.mostrarError();
        }
      });
  }

  private mostrarError(mensaje = 'Ocurrió un error durante la importación'): void {
    this.dialog.open(DialogComponent, {
      width: '30%',
      minWidth: '320px',
      data: { mensaje },
    });
  }

  @HostListener('document:contextmenu', ['$event'])
  disableRightClick(event: MouseEvent): void {
    event.preventDefault();
  }
}
