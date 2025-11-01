import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CollectionTree } from "./collection-tree/collection-tree";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CollectionTree],
  templateUrl: './app.html',
  styleUrl: './app.css',
  standalone: true,
})
export class App {
  protected readonly title = signal('JpoAngular');
}
