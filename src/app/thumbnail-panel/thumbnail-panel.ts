import { MatButtonModule } from '@angular/material/button';
import { Component, inject } from '@angular/core';
import { SelectedNodeState } from '../selected-node-state';
import { filter } from 'rxjs';
import { JpoNode, SpringConnection } from '../spring-connection';

@Component({
  selector: 'app-thumbnail-panel',
  imports: [MatButtonModule],
  templateUrl: './thumbnail-panel.html',
  styleUrl: './thumbnail-panel.css',
  standalone: true,
})
export class ThumbnailPanel {
  springService = inject(SpringConnection);
  nodeStateService = inject(SelectedNodeState);

  myNode: JpoNode | null = null;

  ngOnInit(): void {
    this.nodeStateService.selectedJpoNode$
      .pipe(
        /// Filter out null nodes. This tells TypeScript the result type is now JpoNode.
        filter((node): node is JpoNode => node !== null)
      )
      .subscribe((node: JpoNode) => {
        this.myNode = node;
      });
  }
}
