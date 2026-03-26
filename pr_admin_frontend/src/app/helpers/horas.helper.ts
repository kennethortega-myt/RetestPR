import { IHoraDeInicioItem } from "../interfaces/configurar-reportes.interfaces";
import dayjs from 'dayjs';

export type PeriodoType = 20 | 30 | 60;

/**
 *
 * @param periodo en minutos
 */
export function getAllHourListFromDay2(
  periodo: PeriodoType
): IHoraDeInicioItem[] {
  const customHourList: IHoraDeInicioItem[] = [];
  const numberOfElements = (24 * 60) / periodo;
  for (let index = 0; index < numberOfElements; index++) {
    const minutesFromStart = index * periodo;
    const t = dayjs().startOf('day').add(minutesFromStart, 'minute');
    const ele = {
      text: `${t.format('HH:mm')} horas`,
      value: t.format('HH:mm'),
    } as IHoraDeInicioItem;
    customHourList.push(ele);
  }

  return customHourList;
}

export function getAllHourListFromDay(
  periodo: PeriodoType
): IHoraDeInicioItem[] {
  const customHourList: IHoraDeInicioItem[] = [];
  const numberOfElements = (24 * 60) / periodo;

  for (let index = 0; index < numberOfElements; index++) {
    const minutesFromStart = index * periodo;
    const t = dayjs().startOf('day').add(minutesFromStart, 'minute');

    const text = t.format('hh:mm A').replace('AM', 'a.m.').replace('PM', 'p.m.');

    const ele = {
      text,
      value: text, // si quieres cálculos en 24h, aquí pon numberOfHours:numberOfMinutes
    } as IHoraDeInicioItem;

    customHourList.push(ele);
  }

  return customHourList;
}

export function convertTo24Hour(time12h: string): string {
  // Try to parse using dayjs (will handle common AM/PM formats). Fallback to manual parsing.
  try {
    const parsed = dayjs(time12h.replace(/\./g, ''));
    if (parsed.isValid()) {
      return parsed.format('HH:mm');
    }
  } catch (e) {
    console.error('Error parsing time:', e);
  }

  // Fallback manual parse (accepts formats like "8:30 PM" or "08:30 PM")
  const parts = time12h.trim().split(/\s+/);
  if (parts.length < 2) return time12h;
  const time = parts[0];
  const modifier = parts[1].replace(/\./g, '').toUpperCase();
  let [hours, minutes] = time.split(':').map(Number);

  if (modifier === 'PM' && hours !== 12) {
    hours += 12;
  }
  if (modifier === 'AM' && hours === 12) {
    hours = 0;
  }

  // retorna con formato 2 dígitos
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}`;
}

export function getHoursOrMinutesString(value: number): string {
  return value < 10 ? `0${value}` : `${value}`;
}

// Format a Date or date-like string into 12-hour format with AM/PM (e.g. "8:30 PM")
export function formatTo12Hour(date: Date | string): string {
  return dayjs(date).format('hh:mm A').replace('AM', 'a.m.').replace('PM', 'p.m.');
}
