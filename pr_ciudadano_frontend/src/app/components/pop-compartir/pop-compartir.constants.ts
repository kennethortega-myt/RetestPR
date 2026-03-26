import { URL_PATHS_TO_REDIRECT } from "../../settings/app.routes.settings";
import { TranslateModule } from '@ngx-translate/core';

export const IFRAMES = {
  presidenciales: `
    <iframe id="resultadosOnpe-presidenciales" title="Presentación de resultados
    electorales" width="100%" height="auto"
    src="${window.location.hostname}/#${URL_PATHS_TO_REDIRECT.presidenciales}">
    </iframe>
  `,
  diputados: `
    <iframe id="resultadosOnpe-diputados" title="Presentación de resultados
    electorales" width="100%" height="auto"
    src="${window.location.hostname}/#${URL_PATHS_TO_REDIRECT.diputados}">
    </iframe>
  `,
  parlamento_andino: `
    <iframe id="resultadosOnpe-parlamento" title="Presentación de resultados
    electorales" width="100%" height="auto"
    src="${window.location.hostname}/#${URL_PATHS_TO_REDIRECT.parlamento_andino}">
    </iframe>
  `,
  distrito_electoral_multiple: `
    <iframe id="resultadosOnpe-distrito-electoral-multiple" title="Presentación de resultados
    electorales" width="100%" height="auto"
    src="${window.location.hostname}/#${URL_PATHS_TO_REDIRECT.distrito_electoral_multiple}">
    </iframe>
  `,
  distrito_electoral_unico: `
    <iframe id="resultadosOnpe-distrito-electoral-unico" title="Presentación de resultados
    electorales" width="100%" height="auto"
    src="${window.location.hostname}/#${URL_PATHS_TO_REDIRECT.distrito_electoral_unico}">
    </iframe>
  `,
};
