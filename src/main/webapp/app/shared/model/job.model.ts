export interface IJob {
    id?: string;
    value?: string;
}

export class Job implements IJob {
    constructor(public id?: string, public value?: string) {}
}
