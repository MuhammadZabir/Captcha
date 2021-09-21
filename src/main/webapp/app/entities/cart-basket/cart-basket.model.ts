import { ICart } from 'app/entities/cart/cart.model';
import { IItem } from 'app/entities/item/item.model';

export interface ICartBasket {
  id?: number;
  amount?: number | null;
  cart?: ICart | null;
  item?: IItem | null;
}

export class CartBasket implements ICartBasket {
  constructor(public id?: number, public amount?: number | null, public cart?: ICart | null, public item?: IItem | null) {}
}

export function getCartBasketIdentifier(cartBasket: ICartBasket): number | undefined {
  return cartBasket.id;
}
