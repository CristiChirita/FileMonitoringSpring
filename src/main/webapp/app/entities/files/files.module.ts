import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FileMonitoringSharedModule } from 'app/shared';
import {
    FilesComponent,
    FilesDetailComponent,
    FilesUpdateComponent,
    FilesDeletePopupComponent,
    FilesDeleteDialogComponent,
    filesRoute,
    filesPopupRoute
} from './';

const ENTITY_STATES = [...filesRoute, ...filesPopupRoute];

@NgModule({
    imports: [FileMonitoringSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [FilesComponent, FilesDetailComponent, FilesUpdateComponent, FilesDeleteDialogComponent, FilesDeletePopupComponent],
    entryComponents: [FilesComponent, FilesUpdateComponent, FilesDeleteDialogComponent, FilesDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class FileMonitoringFilesModule {}
