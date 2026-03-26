import { Injectable } from '@angular/core';
import * as forge from 'node-forge';

@Injectable({
  providedIn: 'root',
})
export class EncryptionService {

  constructor() {}

  encryptPassword(password: string): string {
    let key = (window as any)['__key'];
    const publicKey = forge.pki.publicKeyFromPem(key);
    const encrypted = publicKey.encrypt(password, 'RSA-OAEP', {
      md: forge.md.sha256.create(),
    });
    return forge.util.encode64(encrypted);
  }
}
