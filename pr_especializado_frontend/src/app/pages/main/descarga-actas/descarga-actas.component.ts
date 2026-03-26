import { Component, inject, OnInit } from '@angular/core';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ComponentsModule } from '../../../components/components.module';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';
import { LoadingService } from '../../../components/loading/loading.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { environment } from '../../../../environments/environment';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import 'dayjs/locale/es';
import { UbigeoApiService } from '../../../services/ubigeo-api.service';
import { DialogService } from '../../../services/dialog.service';
import { OnpeDatePipe } from '../../../pipes/onpe-date.pipe';

dayjs.extend(customParseFormat);
dayjs.locale('es');

// Interfaz para el ZIP
export interface ZipInfo {
  tipoEleccion: string;
  region: string;
  nombreArchivo: string;
  ruta: string;
  tamanio: string;
  fechaModificacion: string;
  urlDescarga: string;
}

// Interfaz para la respuesta del endpoint
export interface ListarZipsResponse {
  [tipoEleccion: string]: ZipInfo[];
}

// Interfaz para la fila de la tabla
export interface FilaTablaDescarga {
  region: string;

  // Presidencial
  presidencialDisponible: boolean;
  presidencialUrl: string;
  presidencialFecha: string;

  // Senadores DEM
  senadoresDemDisponible: boolean;
  senadoresDemUrl: string;
  senadoresDemFecha: string;

  // Senadores DEU
  senadoresDeuDisponible: boolean;
  senadoresDeuUrl: string;
  senadoresDeuFecha: string;

  // Diputados
  diputadosDisponible: boolean;
  diputadosUrl: string;
  diputadosFecha: string;

  // Parlamento Andino
  parlamentoDisponible: boolean;
  parlamentoUrl: string;
  parlamentoFecha: string;
}

@Component({
  selector: 'app-descarga-actas',
  imports: [
    MatTableModule,
    ComponentsModule,
    CommonModule,
    ReactiveFormsModule,
    MatSelectModule,
    TranslateModule,
    MatExpansionModule,
    OnpeDatePipe
  ],
  templateUrl: './descarga-actas.component.html',
})
export class DescargaActasComponent implements OnInit {

  private readonly http = inject(HttpClient);
  private readonly loadingService = inject(LoadingService);
  private readonly ubigeoService = inject(UbigeoApiService);
  private readonly dialogService = inject(DialogService);

  displayedColumns: string[] = [
    'region',
    'presidencial',
    'senadoresDem',
    'senadoresDeu',
    'diputados',
    'parlamento',
  ];

  dataSource = new MatTableDataSource<FilaTablaDescarga>([]);

  // URL del backend
  private readonly API_URL = environment.apiUrl;


  private REGIONES_PERU: string[] = [];

  // Mapeo de tipos de elección (nombre en backend → nombre en frontend)
  private readonly TIPO_ELECCION_MAP: { [key: string]: string } = {
    'Presidencial': 'presidencial',
    'Diputados': 'diputados',
    'Parlamento_andino': 'parlamento',
    'Senadores_dem': 'senadoresDem',
    'Senadores_deu': 'senadoresDeu'
  };

  mostrarTabla = true;

  isLoading: boolean = true;

  ngOnInit(): void {
    this.cargarRegionesYActualizar();
  }

  cargarRegionesYActualizar(): void {
    this.loadingService.show();
    this.ubigeoService.listarDepartamentos().subscribe({
      next: (regiones) => {
        this.REGIONES_PERU = regiones;
        this.actualizarTabla();
      },
      error: (error) => {
        console.error('Error al cargar regiones:', error);
        this.loadingService.hide();
        this.isLoading = false;
      }
    });
  }

  actualizarTabla(): void {
    // La carga ya se muestra en cargarRegionesYActualizar, pero por seguridad
    // loadService.show() podria llamarse nuevamente si se llama independiente,
    // pero aqui es parte del flujo secuencial

    const url = `${this.API_URL}/procesamientoActas/listarZips`;

    this.http.get<ListarZipsResponse>(url).subscribe({
      next: (response) => {
        this.procesarRespuesta(response);
        this.isLoading = false;
        this.loadingService.hide();
      },
      error: (error) => {
        console.error('Error al obtener ZIPs:', error);
        this.inicializarTablaVacia();
        this.isLoading = false;
        this.loadingService.hide();
      }
    });
  }


  private inicializarTablaVacia(): void {
    const filas: FilaTablaDescarga[] = this.REGIONES_PERU.map(region => ({
      region: region,
      presidencialDisponible: false,
      presidencialUrl: '',
      presidencialFecha: '',
      senadoresDemDisponible: false,
      senadoresDemUrl: '',
      senadoresDemFecha: '',
      senadoresDeuDisponible: false,
      senadoresDeuUrl: '',
      senadoresDeuFecha: '',
      diputadosDisponible: false,
      diputadosUrl: '',
      diputadosFecha: '',
      parlamentoDisponible: false,
      parlamentoUrl: '',
      parlamentoFecha: ''
    }));

    this.dataSource.data = filas;
    this.mostrarTabla = true;
  }


  private procesarRespuesta(response: ListarZipsResponse): void {

    const regionesMap = new Map<string, FilaTablaDescarga>();

    // Crear todas las regiones con valores por defecto
    this.REGIONES_PERU.forEach(region => {
      regionesMap.set(region, {
        region: region,
        presidencialDisponible: false,
        presidencialUrl: '',
        presidencialFecha: '',
        senadoresDemDisponible: false,
        senadoresDemUrl: '',
        senadoresDemFecha: '',
        senadoresDeuDisponible: false,
        senadoresDeuUrl: '',
        senadoresDeuFecha: '',
        diputadosDisponible: false,
        diputadosUrl: '',
        diputadosFecha: '',
        parlamentoDisponible: false,
        parlamentoUrl: '',
        parlamentoFecha: ''
      });
    });

    // Si hay respuesta, actualizar las regiones con ZIPs disponibles
    if (response && Object.keys(response).length > 0) {

      // Procesar cada tipo de elección
      Object.keys(response).forEach(tipoEleccion => {
        const zips = response[tipoEleccion];

        zips.forEach(zip => {
          const region = zip.region;

          // Si la región existe en el mapa, actualizarla
          if (regionesMap.has(region)) {
            const fila = regionesMap.get(region)!;

            // Determinar a qué columna pertenece este ZIP
            const columna = this.obtenerColumna(tipoEleccion);
            if (columna) {
              // Marcar como disponible y guardar URL y fecha
              (fila as any)[`${columna}Disponible`] = true;
              (fila as any)[`${columna}Url`] = this.API_URL + zip.urlDescarga;
              (fila as any)[`${columna}Fecha`] = this.formatearFecha(zip.fechaModificacion);
            }
          } else {
            console.warn(`Región no reconocida: ${region}`);
          }
        });
      });
    }

    const filas = Array.from(regionesMap.values());

    // Actualizar la tabla
    this.dataSource.data = filas;
    this.mostrarTabla = true;
  }

  /**
   * Determina a qué columna pertenece un tipo de elección
   */
  private obtenerColumna(tipoEleccion: string): string | null {

    // Buscar en el mapeo
    const columna = this.TIPO_ELECCION_MAP[tipoEleccion];
    if (columna) {
      return columna;
    }

    // Búsqueda flexible (por si el nombre varía ligeramente)
    const tipoLower = tipoEleccion.toLowerCase().replaceAll(/[_\s]/g, '');
    if (tipoLower.includes('presidencial')) {
      return 'presidencial';
    } else if (tipoLower.includes('diputados')) {
      return 'diputados';
    } else if (tipoLower.includes('parlamento') || tipoLower.includes('andino')) {
      return 'parlamento';
    } else if (tipoLower.includes('dem')) {
      return 'senadoresdem';
    } else if (tipoLower.includes('deu')) {
      return 'senadoresdeu';
    }

    console.warn('Tipo de elección no reconocido:', tipoEleccion);
    return null;
  }

  /**
   * Formatea la fecha para mostrar en la tabla
   */
  private formatearFecha(fechaStr: string): string {
    if (!fechaStr) return '';
    
    const fechaObtenida = fechaStr.split('  -  ');
    
    if (fechaObtenida.length === 2) {
      const fecha = fechaObtenida[0].trim();
      const horario = fechaObtenida[1].trim().replace(' h', '');
      
      const fechaCompleta = `${fecha} ${horario}`;
      const fechaDayjs = dayjs(fechaCompleta, 'DD/MM/YYYY HH:mm:ss');
      
      if (fechaDayjs.isValid()) {
        const fechaFormateada = fechaDayjs.format('DD/MM/YYYY hh:mm:ss');
        const ampm = fechaDayjs.format('a');
        
        const turno = ampm === 'am' ? 'a. m.' : 'p. m.';
        
        return `${fechaFormateada} ${turno}`;
      }
    }
    
    return fechaStr;
  }

  /**
   * Descarga un ZIP
   */
  descargarZip(url: string): void {
    if (!url) {
      console.warn('URL de descarga vacía');
      return;
    }

    this.loadingService.show();
    this.http.get(url, { responseType: 'blob', observe: 'response' }).subscribe({
      next: (response) => {
        let fileName = 'archivo.zip';
        const contentDisposition = response.headers.get('content-disposition');
        if (contentDisposition && contentDisposition.indexOf('filename=') !== -1) {
          fileName = contentDisposition.split('filename=')[1].trim().replace(/['"]/g, '');
        }

        const blob = response.body as Blob;
        const blobUrl = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = blobUrl;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();

        window.URL.revokeObjectURL(blobUrl);
        document.body.removeChild(a);

        this.loadingService.hide();
      },
      error: (error) => {
        console.error('Error al descargar ZIP:', error);
        this.loadingService.hide();
        this.dialogService.mostrarMensajeError('No se pudo descargar el archivo.');
      }
    });
  }

  convertirFechaBackend(fecha: string | null): string | null {
    if (!fecha) return null;

    const partes = fecha.match(/(\d{2}\/\d{2}\/\d{4})\s+(\d{2}:\d{2}:\d{2})\s+([ap])\.\s*m\./);
    if (!partes) {
      console.error('Formato de fecha inválido:', fecha);
      return null;
    }

    const [, fechaStr, horaStr, indicador] = partes;
    const [h, m, s] = horaStr.split(':').map(Number);
    
    let hora24 = h;
    if (indicador === 'p' && h !== 12) hora24 = h + 12;
    if (indicador === 'a' && h === 12) hora24 = 0;
    
    const fechaCompleta = `${fechaStr} ${hora24.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    const fechaParsed = dayjs(fechaCompleta, 'DD/MM/YYYY HH:mm:ss', true);
    
    if (!fechaParsed.isValid()) {
      console.error('Error al parsear fecha:', fechaCompleta);
      return null;
    }
    
    return fechaParsed.toISOString();
  }
}
