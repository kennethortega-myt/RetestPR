export interface UbigeoNacionDto{
  departamentos: Array<UbigeoDTO>;
  provincias: Array<UbigeoDTO>;
  distritos: Array<UbigeoDTO>;
}

export interface UbigeoDTO{
   	id:number;
   	idPadre:number;
   	codigo: string;
  	nombre:string;
    nivel?:number;
    idEleccion?:number;
}


