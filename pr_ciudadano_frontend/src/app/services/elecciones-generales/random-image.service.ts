import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class RandomImageService {
  private images: string[] = [
    "assets/personajes/personaje1.svg",
    "assets/personajes/personaje2.svg",
    "assets/personajes/personaje3.svg",
  ];

  private lastIndex: number | null = null;

  constructor() {}

  getRandomImage(): string {
    let randomIndex: number;

    do {
      randomIndex = Math.floor(Math.random() * this.images.length);
    } while (randomIndex === this.lastIndex);

    this.lastIndex = randomIndex;
    return this.images[randomIndex];
  }
}
