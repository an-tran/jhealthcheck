import { browser, element, by, $ } from 'protractor';
import { NavBarPage } from './../page-objects/jhi-page-objects';
const path = require('path');

describe('Right e2e test', () => {

    let navBarPage: NavBarPage;
    let rightDialogPage: RightDialogPage;
    let rightComponentsPage: RightComponentsPage;
    const fileToUpload = '../../../../main/webapp/content/images/logo-jhipster.png';
    const absolutePath = path.resolve(__dirname, fileToUpload);
    

    beforeAll(() => {
        browser.get('/');
        browser.waitForAngular();
        navBarPage = new NavBarPage();
        navBarPage.getSignInPage().autoSignInUsing('admin', 'admin');
        browser.waitForAngular();
    });

    it('should load Rights', () => {
        navBarPage.goToEntity('right');
        rightComponentsPage = new RightComponentsPage();
        expect(rightComponentsPage.getTitle()).toMatch(/jhealthcheckApp.right.home.title/);

    });

    it('should load create Right dialog', () => {
        rightComponentsPage.clickOnCreateButton();
        rightDialogPage = new RightDialogPage();
        expect(rightDialogPage.getModalTitle()).toMatch(/jhealthcheckApp.right.home.createOrEditLabel/);
        rightDialogPage.close();
    });

    it('should create and save Rights', () => {
        rightComponentsPage.clickOnCreateButton();
        rightDialogPage.setNameInput('name');
        expect(rightDialogPage.getNameInput()).toMatch('name');
        rightDialogPage.save();
        expect(rightDialogPage.getSaveButton().isPresent()).toBeFalsy();
    }); 

    afterAll(() => {
        navBarPage.autoSignOut();
    });
});

export class RightComponentsPage {
    createButton = element(by.css('.jh-create-entity'));
    title = element.all(by.css('jhi-right div h2 span')).first();

    clickOnCreateButton() {
        return this.createButton.click();
    }

    getTitle() {
        return this.title.getAttribute('jhiTranslate');
    }
}

export class RightDialogPage {
    modalTitle = element(by.css('h4#myRightLabel'));
    saveButton = element(by.css('.modal-footer .btn.btn-primary'));
    closeButton = element(by.css('button.close'));
    nameInput = element(by.css('input#field_name'));

    getModalTitle() {
        return this.modalTitle.getAttribute('jhiTranslate');
    }

    setNameInput = function (name) {
        this.nameInput.sendKeys(name);
    }

    getNameInput = function () {
        return this.nameInput.getAttribute('value');
    }

    save() {
        this.saveButton.click();
    }

    close() {
        this.closeButton.click();
    }

    getSaveButton() {
        return this.saveButton;
    }
}
