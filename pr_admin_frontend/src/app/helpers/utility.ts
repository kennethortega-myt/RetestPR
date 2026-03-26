export class Utility {
  static rellenarCerosAIzquierda(number: number, width: number): string {
    const isNegative = number < 0;
    const numberStr = Math.abs(number).toString().padStart(width, '0');
    return isNegative ? '-' + numberStr : numberStr;
  }
}