import { Component, inject, signal } from '@angular/core';
import { CollectionTree } from "./collection-tree/collection-tree";
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ThumbnailPanel } from "./thumbnail-panel/thumbnail-panel";
import { PictureViewer } from "./picture-viewer/picture-viewer";
import { SelectedNodeState } from "./selected-node-state";
import { WelcomeComponent } from './welcome/welcome';
import { CommonModule } from '@angular/common';
import { SpringConnection } from './spring-connection';

@Component({
  selector: 'app-root',
  imports: [
    CollectionTree,
    MatSidenavModule,
    MatButtonModule,
    MatIconModule,
    ThumbnailPanel,
    PictureViewer,
    CommonModule,
    WelcomeComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
  standalone: true,
})
export class App {
  protected readonly title = signal('JpoAngular');
  nodeStateService = inject(SelectedNodeState);
  springService = inject(SpringConnection);

  loginWithGoogle(): void {
    window.location.href = '/oauth2/authorization/google';
  }
}
