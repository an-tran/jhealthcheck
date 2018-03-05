import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JhealthcheckSharedModule } from '../../shared';
import {
    RightService,
    RightPopupService,
    RightComponent,
    RightDetailComponent,
    RightDialogComponent,
    RightPopupComponent,
    RightDeletePopupComponent,
    RightDeleteDialogComponent,
    rightRoute,
    rightPopupRoute,
} from './';

const ENTITY_STATES = [
    ...rightRoute,
    ...rightPopupRoute,
];

@NgModule({
    imports: [
        JhealthcheckSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        RightComponent,
        RightDetailComponent,
        RightDialogComponent,
        RightDeleteDialogComponent,
        RightPopupComponent,
        RightDeletePopupComponent,
    ],
    entryComponents: [
        RightComponent,
        RightDialogComponent,
        RightPopupComponent,
        RightDeleteDialogComponent,
        RightDeletePopupComponent,
    ],
    providers: [
        RightService,
        RightPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JhealthcheckRightModule {}
