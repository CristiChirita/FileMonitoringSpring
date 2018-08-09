import { NgModule } from '@angular/core';

import { FileMonitoringSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [FileMonitoringSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [FileMonitoringSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class FileMonitoringSharedCommonModule {}
