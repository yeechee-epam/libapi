// import { Component } from '@angular/core';
//
// @Component({
//   selector: 'app-logout-button',
//   standalone: true,
//   imports: [],
//   template: `
//     <p>
//       logout-button works!
//     </p>
//   `
// })
// export class LogoutButtonComponent {
//
// }
import { Component, inject } from '@angular/core';
import { CommonModule, DOCUMENT } from '@angular/common';
import { AuthService } from '@auth0/auth0-angular';

@Component({
  selector: 'app-logout-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button class="button__logout" (click)="handleLogout()">Log Out</button>
  `
})
export class LogoutButtonComponent {
  private auth = inject(AuthService);
  private doc = inject(DOCUMENT);

  handleLogout(): void {
    this.auth.logout({
      logoutParams: {
        returnTo: this.doc.location.origin,
      },
    });
  }
}
// When using the logout() method, the Auth0 Angular SDK clears the application session and redirects to the Auth0 /v2/logout endpoint to clear the Auth0 session under the hood.
