import { Component } from '@angular/core';

@Component({
  selector: 'app-welcome',
  standalone: true,
  template: `
    <div style="display: flex; justify-content: center; align-items: center; height: 100%; text-align: center;">
      <h1>Welcome to JPO</h1>
    </div>
  `
})
export class WelcomeComponent {}
