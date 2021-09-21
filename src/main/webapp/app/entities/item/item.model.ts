import { IImage } from 'app/entities/image/image.model';
import { IItemReview } from 'app/entities/item-review/item-review.model';
import { IShop } from 'app/entities/shop/shop.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';

export interface IItem {
  id?: number;
  name?: string | null;
  description?: string | null;
  category?: string | null;
  price?: number | null;
  images?: IImage[] | null;
  itemReviews?: IItemReview[] | null;
  cartBaskets?: ICartBasket[] | null;
  shop?: IShop | null;
}

export class Item implements IItem {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public category?: string | null,
    public price?: number | null,
    public images?: IImage[] | null,
    public itemReviews?: IItemReview[] | null,
    public cartBaskets?: ICartBasket[] | null,
    public shop?: IShop | null
  ) {}
}

export function getItemIdentifier(item: IItem): number | undefined {
  return item.id;
}
