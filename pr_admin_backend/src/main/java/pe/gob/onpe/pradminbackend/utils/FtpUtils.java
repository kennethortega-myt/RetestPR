package pe.gob.onpe.pradminbackend.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class FtpUtils {

	private FtpUtils() {
		throw new IllegalStateException("FtpUtils class");
	}

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
		return  IOUtils.toByteArray(inputStream);

	}
	
	public static InputStream convertToInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }
	

}