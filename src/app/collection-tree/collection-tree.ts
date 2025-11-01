import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common'; // Needed for basic directives
import { MatTreeModule } from '@angular/material/tree';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import { JpoNode, SpringConnection } from '../spring-connection';

@Component({
  selector: 'app-collection-tree',
  imports: [CommonModule, MatTreeModule, MatButtonModule, MatIconModule],
  templateUrl: './collection-tree.html',
  styleUrl: './collection-tree.css',
  standalone: true
})
export class CollectionTree {
  private springService = inject(SpringConnection);

  treeData = this.springService.treeData;
  childrenAccessor = (node: JpoNode) => node.children ?? [];
  hasChild = (_: number, node: JpoNode) => !!node.children && node.children.length > 0;
}

