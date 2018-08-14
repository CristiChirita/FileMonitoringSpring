import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { IFolder } from 'app/shared/model/folder.model';
import { FolderService } from './folder.service';

@Component({
    selector: 'jhi-folder-update',
    templateUrl: './folder-update.component.html'
})
export class FolderUpdateComponent implements OnInit {
    private _folder: IFolder;
    isSaving: boolean;

    constructor(private folderService: FolderService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ folder }) => {
            this.folder = folder;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.folder.id !== undefined) {
            this.subscribeToSaveResponse(this.folderService.update(this.folder));
        } else {
            this.subscribeToSaveResponse(this.folderService.create(this.folder));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IFolder>>) {
        result.subscribe((res: HttpResponse<IFolder>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    get folder() {
        return this._folder;
    }

    set folder(folder: IFolder) {
        this._folder = folder;
    }
}
