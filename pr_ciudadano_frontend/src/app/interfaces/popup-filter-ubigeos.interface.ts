import { IBaseFiltroUbigeo } from "./elections.interfaces";
import { FilterFunctionality, GenericFilterParams } from "./filtro-settings";
import { PopupFilterUbigeosService } from "../services/common/popup-filter-ubigeos.service";

export interface IDataForPopup {
  targetId: string;
  electionId: number;
  showLocales: boolean;
  showSelectLocationUbigeo: boolean;
  filterFunctionality: FilterFunctionality;
  popupInformationInstance: IBaseFiltroUbigeo;
  responsiveUbigeoParams: GenericFilterParams;
  service: PopupFilterUbigeosService;
  breadcrumbString?: string;
}
