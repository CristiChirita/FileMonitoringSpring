import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { Folder } from 'app/shared/model/folder.model';
import { FolderService } from './folder.service';
import { FolderComponent } from './folder.component';
import { FolderDetailComponent } from './folder-detail.component';
import { FolderUpdateComponent } from './folder-update.component';
import { FolderDeletePopupComponent } from './folder-delete-dialog.component';
import { IFolder } from 'app/shared/model/folder.model';

@Injectable({ providedIn: 'root' })
export class FolderResolve implements Resolve<IFolder> {
    constructor(private service: FolderService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((folder: HttpResponse<Folder>) => folder.body));
        }
        return of(new Folder());
    }
}

export const folderRoute: Routes = [
    {
        path: 'folder',
        component: FolderComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'Folders'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'folder/:id/view',
        component: FolderDetailComponent,
        resolve: {
            folder: FolderResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Folders'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'folder/new',
        component: FolderUpdateComponent,
        resolve: {
            folder: FolderResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Folders'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'folder/:id/edit',
        component: FolderUpdateComponent,
        resolve: {
            folder: FolderResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Folders'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const folderPopupRoute: Routes = [
    {
        path: 'folder/:id/delete',
        component: FolderDeletePopupComponent,
        resolve: {
            folder: FolderResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Folders'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
