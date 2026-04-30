import { Component, inject, signal } from '@angular/core';
 import { MatTreeModule } from '@angular/material/tree';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import { JpoNode, SpringConnection } from '../spring-connection';
import { MatCardModule } from '@angular/material/card';
import { NestedTreeControl } from '@angular/cdk/tree';
import { SelectedNodeState } from '../selected-node-state';

@Component({
  selector: 'app-collection-tree',
  imports: [MatCardModule, MatTreeModule, MatButtonModule, MatIconModule],
  templateUrl: './collection-tree.html',
  styleUrl: './collection-tree.css',
  standalone: true
})
export class CollectionTree {
  springService = inject(SpringConnection);
  nodeStateService = inject(SelectedNodeState);

  treeData = this.springService.treeData;
  childrenAccessor = (node: JpoNode) => node.children ?? [];
  hasChild = (_: number, node: JpoNode) => !!node.children && node.children.length > 0;

  clickedNodeLabel = signal('None');

  // Initialize Tree Control
  treeControl = new NestedTreeControl<JpoNode>((node) => node.children);

  handleNodeClick(node: JpoNode): void {
    console.log('Node clicked:', node);
    this.nodeStateService.setSelectedChild(node);
    this.clickedNodeLabel.set(node.label);
    // Optional: toggle expansion for parent nodes on click (in addition to the icon)
    if (this.hasChild(0, node)) {
      this.treeControl.toggle(node);
    }
  }
}

