import { Injectable } from "@angular/core";
import { Department } from "../../interfaces/elections.interfaces";

const ubigeoStoreKeys = {
  departments: "departaments",
};

@Injectable({
  providedIn: "root",
})
export class UbigeoStoreService {
  constructor() {}

  storeDepartments(departments: Department[]) {
    const storedDepartaments = JSON.stringify(departments);
    localStorage.setItem(ubigeoStoreKeys.departments, storedDepartaments);
  }

  getStoredDepartments(): Department[] | null {
    const storedDepartments = localStorage.getItem(ubigeoStoreKeys.departments);
    if (!storedDepartments) {
      return null;
    }
    const currentStoredDepartments = JSON.parse(storedDepartments) as Department[];
    return currentStoredDepartments;
  }
}
