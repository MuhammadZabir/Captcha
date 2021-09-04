jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { UserTypeService } from '../service/user-type.service';
import { IUserType, UserType } from '../user-type.model';

import { UserTypeUpdateComponent } from './user-type-update.component';

describe('Component Tests', () => {
  describe('UserType Management Update Component', () => {
    let comp: UserTypeUpdateComponent;
    let fixture: ComponentFixture<UserTypeUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let userTypeService: UserTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UserTypeUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(UserTypeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserTypeUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      userTypeService = TestBed.inject(UserTypeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const userType: IUserType = { id: 456 };

        activatedRoute.data = of({ userType });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(userType));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserType>>();
        const userType = { id: 123 };
        jest.spyOn(userTypeService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userType });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: userType }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(userTypeService.update).toHaveBeenCalledWith(userType);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserType>>();
        const userType = new UserType();
        jest.spyOn(userTypeService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userType });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: userType }));
        saveSubject.complete();

        // THEN
        expect(userTypeService.create).toHaveBeenCalledWith(userType);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserType>>();
        const userType = { id: 123 };
        jest.spyOn(userTypeService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userType });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(userTypeService.update).toHaveBeenCalledWith(userType);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
