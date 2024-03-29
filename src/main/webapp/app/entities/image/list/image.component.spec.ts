jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImageService } from '../service/image.service';

import { ImageComponent } from './image.component';

describe('Component Tests', () => {
  describe('Image Management Component', () => {
    let comp: ImageComponent;
    let fixture: ComponentFixture<ImageComponent>;
    let service: ImageService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ImageComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { snapshot: { queryParams: {} } },
          },
        ],
      })
        .overrideTemplate(ImageComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ImageComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(ImageService);

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
      expect(comp.images?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
