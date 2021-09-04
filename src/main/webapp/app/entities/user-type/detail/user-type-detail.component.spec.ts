import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserTypeDetailComponent } from './user-type-detail.component';

describe('Component Tests', () => {
  describe('UserType Management Detail Component', () => {
    let comp: UserTypeDetailComponent;
    let fixture: ComponentFixture<UserTypeDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [UserTypeDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ userType: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(UserTypeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(UserTypeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load userType on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.userType).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
