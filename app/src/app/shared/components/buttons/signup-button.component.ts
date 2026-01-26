// import { Component } from '@angular/core';
//
// @Component({
//   selector: 'app-signup-button',
//   standalone: true,
//   imports: [],
//   template: `
//     <p>
//       signup-button works!
//     </p>
//   `
// })
// export class SignupButtonComponent {
//
// }
import { Component, inject } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-signup-button',
  standalone: true,
  imports: [CommonModule],
  template: `
  <button class="button__sign-up" (click)="handleSignUp()">Admin Sign Up</button>
  `
})
export class SignupButtonComponent {
  private auth = inject(AuthService);

  handleSignUp(): void {
    this.auth.loginWithRedirect({
      appState: {
        target: "/profile",
      },
      authorizationParams: {
        screen_hint: 'signup',
      },
    });
  }

}