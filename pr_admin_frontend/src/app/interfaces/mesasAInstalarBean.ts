import { MesasAInstalarDetalleBean } from './mesasAInstalarDetalleBean';

export interface MesasAInstalarBean {
  titulo: string;
  cantidad: number;
  detalle: Array<MesasAInstalarDetalleBean>;
}
