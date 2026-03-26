/*
  This file remains as a compatibility facade to avoid changing many imports
  across the codebase. New implementations live in separate English-named
  files under `src/app/helpers/` (color-utils, paginator-utils, date-utils,
  file-utils, string-utils). We re-export here the original function names so
  existing imports continue to work.
*/

export * from './color-utils';
export * from './paginator-utils';
export * from './date-utils';
export * from './string-utils';

// Backwards-compatible alias for obtenerDatos (keeps original API)
import { Base } from '../interfaces/output/base.model';
export function obtenerDatos(param: Base): Base | null {
  if (param == null) {
    let result: Base = new Base();
    result.success = false;
    result.message = '';
    return result;
  }
  let result = param;
  result.data = param.data ?? null;
  return result;
}
