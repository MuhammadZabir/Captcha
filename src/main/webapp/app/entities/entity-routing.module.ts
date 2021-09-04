import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'item',
        data: { pageTitle: 'Items' },
        loadChildren: () => import('./item/item.module').then(m => m.ItemModule),
      },
      {
        path: 'image',
        data: { pageTitle: 'Images' },
        loadChildren: () => import('./image/image.module').then(m => m.ImageModule),
      },
      {
        path: 'item-status',
        data: { pageTitle: 'ItemStatuses' },
        loadChildren: () => import('./item-status/item-status.module').then(m => m.ItemStatusModule),
      },
      {
        path: 'shop',
        data: { pageTitle: 'Shops' },
        loadChildren: () => import('./shop/shop.module').then(m => m.ShopModule),
      },
      {
        path: 'user-extra',
        data: { pageTitle: 'UserExtras' },
        loadChildren: () => import('./user-extra/user-extra.module').then(m => m.UserExtraModule),
      },
      {
        path: 'user-type',
        data: { pageTitle: 'UserTypes' },
        loadChildren: () => import('./user-type/user-type.module').then(m => m.UserTypeModule),
      },
      {
        path: 'shop-review',
        data: { pageTitle: 'ShopReviews' },
        loadChildren: () => import('./shop-review/shop-review.module').then(m => m.ShopReviewModule),
      },
      {
        path: 'item-review',
        data: { pageTitle: 'ItemReviews' },
        loadChildren: () => import('./item-review/item-review.module').then(m => m.ItemReviewModule),
      },
      {
        path: 'purchase-history',
        data: { pageTitle: 'PurchaseHistories' },
        loadChildren: () => import('./purchase-history/purchase-history.module').then(m => m.PurchaseHistoryModule),
      },
      {
        path: 'cart',
        data: { pageTitle: 'Carts' },
        loadChildren: () => import('./cart/cart.module').then(m => m.CartModule),
      },
      {
        path: 'cart-basket',
        data: { pageTitle: 'CartBaskets' },
        loadChildren: () => import('./cart-basket/cart-basket.module').then(m => m.CartBasketModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
