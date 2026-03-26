package pe.gob.onpe.consultaopbackend.utils;

public class ArchivoUtils {

    private ArchivoUtils() {
        // Private constructor to hide the implicit public one
    }

	 public static String formatBytes(long bytes) {
	        int unit = 1024;
	        if (bytes < unit) {
	            return bytes + " B";
	        }
	        int exp = (int) (Math.log(bytes) / Math.log(unit));
	        String pre = "KMGTPE".charAt(exp - 1) + "";
	        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	 }

     /** Extrae el nombre del archivo sin la parte de la fecha y hora
      *  Ejemplo: input = "reporte_20231010_153000.pdf"
      *           output = "reporte.pdf"
      * @param input Nombre del archivo con fecha y hora
      * @return Nombre del archivo sin la parte de la fecha y hora
      */
    public static String extractFileName(String input) {
        // Encuentra la posición del último guión bajo antes de la fecha
        int lastUnderscoreBeforeDate = input.lastIndexOf("_", input.lastIndexOf("_") - 1);

        // Extrae la parte antes de la fecha
        String baseName = input.substring(0, lastUnderscoreBeforeDate);

        // Obtiene la extensión
        String extension = input.substring(input.lastIndexOf("."));

        return baseName.trim().replaceAll("\\s", "") + extension;
    }


}
