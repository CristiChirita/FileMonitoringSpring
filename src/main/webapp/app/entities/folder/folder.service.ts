import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IFolder } from 'app/shared/model/folder.model';

type EntityResponseType = HttpResponse<IFolder>;
type EntityArrayResponseType = HttpResponse<IFolder[]>;

@Injectable({ providedIn: 'root' })
export class FolderService {
    private resourceUrl = SERVER_API_URL + 'api/folders';

    constructor(private http: HttpClient) {}

    create(folder: IFolder): Observable<EntityResponseType> {
        return this.http.post<IFolder>(this.resourceUrl, folder, { observe: 'response' });
    }

    update(folder: IFolder): Observable<EntityResponseType> {
        return this.http.put<IFolder>(this.resourceUrl, folder, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IFolder>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IFolder[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
