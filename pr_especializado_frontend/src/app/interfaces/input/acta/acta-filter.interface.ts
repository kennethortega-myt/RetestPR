export interface BaseOptionFilter {
    id?: string,
    text: string,
}

export interface MainOptionFilter extends BaseOptionFilter{
    value?: number,
}

export interface ActaOptionFilter extends BaseOptionFilter {
    value?: string,
    children?: ActaOptionFilter[]
}