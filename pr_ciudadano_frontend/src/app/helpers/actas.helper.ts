import { FilterByLocationParams } from '../interfaces/filtro-settings';

/**
 * Analiza un código ubigeo de 6 dígitos y retorna los códigos formateados por nivel existente.
 * @param value Código ubigeo en formato "000000"
 * @returns Objeto con el tipo y códigos ubigeo formateados a 6 dígitos solo para niveles existentes
 */
export const generateFilterByLocationParams = (value: string): FilterByLocationParams => {
  // Formato esperado: "000000"
  // Ejemplos:
  // '020000' -> { tipo: 'departamento', departamento: '020000' }
  // '021200' -> { tipo: 'provincia', departamento: '020000', provincia: '021200' }
  // '021210' -> { tipo: 'distrito', departamento: '020000', provincia: '021200', distrito: '021210' }

  if (!value || typeof value !== 'string' || value.length !== 6 || !/^\d{6}$/.test(value)) {
    return {
      departmentUbigeoId: '000000'
    };
  }
  const depCode = value.substring(0, 2);
  const provCode = value.substring(2, 4);
  const distCode = value.substring(4, 6);
  const departmentUbigeoId = depCode + '0000';
  const provinceUbigeoId = depCode + provCode + '00';
  const districtUbigeoId = value;

  if (distCode !== '00') {
    return { departmentUbigeoId, provinceUbigeoId, districtUbigeoId };
  } else if (provCode !== '00') {
    return { departmentUbigeoId, provinceUbigeoId };
  } else {
    return { departmentUbigeoId };
  }
};
