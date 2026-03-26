import { BaseResumenGeneralInput } from './base-resumen-general-input';

export class ObtenerMapaCalorResumenGeneralInput extends BaseResumenGeneralInput {
  codigoAgrupacionPolitica?: number;
  idDistritoElectoral?: number | null;
}
