import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Right } from './right.model';
import { RightPopupService } from './right-popup.service';
import { RightService } from './right.service';

@Component({
    selector: 'jhi-right-delete-dialog',
    templateUrl: './right-delete-dialog.component.html'
})
export class RightDeleteDialogComponent {

    right: Right;

    constructor(
        private rightService: RightService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.rightService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'rightListModification',
                content: 'Deleted an right'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-right-delete-popup',
    template: ''
})
export class RightDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private rightPopupService: RightPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.rightPopupService
                .open(RightDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
