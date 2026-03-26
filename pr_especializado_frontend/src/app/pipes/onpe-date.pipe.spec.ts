import { DatePipe } from '@angular/common';
import { OnpeDatePipe } from './onpe-date.pipe';

describe('OnpeDatePipe', () => {
  let pipe: OnpeDatePipe;
  let datePipe: DatePipe;

  beforeEach(() => {
    datePipe = new DatePipe('en-US');
    pipe = new OnpeDatePipe(datePipe);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should format date and replace am with A.M.', () => {
    const date = new Date('2024-01-15T09:15:00');
    const result = pipe.transform(date, 'short');

    expect(result).toBeTruthy();
    expect(result).toContain('A.M.');
    expect(result).not.toMatch(/\bam\b/i);
  });

  it('should format date and replace pm with P.M.', () => {
    const date = new Date('2024-01-15T14:30:00');
    const result = pipe.transform(date, 'short');

    expect(result).toBeTruthy();
    expect(result).toContain('P.M.');
    expect(result).not.toMatch(/\bpm\b/i);
  });

  it('should handle null value', () => {
    const result = pipe.transform(null);
    expect(result).toBeNull();
  });

  it('should handle undefined value', () => {
    const result = pipe.transform(undefined);
    expect(result).toBeNull();
  });

  it('should use default format when format is not provided', () => {
    const date = new Date('2024-01-15T14:30:00');
    const result = pipe.transform(date);

    expect(result).toBeTruthy();
    expect(result).toContain('P.M.');
  });

  it('should accept custom format', () => {
    const date = new Date('2024-01-15T14:30:00');
    const result = pipe.transform(date, 'medium');

    expect(result).toBeTruthy();
    expect(result).toContain('P.M.');
  });

  it('should accept string date', () => {
    const dateString = '2024-01-15T14:30:00';
    const result = pipe.transform(dateString, 'short');

    expect(result).toBeTruthy();
    expect(result).toContain('P.M.');
  });

  it('should accept number timestamp', () => {
    const timestamp = new Date('2024-01-15T09:15:00').getTime();
    const result = pipe.transform(timestamp, 'short');

    expect(result).toBeTruthy();
    expect(result).toContain('A.M.');
  });

  it('should replace both am and pm in the same string if present', () => {
    // Este test verifica que el reemplazo funciona correctamente
    const date = new Date('2024-01-15T14:30:00');
    const result = pipe.transform(date, 'short');

    // Asegurarse de que no hay "am" o "pm" en minúsculas
    expect(result).not.toMatch(/\bam\b|\bpm\b/);
  });
});
