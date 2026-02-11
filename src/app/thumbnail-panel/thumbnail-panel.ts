import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectedNodeState } from '../selected-node-state';
import { filter } from 'rxjs';
import { JpoNode, SpringConnection } from '../spring-connection';

@Component({
  selector: 'app-thumbnail-panel',
  imports: [FormsModule],
  templateUrl: './thumbnail-panel.html',
  styleUrl: './thumbnail-panel.css',
  standalone: true,
})
export class ThumbnailPanel {
  springService = inject(SpringConnection);
  nodeStateService = inject(SelectedNodeState);

  myNode: JpoNode | null = null;
  zoomLevel = 30; // 5 to 100

  get thumbnailWidth(): number {
    const minWidth = 50;
    const maxWidth = 350;
    return minWidth + (maxWidth - minWidth) * (this.zoomLevel - 5) / 95;
  }

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
