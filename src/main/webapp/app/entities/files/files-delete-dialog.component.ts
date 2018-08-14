import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IFiles } from 'app/shared/model/files.model';
import { FilesService } from './files.service';

@Component({
    selector: 'jhi-files-delete-dialog',
    templateUrl: './files-delete-dialog.component.html'
})
export class FilesDeleteDialogComponent {
    files: IFiles;

    constructor(private filesService: FilesService, public activeModal: NgbActiveModal, private eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.filesService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'filesListModification',
                content: 'Deleted an files'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-files-delete-popup',
    template: ''
})
export class FilesDeletePopupComponent implements OnInit, OnDestroy {
    private ngbModalRef: NgbModalRef;

    constructor(private activatedRoute: ActivatedRoute, private router: Router, private modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ files }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(FilesDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.files = files;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate([{ outlets: { popup: null } }], { replaceUrl: true, queryParamsHandling: 'merge' });
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
