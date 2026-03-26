export interface IHoraDeInicioItem {
  value: string;
  text: string;
}

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
    const numberOfHours = Math.floor((index * periodo) / 60);
    const numberOfMinutes = (index * periodo) % 60;
    const ele = {
      text: `${getHoursOrMinutesString(
        numberOfHours
      )}:${getHoursOrMinutesString(numberOfMinutes)} horas`,
      value: `${getHoursOrMinutesString(
        numberOfHours
      )}:${getHoursOrMinutesString(numberOfMinutes)}`,
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
    const totalMinutes = index * periodo;
    const numberOfHours = Math.floor(totalMinutes / 60);
    const numberOfMinutes = totalMinutes % 60;

    // convertir a formato 12h con AM/PM
    const ampm = numberOfHours < 12 ? "AM" : "PM";
    const hour12 = numberOfHours % 12 === 0 ? 12 : numberOfHours % 12;

    const text = `${getHoursOrMinutesString(hour12)}:${getHoursOrMinutesString(
      numberOfMinutes
    )} ${ampm}`;

    const ele = {
      text,
      value: text, // si quieres cálculos en 24h, aquí pon numberOfHours:numberOfMinutes
    } as IHoraDeInicioItem;

    customHourList.push(ele);
  }

  return customHourList;
}

export function convertTo24Hour(time12h: string): string {
  const [time, modifier] = time12h.split(" ");
  let [hours, minutes] = time.split(":").map(Number);

  if (modifier === "PM" && hours !== 12) {
    hours += 12;
  }
  if (modifier === "AM" && hours === 12) {
    hours = 0;
  }

  // retorna con formato 2 dígitos
  return `${hours.toString().padStart(2, "0")}:${minutes.toString().padStart(2, "0")}`;
}

export function getHoursOrMinutesString(value: number): string {
  return value < 10 ? `0${value}` : `${value}`;
}
