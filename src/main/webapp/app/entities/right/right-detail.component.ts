import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { Right } from './right.model';
import { RightService } from './right.service';

@Component({
    selector: 'jhi-right-detail',
    templateUrl: './right-detail.component.html'
})
export class RightDetailComponent implements OnInit, OnDestroy {

    right: Right;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private rightService: RightService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInRights();
    }

    load(id) {
        this.rightService.find(id).subscribe((right) => {
            this.right = right;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInRights() {
        this.eventSubscriber = this.eventManager.subscribe(
            'rightListModification',
            (response) => this.load(this.right.id)
        );
    }
}
