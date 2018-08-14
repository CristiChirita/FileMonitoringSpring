import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IFiles } from 'app/shared/model/files.model';
import { Principal } from 'app/core';
import { FilesService } from './files.service';

@Component({
    selector: 'jhi-files',
    templateUrl: './files.component.html'
})
export class FilesComponent implements OnInit, OnDestroy {
    files: IFiles[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private filesService: FilesService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {}

    loadAll() {
        this.filesService.query().subscribe(
            (res: HttpResponse<IFiles[]>) => {
                this.files = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInFiles();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IFiles) {
        return item.id;
    }

    registerChangeInFiles() {
        this.eventSubscriber = this.eventManager.subscribe('filesListModification', response => this.loadAll());
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
