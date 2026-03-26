export interface IUbigeoSelectedOptions {
    electoralRegionUbigeoId?: string; // ID del region
    electoralRegionUbigeoText?: string; // Nombre del region

    electoralRevocaUbigeoId?: string; // ID del region
    electoralRevocaUbigeoText?: string; // Nombre del region

    regionUbigeoId?: string; // ID del region    
    regionUbigeoText?: string; // Nombre del region

    departmentUbigeoId?: string; // ID del departamento (Región en Perú)
    departmentUbigeoText?: string; // Nombre del departamento

    provinceUbigeoId?: string; // ID de la provincia
    provinceUbigeoText?: string; // Nombre de la provincia

    districtUbigeoId?: string; // ID del distrito
    districtUbigeoText?: string; // Nombre del distrito

    locationId?: string; // ID del local de votación
    locationText?: string; // Nombre del local de votación

    continentId?: string; // ID del continente (para ubicaciones internacionales)
    continentText?: string; // Nombre del continente

    countryId?: string; // ID del país
    countryText?: string; // Nombre del país

    stateId?: string; // ID del estado o región internacional
    stateText?: string; // Nombre del estado o región
}