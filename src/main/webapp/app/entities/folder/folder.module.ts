import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FileMonitoringSharedModule } from 'app/shared';
import {
    FolderComponent,
    FolderDetailComponent,
    FolderUpdateComponent,
    FolderDeletePopupComponent,
    FolderDeleteDialogComponent,
    folderRoute,
    folderPopupRoute
} from './';

const ENTITY_STATES = [...folderRoute, ...folderPopupRoute];

@NgModule({
    imports: [FileMonitoringSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [FolderComponent, FolderDetailComponent, FolderUpdateComponent, FolderDeleteDialogComponent, FolderDeletePopupComponent],
    entryComponents: [FolderComponent, FolderUpdateComponent, FolderDeleteDialogComponent, FolderDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class FileMonitoringFolderModule {}
