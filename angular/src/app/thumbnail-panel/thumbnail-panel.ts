import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectedNodeState } from '../selected-node-state';
import { JpoNode, SpringConnection } from '../spring-connection';
import { NodeNavigator } from '../node-navigator';
import { CommonModule } from '@angular/common';

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

  navigator: NodeNavigator | null = null;
  zoomLevel = 30; // 5 to 100

  get thumbnailWidth(): number {
    const minWidth = 50;
    const maxWidth = 350;
    return minWidth + (maxWidth - minWidth) * (this.zoomLevel - 5) / 95;
  }

  get nodes(): JpoNode[] {
    if (!this.navigator) return [];
    const count = this.navigator.getNumberOfNodes();
    const result: JpoNode[] = [];
    for (let i = 0; i < count; i++) {
      const node = this.navigator.getNode(i);
      if (node) result.push(node);
    }
    return result;
  }

  ngOnInit(): void {
    this.nodeStateService.navigator$.subscribe((nav) => {
      this.navigator = nav;
    });
  }

  onThumbnailClick(node: JpoNode): void {
    this.nodeStateService.setSelectedChild(node);
  }
}
