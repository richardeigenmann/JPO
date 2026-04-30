import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectedNodeState } from '../selected-node-state';
import { JpoNode, SpringConnection } from '../spring-connection';
import { CommonModule } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-thumbnail-panel',
  imports: [FormsModule, CommonModule],
  templateUrl: './thumbnail-panel.html',
  styleUrl: './thumbnail-panel.css',
  standalone: true,
})
export class ThumbnailPanel {
  springService = inject(SpringConnection);
  nodeStateService = inject(SelectedNodeState);

  navigator = toSignal(this.nodeStateService.navigator$);
  zoomLevel = 30; // 5 to 100

  get thumbnailWidth(): number {
    const minWidth = 50;
    const maxWidth = 350;
    return minWidth + (maxWidth - minWidth) * (this.zoomLevel - 5) / 95;
  }

  get nodes(): JpoNode[] {
    const nav = this.navigator();
    if (!nav) return [];
    const count = nav.getNumberOfNodes();
    const result: JpoNode[] = [];
    for (let i = 0; i < count; i++) {
      const node = nav.getNode(i);
      if (node) result.push(node);
    }
    return result;
  }

  onThumbnailClick(node: JpoNode): void {
    this.nodeStateService.setSelectedChild(node);
  }
}
