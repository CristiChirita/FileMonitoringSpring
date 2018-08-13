import { Moment } from 'moment';

export interface IJob {
    id?: string;
    value?: string;
    date?: Moment;
}

export class Job implements IJob {
    constructor(public id?: string, public value?: string, public date?: Moment) {}
}
