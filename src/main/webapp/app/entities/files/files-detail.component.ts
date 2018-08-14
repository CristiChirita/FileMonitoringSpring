import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFiles } from 'app/shared/model/files.model';

@Component({
    selector: 'jhi-files-detail',
    templateUrl: './files-detail.component.html'
})
export class FilesDetailComponent implements OnInit {
    files: IFiles;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ files }) => {
            this.files = files;
        });
    }

    previousState() {
        window.history.back();
    }
}
