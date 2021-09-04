import { IItem } from 'app/entities/item/item.model';
import { AvailabilityStatus } from 'app/entities/enumerations/availability-status.model';

export interface IItemStatus {
  id?: number;
  amountAvailable?: number | null;
  amountSold?: number | null;
  availabilityStatus?: AvailabilityStatus | null;
  item?: IItem | null;
}

export class ItemStatus implements IItemStatus {
  constructor(
    public id?: number,
    public amountAvailable?: number | null,
    public amountSold?: number | null,
    public availabilityStatus?: AvailabilityStatus | null,
    public item?: IItem | null
  ) {}
}

export function getItemStatusIdentifier(itemStatus: IItemStatus): number | undefined {
  return itemStatus.id;
}
