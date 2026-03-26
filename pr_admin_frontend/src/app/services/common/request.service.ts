import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RequestsService {
  constructor(private httpClient: HttpClient) {}

  public get<T>(
    url: string,
    headers?: HttpHeaders
  ): Observable<HttpResponse<T>> {
    return this.httpClient.get<T>(url, {
      observe: 'response',
      headers: headers,
    });
  }

  public post<T>(
    url: string,
    body: object,
    headers?: HttpHeaders,
    queryParams?: { [key: string]: string | number }
  ): Observable<HttpResponse<T>> {
    return this.httpClient.post<T>(url, body, {
      observe: 'response',
      headers: headers,
      params: queryParams,
    });
  }

  public postBlob(
    url: string,
    body: object,
    headers?: HttpHeaders
  ): Observable<HttpResponse<Blob>> {
    const defaultHeaders = { Accept: 'application/pdf' };
    return this.httpClient.post(url, body, {
      observe: 'response',
      headers: headers ? headers : defaultHeaders,
      responseType: 'blob',
    });
  }


}
