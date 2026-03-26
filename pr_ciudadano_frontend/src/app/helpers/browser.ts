export const isBrowserChrome = (): boolean => {
  const ua = navigator.userAgent;
  const isChromeDesktop = /Chrome\//i.test(ua) && !/Edg\/|OPR\/|SamsungBrowser|YaBrowser/i.test(ua);
  const isChromeIOS = /CriOS\//i.test(ua);
  const isChrome = isChromeDesktop || isChromeIOS;

  return isChrome;
};
