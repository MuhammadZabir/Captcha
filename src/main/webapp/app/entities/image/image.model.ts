import { IItem } from 'app/entities/item/item.model';

export interface IImage {
  id?: number;
  imageDir?: string | null;
  item?: IItem | null;
}

export class Image implements IImage {
  constructor(public id?: number, public imageDir?: string | null, public item?: IItem | null) {}
}

export function getImageIdentifier(image: IImage): number | undefined {
  return image.id;
}
