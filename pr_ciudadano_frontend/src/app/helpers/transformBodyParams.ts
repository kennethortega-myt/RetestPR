import { HttpParams } from "@angular/common/http";

export interface IBodyToParams {
  [key: string]: string | number | boolean | undefined;
}

export function transformBodyToParams(body: IBodyToParams) {
  let httpParams = new HttpParams();
  if (body) {
    const entries = Object.entries(body);
    entries.forEach(([key, value]) => {
      if (value || (typeof value !== "undefined" && value !== null && value !== "" && value !== 0)) {
        httpParams = httpParams.set(key, value);
      }
    });
  }
  return httpParams;
}
