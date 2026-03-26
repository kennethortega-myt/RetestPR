export const ONPE_PERSONAJES_IMAGES: string[] = [
  "assets/personajes/personaje1.svg",
  "assets/personajes/personaje2.svg",
  "assets/personajes/personaje3.svg",
];

export function getRandomImage(): string {
  const randomIndex = Math.floor(Math.random() * (ONPE_PERSONAJES_IMAGES.length - 1));
  return ONPE_PERSONAJES_IMAGES[randomIndex];
}
