import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { JpoNode } from './spring-connection';
import { NodeNavigator, GroupNavigator } from './node-navigator';

@Injectable({
  providedIn: 'root',
})
export class SelectedNodeState {
  private navigatorSubject = new BehaviorSubject<NodeNavigator | null>(null);
  public navigator$: Observable<NodeNavigator | null> = this.navigatorSubject.asObservable();

  private selectedChildSubject = new BehaviorSubject<JpoNode | null>(null);
  public selectedChild$: Observable<JpoNode | null> = this.selectedChildSubject.asObservable();

  /**
   * Sets a new navigator.
   */
  setNavigator(navigator: NodeNavigator): void {
    this.navigatorSubject.next(navigator);
    this.selectedChildSubject.next(null);
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
      this.selectedChildSubject.next(node);
    }
  }

  /**
   * Navigates to the next node in the current navigator.
   */
  next(): void {
    const nav = this.navigatorSubject.getValue();
    const current = this.selectedChildSubject.getValue();
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
    const nav = this.navigatorSubject.getValue();
    const current = this.selectedChildSubject.getValue();
    if (nav && current) {
      const index = nav.getIndex(current);
      if (index > 0) {
        this.setSelectedChild(nav.getNode(index - 1));
      }
    }
  }

  // Compatibility with old code if needed
  public selectedJpoNode$: Observable<JpoNode | null> = this.selectedChild$;

  setSelectedNodeId(node: JpoNode): void {
    this.setSelectedChild(node);
  }

  getCurrentNodeId(): JpoNode | null {
    return this.selectedChildSubject.getValue();
  }
}
