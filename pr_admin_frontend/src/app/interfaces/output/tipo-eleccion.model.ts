import { Base } from "./base.model";

export class TipoEleccion extends Base {
  value!: number;
  text?: string;
  descripcion?: string;
  icono?: string;
  exito?: boolean;
}
