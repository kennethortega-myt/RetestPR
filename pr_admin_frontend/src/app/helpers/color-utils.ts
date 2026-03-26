export interface RGBValue {
  red: number;
  green: number;
  blue: number;
}

export function getDegradatedColorFromPercentage(
  percentage: number,
  colors: { init: string; end: string }
): string {
  const initialColor = convertHexToRGB(colors.init);
  const finalColor = convertHexToRGB(colors.end);
  const newColor: RGBValue = {
    blue: getNewValueInRange(
      initialColor.blue,
      finalColor.blue,
      percentage / 100
    ),
    green: getNewValueInRange(
      initialColor.green,
      finalColor.green,
      percentage / 100
    ),
    red: getNewValueInRange(initialColor.red, finalColor.red, percentage / 100),
  };
  const newColorHex = convertRGBToHex(
    newColor.red,
    newColor.green,
    newColor.blue
  );
  return newColorHex;
}

export function convertHexToRGB(color: string): RGBValue {
  const red = parseInt(color.substring(1, 3), 16);
  const green = parseInt(color.substring(3, 5), 16);
  const blue = parseInt(color.substring(5, 7), 16);
  return { red, green, blue };
}

export function convertRGBToHex(red: number, green: number, blue: number): string {
  const hexRed = red.toString(16);
  const stringRed = hexRed.length == 1 ? '0' + hexRed : hexRed;
  const hexGreen = green.toString(16);
  const stringGreen = hexGreen.length == 1 ? '0' + hexGreen : hexGreen;
  const hexBlue = blue.toString(16);
  const stringBlue = hexBlue.length == 1 ? '0' + hexBlue : hexBlue;
  return '#' + stringRed + stringGreen + stringBlue;
}

export function getNewValueInRange(init: number, end: number, value: number): number {
  return Math.round(init + (end - init) * value);
}
