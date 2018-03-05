import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Right } from './right.model';
import { RightPopupService } from './right-popup.service';
import { RightService } from './right.service';

@Component({
    selector: 'jhi-right-dialog',
    templateUrl: './right-dialog.component.html'
})
export class RightDialogComponent implements OnInit {

    right: Right;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private jhiAlertService: JhiAlertService,
        private rightService: RightService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.right.id !== undefined) {
            this.subscribeToSaveResponse(
                this.rightService.update(this.right));
        } else {
            this.subscribeToSaveResponse(
                this.rightService.create(this.right));
        }
    }

    private subscribeToSaveResponse(result: Observable<Right>) {
        result.subscribe((res: Right) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError());
    }

    private onSaveSuccess(result: Right) {
        this.eventManager.broadcast({ name: 'rightListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.jhiAlertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-right-popup',
    template: ''
})
export class RightPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private rightPopupService: RightPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.rightPopupService
                    .open(RightDialogComponent as Component, params['id']);
            } else {
                this.rightPopupService
                    .open(RightDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
