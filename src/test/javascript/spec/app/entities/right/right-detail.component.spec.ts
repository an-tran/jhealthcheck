/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async } from '@angular/core/testing';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { JhealthcheckTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { RightDetailComponent } from '../../../../../../main/webapp/app/entities/right/right-detail.component';
import { RightService } from '../../../../../../main/webapp/app/entities/right/right.service';
import { Right } from '../../../../../../main/webapp/app/entities/right/right.model';

describe('Component Tests', () => {

    describe('Right Management Detail Component', () => {
        let comp: RightDetailComponent;
        let fixture: ComponentFixture<RightDetailComponent>;
        let service: RightService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [JhealthcheckTestModule],
                declarations: [RightDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    RightService,
                    JhiEventManager
                ]
            }).overrideTemplate(RightDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(RightDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(RightService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Right(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.right).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
