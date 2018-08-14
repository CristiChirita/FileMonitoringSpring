/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { FileMonitoringTestModule } from '../../../test.module';
import { FilesUpdateComponent } from 'app/entities/files/files-update.component';
import { FilesService } from 'app/entities/files/files.service';
import { Files } from 'app/shared/model/files.model';

describe('Component Tests', () => {
    describe('Files Management Update Component', () => {
        let comp: FilesUpdateComponent;
        let fixture: ComponentFixture<FilesUpdateComponent>;
        let service: FilesService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [FileMonitoringTestModule],
                declarations: [FilesUpdateComponent]
            })
                .overrideTemplate(FilesUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(FilesUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(FilesService);
        });

        describe('save', () => {
            it(
                'Should call update service on save for existing entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Files('123');
                    spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.files = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.update).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );

            it(
                'Should call create service on save for new entity',
                fakeAsync(() => {
                    // GIVEN
                    const entity = new Files();
                    spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                    comp.files = entity;
                    // WHEN
                    comp.save();
                    tick(); // simulate async

                    // THEN
                    expect(service.create).toHaveBeenCalledWith(entity);
                    expect(comp.isSaving).toEqual(false);
                })
            );
        });
    });
});
