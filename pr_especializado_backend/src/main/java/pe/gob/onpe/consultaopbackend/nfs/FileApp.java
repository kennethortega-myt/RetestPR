package pe.gob.onpe.consultaopbackend.nfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.consultaopbackend.model.enums.ExtensionEnum;
import pe.gob.onpe.consultaopbackend.utils.FtpUtils;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class FileApp {

	static Logger logger = LoggerFactory.getLogger(FileApp.class);
	
	private final MultipartFile part;
	private final String filename;
	private final String extension;
	private final byte[] file;
	private final Long size;
	private String prefix = "";
	private final Map<String, String> filenameCode = new HashMap<>();
	private static Map<String, ExtensionEnum> extperm = null;
	
	static {
		extperm = new HashMap<>();
		extperm.put(ExtensionEnum.PDF.getExtension().toUpperCase(), ExtensionEnum.PDF);
		extperm.put(ExtensionEnum.JPEG.getExtension().toUpperCase(), ExtensionEnum.JPEG);
		extperm.put(ExtensionEnum.JPG.getExtension().toUpperCase(), ExtensionEnum.JPG);
		extperm.put(ExtensionEnum.RAR.getExtension().toUpperCase(), ExtensionEnum.RAR);
		extperm.put(ExtensionEnum.ZIP.getExtension().toUpperCase(), ExtensionEnum.ZIP);
		extperm.put(ExtensionEnum.PNG.getExtension().toUpperCase(), ExtensionEnum.PNG);
		extperm.put(ExtensionEnum.DOC.getExtension().toUpperCase(), ExtensionEnum.DOC);
		extperm.put(ExtensionEnum.DOCX.getExtension().toUpperCase(), ExtensionEnum.DOCX);
		extperm.put(ExtensionEnum.XLS.getExtension().toUpperCase(), ExtensionEnum.XLS);
		extperm.put(ExtensionEnum.XLSX.getExtension().toUpperCase(), ExtensionEnum.XLSX);
	}

	public FileApp() {
		this.part = null;
		this.filename = null;
		this.file = null;
		this.extension = null;
		this.size = null;
	}
	
	
	public FileApp(byte[] is, String filename) {
		this.part = null;
		this.file = is;
		this.filename = filename;
		this.extension = FtpUtils.getFileExtension(this.filename);
		this.size = (long) this.file.length;
	}

	public MultipartFile getPart() {
		return part;
	}

	public String getFilename() {
		return filename;
	}

	public byte[] getFile() {
		return file;
	}

	public String getExtension() {
		return extension;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Long getSize() {
		return size;
	}
	
	public Map<String, String> getFilenameCode() {
		return filenameCode;
	}

	public String getTypeMime() {
		if(this.extension!=null && !this.extension.trim().isEmpty()) {
			ExtensionEnum extEnum = extperm.get(this.extension.toUpperCase());
			if(extEnum==null) {
				return "application/octet-stream";
			}
			return extEnum.getTypemime();
		} else {
			return "application/octet-stream";
		}
		
	}
	
	public String getBase64() {
		return Base64.getEncoder().encodeToString(this.file);
    }
	
}