import { Routes } from "@angular/router";
import { PATHS } from "../../../settings/app.routes.settings";
import { ResumenGeneralComponent } from "./resumen-general/resumen-general.component";
import { PresidencialesComponent } from "./presidenciales/presidenciales.component";
import { DiputadosComponent } from "./diputados/diputados.component";
import { SenadoresDistritoElectoralMultipleComponent } from "./senadores-distrito-electoral-multiple/senadores-distrito-electoral-multiple.component";
import { SenadoresDistritoNacionalUnicoComponent } from "./senadores-distrito-nacional-unico/senadores-distrito-nacional-unico.component";
import { ParticipacionCiudadanaComponent } from "./participacion-ciudadana/participacion-ciudadana.component";
import { ParlamentoAndinoComponent } from "./parlamento-andino/parlamento-andino.component";
import { ActasComponent } from "./actas/actas.component";
import { ReportesAutomaticosComponent } from "./reportes-automaticos/reportes-automaticos.component";
import { PreguntasFrecuentesComponent } from "./preguntas-frecuentes/preguntas-frecuentes.component";
import { SitemapComponent } from "./sitemap/sitemap.component";

export const eleccionesGeneralesRoutes: Routes = [
  {
    path: PATHS.resumen_general,
    component: ResumenGeneralComponent,
  },
  {
    path: PATHS.presidenciales,
    component: PresidencialesComponent,
  },
  {
    path: PATHS.diputados,
    component: DiputadosComponent,
  },
  {
    path: PATHS.senadores_27,
    component: SenadoresDistritoElectoralMultipleComponent,
  },
  {
    path: PATHS.senadores_33,
    component: SenadoresDistritoNacionalUnicoComponent,
  },
  {
    path: PATHS.participacion_ciudadana,
    component: ParticipacionCiudadanaComponent,
  },
  {
    path: PATHS.parlamento_andino,
    component: ParlamentoAndinoComponent,
  },
  {
    path: PATHS.actas,
    component: ActasComponent,
  },
  {
    path: PATHS.reportes_automaticos,
    component: ReportesAutomaticosComponent,
  },
  {
    path: PATHS.faq,
    component: PreguntasFrecuentesComponent,
  },
  {
    path: PATHS.sitemap,
    component: SitemapComponent,
  },
];
