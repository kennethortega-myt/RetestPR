import { EncryptStorage } from 'encrypt-storage';

export const encryptStorageEleccion = new EncryptStorage('ONPE_PR_2023', {
  prefix: '@instance1',
  storageType: 'sessionStorage',
  stateManagementUse: true
});

export const encryptStorageLocal = new EncryptStorage('ONPE_PR_2026', {
  prefix: '@instance1',
  storageType: 'localStorage',
  stateManagementUse: true
});
