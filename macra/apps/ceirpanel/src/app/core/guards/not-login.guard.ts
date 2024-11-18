import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/common/auth.service';
import { ApiUtilService } from '../services/common/api.util.service';
import * as _ from 'lodash';

@Injectable({
  providedIn: 'root',
})
export class NotLoginGuard {
  constructor(
    private authService: AuthService,
    private router: Router,
    private apiutil: ApiUtilService
  ) {}
  canActivate():
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    return new Observable<boolean>((observer) => {
      setTimeout(() => {
        this.apiutil.get('/api/auth/isLogin').subscribe({
          next: (data) => {
            if (_.isEqual(_.get(data, 'login'), true)) {
              this.router.navigate(['/dashboard']);
              observer.next(false);
            } else {
              observer.next(true);
            }
          },
        });
      }, 10);
    });
  }
}
