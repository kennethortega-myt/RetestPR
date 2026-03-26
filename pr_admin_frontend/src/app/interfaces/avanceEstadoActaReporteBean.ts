import { EncabezadoFiltroAvanceEstadoMesaBean } from './encabezadoFiltroAvanceEstadoMesaBean';
import { AvanceEstadoMesaResumenBean } from './avanceEstadoMesaResumenBean';
import { AvanceEstadoActaResumenBean } from './avanceEstadoActaResumenBean';

export interface AvanceEstadoActaReporteBean {
  encabezado: EncabezadoFiltroAvanceEstadoMesaBean;
  resumen: AvanceEstadoActaResumenBean;
  detalleAvanceEstadoMesa: Array<AvanceEstadoMesaResumenBean>;
}
