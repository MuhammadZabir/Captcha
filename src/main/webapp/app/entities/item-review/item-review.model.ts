import * as dayjs from 'dayjs';
import { IUserExtra } from 'app/entities/user-extra/user-extra.model';
import { IItem } from 'app/entities/item/item.model';

export interface IItemReview {
  id?: number;
  description?: string | null;
  rating?: number | null;
  reviewDate?: dayjs.Dayjs | null;
  reviewer?: IUserExtra | null;
  item?: IItem | null;
}

export class ItemReview implements IItemReview {
  constructor(
    public id?: number,
    public description?: string | null,
    public rating?: number | null,
    public reviewDate?: dayjs.Dayjs | null,
    public reviewer?: IUserExtra | null,
    public item?: IItem | null
  ) {}
}

export function getItemReviewIdentifier(itemReview: IItemReview): number | undefined {
  return itemReview.id;
}
