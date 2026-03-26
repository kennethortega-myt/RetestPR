export interface IBase<T>{
    success: boolean;
    message: string;
    data: T | [T]
}