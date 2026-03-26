import { DecimalPipe } from '@angular/common';

const decimalPipe = new DecimalPipe('en-US');

export function getformattedNumberWithDecimals(value: number, numberOfDecimals: number): number {
  const newValue = Number(value.toFixed(numberOfDecimals));
  return newValue;
}

export function getDecimalPipe(
  value: number,
  numberOfDecimals: number,
  forceDecimals: boolean = false
): string {
  if (value === null || value === undefined || isNaN(value)) {
    return '0';
  }

  const isZeroOrHundred = value === 0 || value === 100;

  const format =
    forceDecimals && !isZeroOrHundred
      ? `1.${numberOfDecimals}-${numberOfDecimals}`
      : `1.0-${numberOfDecimals}`;

  return decimalPipe.transform(value, format) ?? '';
}
