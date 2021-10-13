import { Component, OnInit, OnChanges } from '@angular/core';
import { Router } from '@angular/router';

import { VERSION } from 'app/app.constants';
import { Account } from 'app/core/auth/account.model';
import { ICart } from 'app/entities/cart/cart.model';
import { ICartBasket } from 'app/entities/cart-basket/cart-basket.model';
import { AccountService } from 'app/core/auth/account.service';
import { LoginService } from 'app/login/login.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { CartSharedService } from 'app/shared/cart/cart-shared.service';

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements OnInit, OnChanges {
  inProduction?: boolean;
  isNavbarCollapsed = true;
  openAPIEnabled?: boolean;
  version = '';
  account: Account | null = null;
  cartBasketCollections: ICartBasket[] = [];
  itemImageCollections: any[] = [];
  totalItem = 0;

  constructor(
    private loginService: LoginService,
    private accountService: AccountService,
    private profileService: ProfileService,
    private cartSharedService: CartSharedService,
    private router: Router
  ) {
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : 'v' + VERSION;
    }
  }

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction = profileInfo.inProduction;
      this.openAPIEnabled = profileInfo.openAPIEnabled;
    });
    this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    this.updateCartBasket();
  }

  ngOnChanges(): void {
    this.updateCartBasket();
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed = true;
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.collapseNavbar();
    this.loginService.logout();
    this.router.navigate(['']);
  }

  toggleNavbar(): void {
    this.isNavbarCollapsed = !this.isNavbarCollapsed;
  }

  updateCartBasket(): void {
    this.cartSharedService.cart.subscribe((cart) => {
      this.cartBasketCollections = cart.cartBaskets!;
      this.totalItem = this.cartBasketCollections.length;
    });
    this.cartSharedService.images.subscribe((imageCollection) => {
      this.itemImageCollections = imageCollection;
    });
  }
}
