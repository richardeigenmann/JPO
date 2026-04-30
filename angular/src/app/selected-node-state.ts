import { Injectable, signal, computed } from '@angular/core';
import { JpoNode } from './spring-connection';
import { NodeNavigator, GroupNavigator } from './node-navigator';

@Injectable({
  providedIn: 'root',
})
export class SelectedNodeState {
  // Private signals for state
  private _navigator = signal<NodeNavigator | null>(null);
  private _selectedChild = signal<JpoNode | null>(null);

  // Public readonly signals
  public readonly navigator = this._navigator.asReadonly();
  public readonly selectedChild = this._selectedChild.asReadonly();

  /**
   * Sets a new navigator.
   */
  setNavigator(navigator: NodeNavigator): void {
    this._navigator.set(navigator);
    this._selectedChild.set(null);
  }

  /**
   * Sets the GroupNavigator for the given group node.
   */
  setGroupNavigator(groupNode: JpoNode): void {
    this.setNavigator(new GroupNavigator(groupNode));
  }

  /**
   * Updates the selected child within the current navigator.
   * If the node is a group, it sets it as a new GroupNavigator instead.
   */
  setSelectedChild(node: JpoNode | null): void {
    if (node?.isGroup) {
      this.setGroupNavigator(node);
    } else {
      this._selectedChild.set(node);
    }
  }

  /**
   * Navigates to the next node in the current navigator.
   */
  next(): void {
    const nav = this._navigator();
    const current = this._selectedChild();
    if (nav && current) {
      const index = nav.getIndex(current);
      if (index !== -1 && index < nav.getNumberOfNodes() - 1) {
        this.setSelectedChild(nav.getNode(index + 1));
      }
    }
  }

  /**
   * Navigates to the previous node in the current navigator.
   */
  previous(): void {
    const nav = this._navigator();
    const current = this._selectedChild();
    if (nav && current) {
      const index = nav.getIndex(current);
      if (index > 0) {
        this.setSelectedChild(nav.getNode(index - 1));
      }
    }
  }

  // Compatibility methods
  setSelectedNodeId(node: JpoNode): void {
    this.setSelectedChild(node);
  }

  getCurrentNodeId(): JpoNode | null {
    return this._selectedChild();
  }
}
