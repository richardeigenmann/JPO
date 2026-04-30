import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { JpoNode, SpringConnection } from '../spring-connection';
import { SelectedNodeState } from '../selected-node-state';

@Component({
  selector: 'app-picture-viewer',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule],
  templateUrl: './picture-viewer.html',
  styleUrl: './picture-viewer.css'
})
export class PictureViewer {
  @Input() node!: JpoNode;
  
  springService = inject(SpringConnection);
  nodeStateService = inject(SelectedNodeState);

  onNext() {
    this.nodeStateService.next();
  }

  onPrevious() {
    this.nodeStateService.previous();
  }

  onBack() {
    this.nodeStateService.setSelectedChild(null);
  }
}
