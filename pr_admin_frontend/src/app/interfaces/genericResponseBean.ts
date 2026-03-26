export interface GenericResponseBean<T> {
  success: boolean;
  message: string;
  data: T;
}
