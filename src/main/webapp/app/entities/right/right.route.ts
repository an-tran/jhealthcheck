import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { RightComponent } from './right.component';
import { RightDetailComponent } from './right-detail.component';
import { RightPopupComponent } from './right-dialog.component';
import { RightDeletePopupComponent } from './right-delete-dialog.component';

export const rightRoute: Routes = [
    {
        path: 'right',
        component: RightComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'jhealthcheckApp.right.home.title'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'right/:id',
        component: RightDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'jhealthcheckApp.right.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const rightPopupRoute: Routes = [
    {
        path: 'right-new',
        component: RightPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'jhealthcheckApp.right.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'right/:id/edit',
        component: RightPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'jhealthcheckApp.right.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'right/:id/delete',
        component: RightDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'jhealthcheckApp.right.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
