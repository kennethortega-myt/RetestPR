import { take } from 'rxjs';
import { Usuario } from '../interfaces/login';
import { PotiticGroupsService } from '../services/potitic-groups.service';

export function settingAppElements(
  element: Element | null,
  element2: Element | null,
  element3: Element | null,
  backgrounds: string[],
  conta_paso: number
) {
  if (element) {
    for (let index = 0; index < backgrounds.length; index++) {
      element.classList.toggle(backgrounds[index], index + 1 === conta_paso);
    }
  }

  if (element2) {
    for (let index = 0; index < backgrounds.length; index++) {
      element2.classList.toggle(backgrounds[index], index + 1 === conta_paso);
    }
  }

  if (element3) {
    for (let index = 0; index < backgrounds.length; index++) {
      element3.classList.toggle(backgrounds[index], index + 1 === conta_paso);
    }
  }
}

export function loadPoliticGroupDescriptionHelper(
  usuarioLogin: Usuario | null | undefined,
  potiticGroupsService: PotiticGroupsService,
  setLabel: (label: string) => void,
  context: string
): void {
  const usuario = usuarioLogin ?? ({} as Usuario);
  const { codigoOp, nombres = '', apellidoPaterno = '', apellidoMaterno = '' } = usuario;

  if (codigoOp) {
    potiticGroupsService
      .getPoliticGroupDescription$({ codigoOP: codigoOp })
      .pipe(take(1))
      .subscribe({
        next: (response) => {
          if (response.success) {
            setLabel(response.data?.descripcion ?? 'Agrupación política');
          } else {
            console.error(`[${context}] Error al obtener descripción del grupo político.`);
          }
        },
        error: (err) => console.error(`[${context}] Error en servicio:`, err),
      });
  } else {
    setLabel(`${nombres} ${apellidoPaterno} ${apellidoMaterno}`.trim());
  }
}
