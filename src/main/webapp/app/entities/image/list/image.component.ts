import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IImage } from '../image.model';
import { ImageService } from '../service/image.service';
import { ImageDeleteDialogComponent } from '../delete/image-delete-dialog.component';

@Component({
  selector: 'jhi-image',
  templateUrl: './image.component.html',
})
export class ImageComponent implements OnInit {
  images?: IImage[];
  isLoading = false;
  currentSearch: string;

  constructor(protected imageService: ImageService, protected modalService: NgbModal, protected activatedRoute: ActivatedRoute) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.imageService
        .search({
          query: this.currentSearch,
        })
        .subscribe(
          (res: HttpResponse<IImage[]>) => {
            this.isLoading = false;
            this.images = res.body ?? [];
          },
          () => {
            this.isLoading = false;
          }
        );
      return;
    }

    this.imageService.query().subscribe(
      (res: HttpResponse<IImage[]>) => {
        this.isLoading = false;
        this.images = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IImage): number {
    return item.id!;
  }

  delete(image: IImage): void {
    const modalRef = this.modalService.open(ImageDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.image = image;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
