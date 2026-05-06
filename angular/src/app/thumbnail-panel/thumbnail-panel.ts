import { Component, inject, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectedNodeState } from '../selected-node-state';
import { JpoNode, SpringConnection } from '../spring-connection';
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

  // Derived signals using computed
  navigator = this.nodeStateService.navigator;
  
  nodes = computed(() => {
    const searchResults = this.nodeStateService.searchResults();
    if (searchResults) return searchResults;

    const nav = this.navigator();
    if (!nav) return [];
    const count = nav.getNumberOfNodes();
    const result: JpoNode[] = [];
    for (let i = 0; i < count; i++) {
      const node = nav.getNode(i);
      if (node) result.push(node);
    }
    return result;
  });

  zoomLevel = 30; // 5 to 100
  searchTerm = '';

  get thumbnailWidth(): number {
    const minWidth = 50;
    const maxWidth = 350;
    return minWidth + (maxWidth - minWidth) * (this.zoomLevel - 5) / 95;
  }

  onSearch(): void {
    if (this.searchTerm.trim() === '') return;
    this.springService.search(this.searchTerm).subscribe(results => {
       this.nodeStateService.setSearchResults(results);
    });
  }

  onThumbnailClick(node: JpoNode): void {
    this.nodeStateService.setSelectedChild(node);
  }
}
