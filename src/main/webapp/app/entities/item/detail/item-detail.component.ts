import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { CartSharedService } from 'app/shared/cart/cart-shared.service';
import { ImageService } from 'app/entities/image/service/image.service';

import { IItem } from '../item.model';
import { IImage } from 'app/entities/image/image.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';

@Component({
  selector: 'jhi-item-detail',
  templateUrl: './item-detail.component.html',
})
export class ItemDetailComponent implements OnInit {
  item: IItem | null = null;
  imageCollection: IImage[] = [];
  results: any[] = [];

  constructor(
    protected activatedRoute: ActivatedRoute,
    protected cartSharedService: CartSharedService,
    protected imageService: ImageService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ item }) => {
      this.item = item;
      this.imageCollection = item.images;
      this.imageService.findByItem(item.id).subscribe((res: HttpResponse<IImage[]>) => {
        this.imageCollection = res.body!;
        this.getImage();
      });
    });
  }

  previousState(): void {
    window.history.back();
  }

  addToBasket(): void {
    const cartBasket = <ICartBasket>{};
    cartBasket.amount = 1;
    cartBasket.item = this.item;
    this.cartSharedService.addCartBasket(cartBasket, this.results[0]);
    this.previousState();
  }

  getImage(): void {
    if (this.imageCollection.length > 0) {
      for (const image of this.imageCollection) {
        this.imageService.getImage(image.imageDir!).subscribe(result => {
          this.createImageFromBlob(result);
        });
      }
    }
  }

  getImageTest(): void {
    this.imageService.getImage('/images/banana.jpeg').subscribe(result => {
      this.createImageFromBlob(result);
    });
  }

  createImageFromBlob(image: Blob): void {
    const reader = new FileReader();
    reader.addEventListener(
        'load',
        () => {
            this.results.push(reader.result);
        },
        false
    );

    reader.readAsDataURL(image);
  }
}
