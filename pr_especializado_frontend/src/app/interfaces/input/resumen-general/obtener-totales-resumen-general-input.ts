import { BaseResumenGeneralInput } from './base-resumen-general-input';

export class ObtenerTotalesResumenGeneralInput extends BaseResumenGeneralInput {
  idDistritoElectoral?: number | null;
  idUbigeoDepartamento?: string | null;
  idUbigeoDistrito?: string | null;
  idUbigeoProvincia?: string | null;
}
