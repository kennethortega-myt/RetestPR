export type dimentionsType = "DESKTOP" | "LAPTOP" | "TABLET" | "CELL";

export const NUMBER_OF_BAR_IN_GRAFIC: { [key in dimentionsType]: number } = {
  DESKTOP: 7,
  LAPTOP: 5,
  TABLET: 4,
  CELL: 3,
};

export const NUMBER_OF_BAR_IN_GRAFIC_GROUP: { [key in dimentionsType]: number } = {
  DESKTOP: 6,
  LAPTOP: 6,
  TABLET: 4,
  CELL: 2,
};

export const SIZE_PAGINATION: { [key in dimentionsType]: number } = {
  DESKTOP: 15,
  LAPTOP: 15,
  TABLET: 10,
  CELL: 5,
};