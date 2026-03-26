import * as am5 from "@amcharts/amcharts5";
import { MapaCalor } from "../interfaces/resumen-general-bean";
import { getDegradatedColorFromPercentage } from "../components/main-hot-map/map-auxiliary-elements";
import { MULTIPLIER_HOT_MAP, MULTIPLIER_HOT_MAP_REVOCA } from "../settings/map.settings";
import { isRevocatoria } from "./storage-helpers/encrypt-storage.helper";

const INITIAL_COLOR_PERCENTAGE_0 = "#DFE5EB";
const FINAL_COLOR_PERCENTAGE_100 = "#295789";

export function settingCustomData(tmp: MapaCalor, data: any[], geodata: any, i: number) {
  const { value, color } = getValuesForData(tmp);
  data.push({
    id: geodata.features[i].id,
    value: value,
    polygonSettings: {
      fill: am5.color(color),
    },
  });
}

function getValuesForData(tmp?: MapaCalor): { value: number; color: string } {
  if (tmp) {
    const MULTIPLIER =  isRevocatoria() ? MULTIPLIER_HOT_MAP_REVOCA : MULTIPLIER_HOT_MAP
    const porcentajeCalculado = Number(tmp.porcentajeActasContabilizadas) * MULTIPLIER;
    const currentPercentage = porcentajeCalculado > 100.0 ? 100.0 : porcentajeCalculado;
    const currentColor = getDegradatedColorFromPercentage(currentPercentage, {
      init: INITIAL_COLOR_PERCENTAGE_0,
      end: FINAL_COLOR_PERCENTAGE_100,
    });
    return { value: tmp.porcentajeActasContabilizadas, color: currentColor };
  }
  return { value: 0, color: INITIAL_COLOR_PERCENTAGE_0 };
}
