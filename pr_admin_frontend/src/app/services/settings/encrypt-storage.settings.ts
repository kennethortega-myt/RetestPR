import { EncryptStorage } from "encrypt-storage";
export const encryptStorageEleccion = new EncryptStorage("ONPE_PR_2023", {
  prefix: "@instance1",
  storageType: "sessionStorage",
  stateManagementUse: true,
});
