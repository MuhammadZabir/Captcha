import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';

export interface ICart {
  id?: number;
  totalPrice?: number | null;
  captcha?: string | null;
  hiddenCaptcha?: string : null;
  realCaptcha?: string : null;
  buyer?: IUserExtra | null;
  cartBaskets?: ICartBasket[] | null;
}

export class Cart implements ICart {
  constructor(
    public id?: number,
    public totalPrice?: number | null,
    public captcha?: string | null,
    public hiddenCaptcha?: string | null,
    public realCaptcha?: string | null,
    public buyer?: IUserExtra | null,
    public cartBaskets?: ICartBasket[] | null
  ) {}
}

export function getCartIdentifier(cart: ICart): number | undefined {
  return cart.id;
}
