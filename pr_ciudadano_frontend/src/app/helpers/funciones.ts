import { DatePipe } from "@angular/common";

export function descargarPdf(prefijoNombreArchivo: string, result: Blob) {
  let currentDateTime = new DatePipe("en-US").transform(new Date(), "dd_MM_yyyy_hmmss");
  const src = URL.createObjectURL(result); // Crear una URL válida desde el Blob
  const link = document.createElement("a");
  link.href = src;
  link.download = prefijoNombreArchivo + "_" + currentDateTime.toString();
  link.target = "_blank";
  link.click();

  link.remove();
  URL.revokeObjectURL(src); // Liberar el objeto URL después de usarlo
}
