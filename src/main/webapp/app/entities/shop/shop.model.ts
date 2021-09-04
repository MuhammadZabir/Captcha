import * as dayjs from 'dayjs';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { IItem } from 'app/entities/item/item.model';
import { IShopReview } from 'app/entities/shop-review/shop-review.model';

export interface IShop {
  id?: number;
  name?: string | null;
  description?: string | null;
  createdDate?: dayjs.Dayjs | null;
  owner?: IUserExtra | null;
  items?: IItem[] | null;
  shopReviews?: IShopReview[] | null;
}

export class Shop implements IShop {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public owner?: IUserExtra | null,
    public items?: IItem[] | null,
    public shopReviews?: IShopReview[] | null
  ) {}
}

export function getShopIdentifier(shop: IShop): number | undefined {
  return shop.id;
}
