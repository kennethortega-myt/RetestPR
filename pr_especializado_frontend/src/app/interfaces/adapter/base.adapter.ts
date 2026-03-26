export interface BaseAdapter<T> {
  adapt(item: any): T | null;
}
