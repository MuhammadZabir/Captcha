jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { UserTypeService } from '../service/user-type.service';

import { UserTypeComponent } from './user-type.component';

describe('Component Tests', () => {
  describe('UserType Management Component', () => {
    let comp: UserTypeComponent;
    let fixture: ComponentFixture<UserTypeComponent>;
    let service: UserTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [UserTypeComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(UserTypeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(UserTypeComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(UserTypeService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.userTypes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
