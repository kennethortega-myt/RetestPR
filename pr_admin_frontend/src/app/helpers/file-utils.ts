// File related helpers
export function descargarArchivoPdf(pdfSrc: any) {
  // kept original Spanish-named function for backward compatibility
  let currentDateTime = new Date().toISOString().replace(/[:.-]/g, '');
  const src = `${pdfSrc}`;
  const link = document.createElement('a');
  link.href = src;
  link.download = currentDateTime;
  link.target = '_blank';
  link.click();

  link.remove();
}

export function downloadPdf(pdfSrc: any) {
  // English alias
  descargarArchivoPdf(pdfSrc);
}
