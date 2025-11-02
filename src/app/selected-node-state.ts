import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { JpoNode } from './spring-connection';

@Injectable({
  providedIn: 'root',
})
export class SelectedNodeState {
  // BehaviorSubject to hold the current ID. It needs an initial value (null or a default ID).
  // The type is 'string' for your TreeNodeDTO ID.
  private selectedNodeIdSubject = new BehaviorSubject<JpoNode | null>(null);

  // Expose the value as a public, read-only Observable stream.
  // Components subscribe to this stream.
  public selectedJpoNode$: Observable<JpoNode | null> = this.selectedNodeIdSubject.asObservable();

  /**
   * Public method to update the selected node ID.
   * Any component in the application can call this method.
   */
  setSelectedNodeId(node: JpoNode): void {
    this.selectedNodeIdSubject.next(node);
  }

  /**
   * Optional: Getter for the current value (useful for synchronous lookups).
   */
  getCurrentNodeId(): JpoNode | null {
    return this.selectedNodeIdSubject.getValue();
  }

}
