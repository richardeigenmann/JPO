import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common'; // Needed for basic directives
import { MatTreeModule } from '@angular/material/tree';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import { JpoNode, SpringConnection } from '../spring-connection';
import { MatCardModule } from '@angular/material/card';
import { NestedTreeControl } from '@angular/cdk/tree';

@Component({
  selector: 'app-collection-tree',
  imports: [CommonModule, MatCardModule, MatTreeModule, MatButtonModule, MatIconModule],
  templateUrl: './collection-tree.html',
  styleUrl: './collection-tree.css',
  standalone: true
})
export class CollectionTree {
  springService = inject(SpringConnection);

  treeData = this.springService.treeData;
  childrenAccessor = (node: JpoNode) => node.children ?? [];
  hasChild = (_: number, node: JpoNode) => !!node.children && node.children.length > 0;

  clickedNodeLabel = signal('None');

  // Initialize Tree Control
  treeControl = new NestedTreeControl<JpoNode>((node) => node.children);

  handleNodeClick(node: JpoNode): void {
    console.log('Node clicked:', node.label);
    this.clickedNodeLabel.set(node.label);
    // Optional: toggle expansion for parent nodes on click (in addition to the icon)
    if (this.hasChild(0, node)) {
      this.treeControl.toggle(node);
    }
  }
}

