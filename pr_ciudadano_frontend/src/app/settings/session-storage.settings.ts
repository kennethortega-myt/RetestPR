export type sessionStorageKey = "url";

export function setSessionStorageBy(key: sessionStorageKey, value: string) {
  sessionStorage.setItem(key, value);
}

export function getSessionStorageBy(key: sessionStorageKey): string | null {
  return sessionStorage.getItem(key);
}
