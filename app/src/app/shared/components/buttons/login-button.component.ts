// import { Component } from '@angular/core';
//
// @Component({
//   selector: 'app-login-button',
//   standalone: true,
//   imports: [],
//   template: `
//     <p>
//       login-button works!
//     </p>
//   `
// })
// export class LoginButtonComponent {
//
// }
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '@auth0/auth0-angular';

@Component({
  selector: 'app-login-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button class="button__login" (click)="handleLogin()">Log In</button>
  `
})
export class LoginButtonComponent {

  private auth = inject(AuthService);

  handleLogin(): void {
    this.auth.loginWithRedirect({
      appState: {
        target: '/profile',
      //   When my users log in with Auth0 and return to my Angular application, take them from the default callback URL path, /callback, to the "Profile" page, /profile
        //If you don't specify this appState.returnTo option, your users will be redirected by default to the / path after they log in.
      },
    });
  }

}