import { IUserExtra } from 'app/entities/user-extra/user-extra.model';

export interface IUserType {
  id?: number;
  name?: string | null;
  description?: string | null;
  userExtras?: IUserExtra[] | null;
}

export class UserType implements IUserType {
  constructor(
    public id?: number,
    public name?: string | null,
    public description?: string | null,
    public userExtras?: IUserExtra[] | null
  ) {}
}

export function getUserTypeIdentifier(userType: IUserType): number | undefined {
  return userType.id;
}
