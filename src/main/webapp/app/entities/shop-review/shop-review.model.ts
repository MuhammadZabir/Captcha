import * as dayjs from 'dayjs';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { IShop } from 'app/entities/shop/shop.model';

export interface IShopReview {
  id?: number;
  description?: string | null;
  rating?: number | null;
  reviewDate?: dayjs.Dayjs | null;
  reviewer?: IUserExtra | null;
  shop?: IShop | null;
}

export class ShopReview implements IShopReview {
  constructor(
    public id?: number,
    public description?: string | null,
    public rating?: number | null,
    public reviewDate?: dayjs.Dayjs | null,
    public reviewer?: IUserExtra | null,
    public shop?: IShop | null
  ) {}
}

export function getShopReviewIdentifier(shopReview: IShopReview): number | undefined {
  return shopReview.id;
}
