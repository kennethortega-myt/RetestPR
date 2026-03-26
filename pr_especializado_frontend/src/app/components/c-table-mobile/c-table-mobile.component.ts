import { Component, OnInit, Input, Output, EventEmitter, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslateModule } from '@ngx-translate/core';

/** Interfaz para las columnas de la tabla móvil */
export interface MobileTableColumn {
  key: string;
  label?: string;
  type?: 'text' | 'acciones' | 'expandedDetail' | 'html' | 'progress' | 'documentos' | 'detalle-estado';
}

/** Interfaz extendida para las filas con datos específicos del proyecto */
export interface MobileTableRow {
  [key: string]: any;
  // Campos específicos para mostrar en el detalle expandido
  tipo?: string;
  ambito?: string;
  departamento?: string;
  provincia?: string;
  distrito?: string;
  local?: string;
  mesa?: string;
  electores?: string;
  votos?: string;
  estadoacta?: string;
  documento?: string;
  archivosActa?: any[];
  archivosResolucion?: any[];

  // Para el detalle expandido personalizado
  detalles?: Array<{
    col1: string;
    col2: any;
    tipo: 'texto' | 'html' | 'progreso';
    color?: string;
  }>;
  acciones?: Array<{
    action: string;
    tooltip: string;
    icon: string;
    clase?: string;
  }>;
}

@Component({
  selector: 'app-c-table-mobile',
  standalone: true,
  imports: [
    MatTableModule,
    CommonModule,
    MatMenuModule,
    MatExpansionModule,
    MatTooltipModule,
    MatPaginatorModule,
    TranslateModule
  ],
  templateUrl: './c-table-mobile.component.html'
})
export class CTableMobileComponent implements OnInit {
  @Input() displayedColumns: MobileTableColumn[] = [];
  @Input() rowClassFn: (row: any) => string = () => '';
  @Input() dataSource = new MatTableDataSource<MobileTableRow>([]);
  @Input() isPoliticalOrganization: boolean = false;

  @Output() actionClick = new EventEmitter<{ action: string; row: any }>();
  @Output() verActa = new EventEmitter<any>();
  @Output() verResolucion = new EventEmitter<any>();

  expandedRow: MobileTableRow | null = null;

  @ViewChild(MatPaginator) paginator?: MatPaginator;
  readonly dialog = inject(MatDialog);

  ngOnInit(): void {
    // Inicialización del componente
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
  }

  // Eliminada duplicación de lógica en ngAfterViewInit

  get displayedColumnKeys() {
    return this.displayedColumns.map((c) => c.key);
  }

  toggleExpand(row: MobileTableRow) {
    this.expandedRow = this.expandedRow === row ? null : row;
  }

  onAction(action: string, row: MobileTableRow) {
    this.actionClick.emit({ action, row });
  }

  handleVerActa(element: MobileTableRow) {
    this.verActa.emit(element);
  }

  handleVerResolucion(element: MobileTableRow) {
    this.verResolucion.emit(element);
  }

  generateRowDetails(row: MobileTableRow): Array<{ col1: string; col2: any; tipo: 'texto' | 'html' }> {
    const details = [];

    if (row.tipo) {
      details.push({
        col1: 'Resultadoapp.TipoEleccion',
        col2: row.tipo,
        tipo: 'texto' as const
      });
    }

    if (row.ambito) {
      details.push({
        col1: 'Resultadoapp.Ambito',
        col2: row.ambito,
        tipo: 'texto' as const
      });
    }

    if (row.departamento) {
      details.push({
        col1: 'Resultadoapp.Region',
        col2: row.departamento,
        tipo: 'texto' as const
      });
    }

    if (row.provincia) {
      details.push({
        col1: 'Resultadoapp.Provincia',
        col2: row.provincia,
        tipo: 'texto' as const
      });
    }

    if (row.distrito) {
      details.push({
        col1: 'Resultadoapp.Distrito',
        col2: row.distrito,
        tipo: 'texto' as const
      });
    }

    if (row.local) {
      details.push({
        col1: 'Resultadoapp.LocalVotacion',
        col2: row.local,
        tipo: 'texto' as const
      });
    }

    if (row.mesa) {
      details.push({
        col1: 'Resultadoapp.Nmesa',
        col2: row.mesa,
        tipo: 'texto' as const
      });
    }

    if (row.electores) {
      details.push({
        col1: 'Resultadoapp.ElectoresHabiles',
        col2: row.electores,
        tipo: 'texto' as const
      });
    }

    if (row.votos && this.isPoliticalOrganization) {
      details.push({
        col1: 'Resultadoapp.VotosObtenidos',
        col2: row.votos,
        tipo: 'texto' as const
      });
    }

    if (row.documento) {
      details.push({
        col1: 'Resultadoapp.EstadoActa',
        col2: row.documento,
        tipo: 'texto' as const
      });
    }

    return details;
  }

  generateRowActions(row: MobileTableRow): Array<{ action: string; tooltip: string; icon: string; clase?: string }> {
    const actions = [];

    if (row.archivosActa && row.archivosActa.length > 0) {
      actions.push({
        action: 'verActa',
        tooltip: 'VerActa',
        icon: 'assets/iconosConsultaOP/actas.svg',
        clase: ''
      });
    }

    if (row.archivosResolucion && row.archivosResolucion.length > 0) {
      actions.push({
        action: 'verResolucion',
        tooltip: 'VerResolucion',
        icon: 'assets/iconosConsultaOP/resolucion2.svg',
        clase: ''
      });
    }

    return actions;
  }

  handleTableAction(event: { action: string; row: MobileTableRow }) {
    const { action, row } = event;

    switch (action) {
      case 'verActa':
        this.handleVerActa(row);
        break;
      case 'verResolucion':
        this.handleVerResolucion(row);
        break;
      default:
        this.onAction(action, row);
        break;
    }
  }

  getCombinedDetailState(element: MobileTableRow): string {
    const detalle =
      element['mesa'] ??
      element['detalle'] ??
      element['numero'] ??
      element['id'] ??
      '';

    const estado =
      element['documento'] ??
      element['estadoacta'] ??
      element['estado'] ??
      '';

    if (detalle && estado) {
      return `${detalle} - ${estado}`;
    } else if (detalle) {
      return detalle;
    } else if (estado) {
      return estado;
    }

    return 'Sin datos';
  }
}
