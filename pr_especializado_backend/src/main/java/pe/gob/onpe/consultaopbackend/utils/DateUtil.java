package pe.gob.onpe.consultaopbackend.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

	private DateUtil() {
		// Private constructor to hide the implicit public one
	}

	public static final Locale LOCALE_PE = new Locale("es", "PE");

	public static String getDateString(Date date, String formato) {
    	String strDate = null;
    	if(date!=null) {
    		DateFormat dateFormat = new SimpleDateFormat(formato);  
        	strDate = dateFormat.format(date);
    	}
    	return strDate;
    }
	
	public static String getFechaActual(String formato) {
    	String strDate = null;
    	DateFormat dateFormat = new SimpleDateFormat(formato);  
        strDate = dateFormat.format(new Date());
    	return strDate;
    }

    public static Date getDate(String dateString, String formato) {
        if (dateString == null || formato == null || formato.isBlank()) {
            log.warn("⚠️ Fecha o formato inválido. dateString={}, formato={}", dateString, formato);
            return null;
        }

        try {
            return new SimpleDateFormat(formato, Locale.ENGLISH).parse(dateString);
        } catch (ParseException e) {
            log.error("❌ Error al parsear fecha. Valor: {}, Formato: {}", dateString, formato);
            return null;
        }
    }

	public static String dateFormatter(String inputFormat, String outputFormat, String inputDate){
		String input = inputFormat.isEmpty()? "yyyy-MM-dd hh:mm:ss" : inputFormat;
		String output = outputFormat.isEmpty()? "dd 'de' MMMM 'de' yyyy" : outputFormat;
		String outputDate = inputDate;
		try {
			outputDate = new SimpleDateFormat(output, LOCALE_PE).format(new SimpleDateFormat(input, LOCALE_PE).parse(inputDate));
		} catch (Exception e) {
			log.error("dateFormatter(): " + e.getMessage());
		}
		return outputDate;
	}

	public static Date sumarHoras(Date fecha, int horas) {

		Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.HOUR_OF_DAY, horas);
		return calendar.getTime();
	}
}
