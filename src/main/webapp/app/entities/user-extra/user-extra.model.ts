import { IUser } from 'app/entities/user/user.model';
import { IPurchaseHistory } from 'app/entities/purchase-history/purchase-history.model';
import { IUserType } from 'app/entities/user-type/user-type.model';

export interface IUserExtra {
  id?: number;
  billingAddress?: string | null;
  user?: IUser | null;
  purchaseHistories?: IPurchaseHistory[] | null;
  userType?: IUserType | null;
}

export class UserExtra implements IUserExtra {
  constructor(
    public id?: number,
    public billingAddress?: string | null,
    public user?: IUser | null,
    public purchaseHistories?: IPurchaseHistory[] | null,
    public userType?: IUserType | null
  ) {}
}

export function getUserExtraIdentifier(userExtra: IUserExtra): number | undefined {
  return userExtra.id;
}
