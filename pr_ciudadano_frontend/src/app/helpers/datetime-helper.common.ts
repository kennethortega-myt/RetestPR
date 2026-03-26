export function getCurrentDateTime(): string {
  let date = new Date();
  let day = date.getDate();
  let month = date.getMonth() + 1;
  let year = date.getFullYear();
  let time = date.getTime();
  return `${day}-${month}-${year}_${time}`;
}
