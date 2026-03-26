package pe.gob.onpe.pradminbackend.model.enums;

public enum ApikeyStatusEnum {
	
    INACTIVO(0),
    ACTIVO(1),
    BAJA(2);

    private final int value;

    ApikeyStatusEnum(final int value) {
        this.value = value;
    }

	public int getValue() {
		return value;
	}
    
    
}
