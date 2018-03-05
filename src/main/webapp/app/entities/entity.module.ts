import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { JhealthcheckPointsModule } from './points/points.module';
import { JhealthcheckRightModule } from './right/right.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        JhealthcheckPointsModule,
        JhealthcheckRightModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JhealthcheckEntityModule {}
