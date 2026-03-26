import { Injectable } from '@angular/core';
import * as forge from 'node-forge';

@Injectable({
  providedIn: 'root'
})
export class EncryptionService {

  private readonly publicKey: string = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh65ZflibNrkOsLX+q4tk
DJvJjDC35DhlGU2yMB0ULGk1Ce1TYtaUUMGJH5NTxGDRkhE61oU4h2X9k+NqGLRR
8yyRkFQg13jvwhcKe506e2FLwOMh53wKeStGIUPF2d2HZjQH0tLgHeklpZO8jaOV
zcngMoRVbc0jDKVO4yBeZsgAXVmH2zLZ99wKxDtN2SNqf2+Q1LPNuNcrs0013J7a
QkreHl+7lMjRHDaihTsUatY3mEQ2Z3rsN8MYsZn2UIfRZUNBA8UKIgyC0rLe+pZf
8vvfV2msSJlm/HqRTwP2S50tPOm4KewhI91NBKigpeMQyGhI0ZKaSkdrnVIoequC
oQIDAQAB
-----END PUBLIC KEY-----`;

  constructor() {}

  encryptPassword(password: string): string {
    const publicKey = forge.pki.publicKeyFromPem(this.publicKey);
    const encrypted = publicKey.encrypt(password, 'RSA-OAEP', {
      md: forge.md.sha256.create(),
    });
    return forge.util.encode64(encrypted);
  }

}
