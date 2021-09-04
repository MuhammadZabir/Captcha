jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { UserExtraService } from '../service/user-extra.service';
import { IUserExtra, UserExtra } from '../user-extra.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IUserType } from 'app/entities/user-type/user-type.model';
import { UserTypeService } from 'app/entities/user-type/service/user-type.service';

import { UserExtraUpdateComponent } from './user-extra-update.component';

describe('Component Tests', () => {
  describe('UserExtra Management Update Component', () => {
    let comp: UserExtraUpdateComponent;
    let fixture: ComponentFixture<UserExtraUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let userExtraService: UserExtraService;
    let userService: UserService;
    let userTypeService: UserTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UserExtraUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(UserExtraUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserExtraUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      userExtraService = TestBed.inject(UserExtraService);
      userService = TestBed.inject(UserService);
      userTypeService = TestBed.inject(UserTypeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const userExtra: IUserExtra = { id: 456 };
        const user: IUser = { id: 71520 };
        userExtra.user = user;

        const userCollection: IUser[] = [{ id: 72997 }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ userExtra });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call UserType query and add missing value', () => {
        const userExtra: IUserExtra = { id: 456 };
        const userType: IUserType = { id: 44731 };
        userExtra.userType = userType;

        const userTypeCollection: IUserType[] = [{ id: 24795 }];
        jest.spyOn(userTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: userTypeCollection })));
        const additionalUserTypes = [userType];
        const expectedCollection: IUserType[] = [...additionalUserTypes, ...userTypeCollection];
        jest.spyOn(userTypeService, 'addUserTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ userExtra });
        comp.ngOnInit();

        expect(userTypeService.query).toHaveBeenCalled();
        expect(userTypeService.addUserTypeToCollectionIfMissing).toHaveBeenCalledWith(userTypeCollection, ...additionalUserTypes);
        expect(comp.userTypesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const userExtra: IUserExtra = { id: 456 };
        const user: IUser = { id: 98618 };
        userExtra.user = user;
        const userType: IUserType = { id: 97468 };
        userExtra.userType = userType;

        activatedRoute.data = of({ userExtra });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(userExtra));
        expect(comp.usersSharedCollection).toContain(user);
        expect(comp.userTypesSharedCollection).toContain(userType);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserExtra>>();
        const userExtra = { id: 123 };
        jest.spyOn(userExtraService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userExtra });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: userExtra }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(userExtraService.update).toHaveBeenCalledWith(userExtra);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserExtra>>();
        const userExtra = new UserExtra();
        jest.spyOn(userExtraService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userExtra });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: userExtra }));
        saveSubject.complete();

        // THEN
        expect(userExtraService.create).toHaveBeenCalledWith(userExtra);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<UserExtra>>();
        const userExtra = { id: 123 };
        jest.spyOn(userExtraService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ userExtra });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(userExtraService.update).toHaveBeenCalledWith(userExtra);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackUserTypeById', () => {
        it('Should return tracked UserType primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserTypeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
