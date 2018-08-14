/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { FileMonitoringTestModule } from '../../../test.module';
import { FilesComponent } from 'app/entities/files/files.component';
import { FilesService } from 'app/entities/files/files.service';
import { Files } from 'app/shared/model/files.model';

describe('Component Tests', () => {
    describe('Files Management Component', () => {
        let comp: FilesComponent;
        let fixture: ComponentFixture<FilesComponent>;
        let service: FilesService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [FileMonitoringTestModule],
                declarations: [FilesComponent],
                providers: []
            })
                .overrideTemplate(FilesComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(FilesComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(FilesService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new Files('123')],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.files[0]).toEqual(jasmine.objectContaining({ id: '123' }));
        });
    });
});
