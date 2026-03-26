import { RecovatoriaResumenData, RevocatoriaResumenResponseData } from "../../interfaces/revocatoria.interfaces";

export function getMapperRevocatoriaResumen(resumen: RevocatoriaResumenResponseData): RecovatoriaResumenData {
  return {
    total: resumen.total ?? 0,
    totalAlcaldesDistritales: resumen.totalAlcaldes ?? 0,
    totalRegidoresDistritales: resumen.totalRegidores ?? 0,
    totalAlcaldesProvinciales: 0,
    totalConsejeros: 0,
    totalGobernadores: 0,
    totalRegidoresProvinciales: 0,
  } as RecovatoriaResumenData;
}
