import { HttpClient, HttpHeaders, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable, switchMap, take } from "rxjs";
import { IBodyToParams, transformBodyToParams } from "../../helpers/transformBodyParams";
import { BlobConUrl, BlobWithFilename } from "../../components/actas-components/modal-visor-pdf/modal-visor-pdf.interface";

@Injectable({
  providedIn: "root",
})
export class RequestsService {
  constructor(private httpClient: HttpClient) { }

  public get<T>(url: string, headers?: HttpHeaders): Observable<HttpResponse<T>> {
    return this.httpClient.get<T>(url, {
      observe: "response",
      headers: headers,
    });
  }

  public getTake<T>(url: string, headers?: HttpHeaders, params?: any): Observable<HttpResponse<T>> {
    return this.httpClient.get<T>(url, {
      observe: "response",
      headers: headers,
      params: params
    })
      .pipe(
        take(1)
      );
  }

  public getImage(url: string): Observable<Blob> {
    return this.httpClient.get(url, { responseType: "blob" }).pipe(switchMap((response) => this.readFile(response)));
  }

  public getArchivo(url: string, tipoArchivo): Observable<BlobWithFilename> {
    return this.httpClient.get(url, {
      headers: {
        Accept: tipoArchivo
      },
      responseType: "blob",
      observe: "response"
    }).pipe(
      map((response: HttpResponse<Blob>) => {
        const contentDisposition = response.headers.get('Content-Disposition');
        const filename = this.extractFilename(contentDisposition);
        const blob = response.body!;
        const fixedBlob = blob.type ? blob : new Blob([blob], { type: tipoArchivo });
        const blobUrl = URL.createObjectURL(fixedBlob);
        return {
          ...fixedBlob,
          url: blobUrl,
          filename: filename
        } as BlobWithFilename;
      })
    );
  }

  public getPdf(url: string): Observable<Blob> {
    return this.httpClient
      .get(url, {
        headers: {
          Accept: "application/pdf",
        },
        responseType: "blob",
      })
      .pipe(switchMap((response) => this.readFile(response)));
  }


  public postPDFReport(url: string, body: object): Observable<Blob> {
    return this.httpClient.post(url, body, {
      headers: { Accept: "application/pdf" },
      responseType: "blob",
    });
  }

  public postPDFHttpRequest(url: string, body: object): Observable<HttpResponse<Blob>> {
    return this.httpClient.post(url, body, {
      observe: "response",
      headers: { Accept: "application/pdf" },
      responseType: "blob",
    });
  }

  public postPdf(url: string, body: any): Observable<Blob> {
    return this.httpClient
      .post(url, body, {
        headers: {
          Accept: "application/pdf",
        },
        responseType: "blob",
      })
      .pipe(switchMap((response) => this.readFile(response)));
  }

  public post<T>(url: string, body: IBodyToParams, headers?: HttpHeaders): Observable<HttpResponse<T>> {
    let httpParams = transformBodyToParams(body);
    return this.httpClient.get<T>(url, {
      observe: "response",
      headers: headers,
      params: httpParams,
    });
  }

  public postBodyWithParams<T>(
    url: string, 
    body: object, 
    headers?: HttpHeaders, 
    params?: any
  ): Observable<HttpResponse<T>> {
    return this.httpClient.post<T>(url, body, {
      observe: "response",
      headers: headers,
      params: params
    });
  }

  private extractFilename(contentDisposition: string | null): string {
    if (!contentDisposition) return '';
    const filenameMatch = contentDisposition.match(/filename="([^"]+)"/);
    
    if (filenameMatch) {
      return filenameMatch[1];
    }
    const fallbackMatch = contentDisposition.match(/filename=([^;]+)/);
    return fallbackMatch ? fallbackMatch[1].replace(/['"]/g, '').trim() : '';
  }

  private readFile(blob: Blob): Observable<Blob> {
  return new Observable((observer) => {
    const fixedBlob = blob.type
      ? blob
      : new Blob([blob], { type: 'application/pdf' });

    const blobUrl = URL.createObjectURL(fixedBlob);
    
    const blobConUrl = fixedBlob as BlobConUrl;
    blobConUrl.url = blobUrl;

    observer.next(blobConUrl);
    observer.complete();
  });
}

  private readCsv(blob: Blob): Observable<BlobConUrl | null> {
    return new Observable((observer) => {
      const fixedBlob = blob.type
        ? blob
        : new Blob([blob], { type: 'text/csv' });

      const blobUrl = URL.createObjectURL(fixedBlob);

      const blobConUrl: BlobConUrl = { ...fixedBlob, url: blobUrl };

      observer.next(blobConUrl);
      observer.complete();
    });
  }
}
