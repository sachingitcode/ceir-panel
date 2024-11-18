import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'ceirpanel-access-denied',
  template: `
  <div class="app flex-row align-items-center">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="clearfix">
            <h1 class="float-left display-3 mr-4">403</h1>
            <h4 class="pt-3">Oops! You do not have access.</h4>
            <p class="text-muted">The page you are looking for was denied.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
  `,
  styles: [``],
})
export class AccessDeniedComponent {
  constructor(private router: Router) { }

}
