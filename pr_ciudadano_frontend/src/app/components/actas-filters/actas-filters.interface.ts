export interface BaseOptionFilter {
    id?: string,
    text: string,
}

export interface MainOptionFilter extends BaseOptionFilter{
    value?: boolean,
}

export interface ActaOptionFilter extends BaseOptionFilter {
    value?: string,
    children?: ActaOptionFilter[]
}