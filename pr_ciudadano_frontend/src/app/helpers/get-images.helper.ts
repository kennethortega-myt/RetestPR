import { getEncryptStorageEleccionValue } from "./encrypt-storage-eleccion";

const assets = "/assets";

export function getIsEnabledRealDirectoryForImages(): boolean {
  const activoFechaProceso = getEncryptStorageEleccionValue("ACTIVO_FECHA_PROCESO") === "true" ? true : false;
  return activoFechaProceso;
}

export function getCandidateImageFromAssets(dniCandidato: string, directory: string = "candidatos") {
  const url_candidate_image_fake = `${assets}/img/${directory}/${dniCandidato}.jpg`;
  const url_candidate_image_real = `${assets}/img-reales/${directory}/${dniCandidato}.jpg`; // THIS VALUE IS BEEN EVALUATED
  const isEnabledRealDirectoryForImages = getIsEnabledRealDirectoryForImages();
  const url_candidate_image = isEnabledRealDirectoryForImages ? url_candidate_image_real : url_candidate_image_fake;
  return url_candidate_image;
}

export function getPoliticImageFromAssets(codigoAgrupacionPolitica: string) {
  const agCodigo = codigoAgrupacionPolitica?.toString().padStart(8, "0");
  const urlAgrupacionImage_fake = `${assets}/img/partidos/${agCodigo}.jpg`;
  const urlAgrupacionImage_real = `${assets}/img-reales/partidos/${agCodigo}.jpg`; // THIS VALUE IS BEEN EVALUATED
  const isEnabledRealDirectoryForImages = getIsEnabledRealDirectoryForImages();
  const urlAgrupacionImage = isEnabledRealDirectoryForImages ? urlAgrupacionImage_real : urlAgrupacionImage_fake;
  return urlAgrupacionImage;
}

/**
 * @param dni should be 8 digits
 * @returns
 */
export function getAutoridadesRevocatoriaImageFromAssets(dni: string) {
  const urlAgrupacionImage_fake = `${assets}/img/autoridades-revocatoria/${dni}.jpg`;
  const urlAgrupacionImage_real = `${assets}/img-reales/autoridades-revocatoria/${dni}.jpg`; // THIS VALUE IS BEEN EVALUATED
  const isEnabledRealDirectoryForImages = getIsEnabledRealDirectoryForImages();
  const urlAgrupacionImage = isEnabledRealDirectoryForImages ? urlAgrupacionImage_real : urlAgrupacionImage_fake;
  return urlAgrupacionImage;
}

export function mapWithPoliticImage<
  T extends {
    codigoAgrupacionPolitica?: number | string;
    code_of_politic_group?: number | string;
    ccodigo?: number | string;
    dniCandidato?: string;
  }
>(
  list: T[]
): (T & { urlAgrupacionImage: string })[] {
  return list.map(el => {
    const codigo =
      el.codigoAgrupacionPolitica ?? el.code_of_politic_group ?? el.ccodigo;

    return {
      ...el,
      urlAgrupacionImage: codigo
        ? getPoliticImageFromAssets(
            codigo.toString().padStart(8, '0')
          )
        : '',
      urlCandidateImage: getCandidateImageFromAssets(el.dniCandidato)
    };
  });
}
