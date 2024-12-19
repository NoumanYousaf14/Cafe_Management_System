import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { SnackbarService } from './snackbar.service';
import {jwtDecode} from 'jwt-decode';
import { GlobalConstant } from '../shared/global-constants';

@Injectable({
  providedIn: 'root',
})
export class RouteGuardService {
  constructor(
    public auth: AuthService,
    public router: Router,
    private snackBarService: SnackbarService
  ) {}

  canActivate(router: ActivatedRouteSnapshot): boolean {
    const expectedRoleArray = router.data?.expectedRole || [];
    const token: string | null = localStorage.getItem('token');

    if (!token) {
      this.redirectToLogin();
      return false;
    }

    let tokenPayload: any;
    try {
      tokenPayload = jwtDecode(token);
    } catch (err) {
      this.redirectToLogin();
      return false;
    }

    const userRole = tokenPayload.role;

    if (!expectedRoleArray.includes(userRole)) {
      this.snackBarService.openSnackBar(GlobalConstant.unauthorized, GlobalConstant.error);
      this.router.navigate(['/cafe/dashboard']);
      return false;
    }

    if (this.auth.isAuthenticated()) {
      return true;
    }

    this.snackBarService.openSnackBar(GlobalConstant.unauthorized, GlobalConstant.error);
    this.router.navigate(['/cafe/dashboard']);
    return false;
  }

  private redirectToLogin() {
    localStorage.clear();
    this.router.navigate(['/']);
  }
}
