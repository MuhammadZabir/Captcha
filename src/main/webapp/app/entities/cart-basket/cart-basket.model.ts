import { ICart } from 'app/entities/cart/cart.model';

export interface ICartBasket {
  id?: number;
  amount?: number | null;
  cart?: ICart | null;
}

export class CartBasket implements ICartBasket {
  constructor(public id?: number, public amount?: number | null, public cart?: ICart | null) {}
}

export function getCartBasketIdentifier(cartBasket: ICartBasket): number | undefined {
  return cartBasket.id;
}
