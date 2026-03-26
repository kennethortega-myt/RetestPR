export interface IModalDescargaData {
  message?: string;
}

export type IDownloadStatusType = 'init_download' | 'in_progress';

export const MODAL_MESSGAGE: { [key in IDownloadStatusType]: string } = {
  init_download: `Tu archivo se está generando en segundo plano. Pronto podrás visualizarlo en el módulo <strong>mis reportes.</strong>`,
  in_progress: `Ya existe un reporte en proceso de generación. Espere a que finalice para poder generar uno nuevo.`
};

/* Nuevo */
export class ModalComponent {
  htmlMessage: string | null = null; // Mensaje que será mostrado
  showAdvertencia: boolean = false; // Controla la visibilidad del div advertencia

  constructor() {
    const downloadStatus: IDownloadStatusType = this.getDownloadStatus(); // Simula obtener el estado actual de la descarga

    // Asigna el mensaje correspondiente según el estado
    this.htmlMessage = MODAL_MESSGAGE[downloadStatus];

    // Si el estado es 'init_download', mostramos la advertencia y ocultamos el iframe
    this.showAdvertencia = downloadStatus === 'init_download';
  }

  // Simulación de obtener el estado actual de la descarga
  getDownloadStatus(): IDownloadStatusType {
    // Aquí debes poner la lógica para obtener el estado real
    return 'in_progress'; // O 'init_download', según corresponda
  }
}
