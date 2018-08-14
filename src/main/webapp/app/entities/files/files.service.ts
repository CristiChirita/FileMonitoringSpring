import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IFiles } from 'app/shared/model/files.model';

type EntityResponseType = HttpResponse<IFiles>;
type EntityArrayResponseType = HttpResponse<IFiles[]>;

@Injectable({ providedIn: 'root' })
export class FilesService {
    private resourceUrl = SERVER_API_URL + 'api/files';

    constructor(private http: HttpClient) {}

    create(files: IFiles): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(files);
        return this.http
            .post<IFiles>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(files: IFiles): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(files);
        return this.http
            .put<IFiles>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http
            .get<IFiles>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<IFiles[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    private convertDateFromClient(files: IFiles): IFiles {
        const copy: IFiles = Object.assign({}, files, {
            lastModified: files.lastModified != null && files.lastModified.isValid() ? files.lastModified.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.lastModified = res.body.lastModified != null ? moment(res.body.lastModified) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((files: IFiles) => {
            files.lastModified = files.lastModified != null ? moment(files.lastModified) : null;
        });
        return res;
    }
}
