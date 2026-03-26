package pe.gob.onpe.pradminbackend.utils;


import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Slf4j
public class DateUtil {

	private DateUtil() {
		throw new IllegalStateException("DateUtil class");
	}
	public static final Locale LOCALE_PE = new Locale("es", "PE");

    public static Date sumarHoras(Date fecha, int horas) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.HOUR_OF_DAY, horas);
        return calendar.getTime();
    }

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
    
    public static Date getDate(String dateString, String formato){
    	Date date = null;
    	if(dateString!=null) {
    		try {
				date=new SimpleDateFormat(formato).parse(dateString);
			} catch (ParseException e) {
				log.error("getDate", e);
			}  
    	}
    	return date;
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

    /**
     * Convierte un String a LocalDateTime con un formato específico
     *
     * @param dateTimeString String a convertir
     * @param pattern Patrón de formato (ej: "yyyy-MM-dd HH:mm:ss")
     * @return LocalDateTime convertido
     * @throws IllegalArgumentException si no puede convertir
     */
    public static LocalDateTime stringToLocalDateTime(String dateTimeString, String pattern) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha y hora no pueden estar vacías");
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("El patrón no puede estar vacío");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateTimeString.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "No se pudo convertir '" + dateTimeString + "' a LocalDateTime con patrón '" + pattern + "': " + e.getMessage()
            );
        }
    }

    /**
     * Convierte un String a LocalDate con un formato específico
     *
     * @param dateString String a convertir
     * @param pattern Patrón de formato (ej: "yyyy-MM-dd")
     * @return LocalDate convertido
     * @throws IllegalArgumentException si no puede convertir
     */
    public static LocalDate stringToLocalDate(String dateString, String pattern) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha no puede estar vacía");
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("El patrón no puede estar vacío");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateString.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "No se pudo convertir: '" + dateString + "' a LocalDate con patrón '" + pattern + "': " + e.getMessage()
            );
        }
    }

    /**
     * Convierte un String a LocalTime con un formato específico
     *
     * @param timeString String a convertir
     * @param pattern Patrón de formato (ej: "HH:mm:ss")
     * @return LocalTime convertido
     * @throws IllegalArgumentException si no puede convertir
     */
    public static LocalTime stringToLocalTime(String timeString, String pattern) {
        if (timeString == null || timeString.trim().isEmpty()) {
            throw new IllegalArgumentException("La hora no puede estar vacía");
        }
        if (pattern == null || pattern.trim().isEmpty()) {
            throw new IllegalArgumentException("El patrón no puede estar vacío.");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalTime.parse(timeString.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "No se pudo convertir '" + timeString + "' a LocalTime con patrón '" + pattern + "': " + e.getMessage()
            );
        }
    }
}
