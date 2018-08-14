import { Moment } from 'moment';

export interface IFiles {
    id?: string;
    name?: string;
    location?: string;
    lastModified?: Moment;
}

export class Files implements IFiles {
    constructor(public id?: string, public name?: string, public location?: string, public lastModified?: Moment) {}
}
