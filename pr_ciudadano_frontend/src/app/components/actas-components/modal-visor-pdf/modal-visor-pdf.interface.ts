import { Archivo } from "../../../interfaces/acta-bean";

export interface IModalVisorConfig {
  file?: any;
  numeroDeActa?: string;
  nombreDeActa?: string;
  multiple: boolean;
  archivos?: Archivo[];
  esPantallaChica?: boolean;
  hideInstructions?: boolean;
}
export interface BlobConUrl extends Blob {
  url?: string;
}

export interface BlobWithFilename extends Blob {
  url: string;
  filename: string;
}
