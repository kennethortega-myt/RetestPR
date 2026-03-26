package pe.gob.onpe.consultaopbackend.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FtpUtils {

	static Logger logger = LoggerFactory.getLogger(FtpUtils.class);
	
	public static String getFileExtension(String name) {
	    int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
		    return null; // empty extension
		}
		return name.substring(lastIndexOf+1);
	}
	
	public static InputStream convertToInputStream(String filePath) throws IOException {
	    try (FileInputStream fis = new FileInputStream(filePath);
	         ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

	        byte[] buf = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = fis.read(buf)) != -1) {
	            bos.write(buf, 0, bytesRead);
	        }

	        byte[] imageBytes = bos.toByteArray();
	        return new ByteArrayInputStream(imageBytes);
	    }
	}
	
	public static byte[] convertToByte(InputStream inputStream) throws IOException {
		byte[] bytes = IOUtils.toByteArray(inputStream);
		return bytes;
	}
	
	public static InputStream convertToInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }
	

}