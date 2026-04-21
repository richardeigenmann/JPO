import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CollectionTree } from "./collection-tree/collection-tree";
import { MatSidenavModule } from '@angular/material/sidenav';
import { ThumbnailPanel } from "./thumbnail-panel/thumbnail-panel";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CollectionTree, MatSidenavModule, ThumbnailPanel],
  templateUrl: './app.html',
  styleUrl: './app.css',
  standalone: true,
})
export class App {
  protected readonly title = signal('JpoAngular');

}
