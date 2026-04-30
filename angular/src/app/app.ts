import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CollectionTree } from "./collection-tree/collection-tree";
import { MatSidenavModule } from '@angular/material/sidenav';
import { ThumbnailPanel } from "./thumbnail-panel/thumbnail-panel";
import { PictureViewer } from "./picture-viewer/picture-viewer";
import { SelectedNodeState } from "./selected-node-state";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CollectionTree, MatSidenavModule, ThumbnailPanel, PictureViewer, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
  standalone: true,
})
export class App {
  protected readonly title = signal('JpoAngular');
  nodeStateService = inject(SelectedNodeState);

}
