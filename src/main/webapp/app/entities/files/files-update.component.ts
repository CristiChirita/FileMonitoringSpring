import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IFiles } from 'app/shared/model/files.model';
import { FilesService } from './files.service';

@Component({
    selector: 'jhi-files-update',
    templateUrl: './files-update.component.html'
})
export class FilesUpdateComponent implements OnInit {
    private _files: IFiles;
    isSaving: boolean;
    lastModified: string;

    constructor(private filesService: FilesService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ files }) => {
            this.files = files;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.files.lastModified = moment(this.lastModified, DATE_TIME_FORMAT);
        if (this.files.id !== undefined) {
            this.subscribeToSaveResponse(this.filesService.update(this.files));
        } else {
            this.subscribeToSaveResponse(this.filesService.create(this.files));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IFiles>>) {
        result.subscribe((res: HttpResponse<IFiles>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
    get files() {
        return this._files;
    }

    set files(files: IFiles) {
        this._files = files;
        this.lastModified = moment(files.lastModified).format(DATE_TIME_FORMAT);
    }
}
