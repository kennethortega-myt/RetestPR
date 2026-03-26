package pe.gob.onpe.presentacionbackend.utils;

import java.text.DecimalFormat;

public class NumberUtils {

	private NumberUtils() {
		throw new IllegalStateException("NumberUtils class");
	}
	public static String getTresdecimales(double number) {
		DecimalFormat intFormat = new DecimalFormat("#,##0");
    	return intFormat.format(number);
	}

	/**
	 * Retorna texto, número formateado a 3 decimales, si es 0 o 100 retorna el número entero
	 * @param number
	 * @return 0%, 100% o número 000%
	 */
	public static String getTresdecimalesPorcentaje(double number) {
		DecimalFormat decimalFormat = null; 
		if(number == 0 || number == 100) {
			decimalFormat = new DecimalFormat("#,##0");
        } else {
        	decimalFormat = new DecimalFormat("#,##0.000");
        }
		return String.format("%s%s", decimalFormat.format(number),"%");
	}
}
