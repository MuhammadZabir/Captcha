import * as dayjs from 'dayjs';
import { ICart } from 'app/entities/cart/cart.model';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';

export interface IPurchaseHistory {
  id?: number;
  purchaseDate?: dayjs.Dayjs | null;
  shippingDate?: dayjs.Dayjs | null;
  billingAddress?: string | null;
  paymentStatus?: PaymentStatus | null;
  cart?: ICart | null;
  buyer?: IUserExtra | null;
}

export class PurchaseHistory implements IPurchaseHistory {
  constructor(
    public id?: number,
    public purchaseDate?: dayjs.Dayjs | null,
    public shippingDate?: dayjs.Dayjs | null,
    public billingAddress?: string | null,
    public paymentStatus?: PaymentStatus | null,
    public cart?: ICart | null,
    public buyer?: IUserExtra | null
  ) {}
}

export function getPurchaseHistoryIdentifier(purchaseHistory: IPurchaseHistory): number | undefined {
  return purchaseHistory.id;
}
