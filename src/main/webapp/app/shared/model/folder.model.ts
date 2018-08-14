export interface IFolder {
    id?: string;
    hash?: string;
    name?: string;
}

export class Folder implements IFolder {
    constructor(public id?: string, public hash?: string, public name?: string) {}
}
