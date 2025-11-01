import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

export interface JpoNode {
  label: string;
  children?: JpoNode[];
}

@Injectable({
  providedIn: 'root',
})
export class SpringConnection {
  treeData = signal<JpoNode[]>([]);

  constructor(private http: HttpClient) {
    this._status.set('Initializing Connection and trying fetch.');
    console.log('SpringConnectionService Initialized.');
    this.loadCollection();
  }

  /**
   * Fetches the collection data from the remote URL and updates the signal.
   * Components should call this method to load the data.
   */
  loadCollection(): void {
    console.log('Fetching collection data from URL: ' + SPRING_CONNECTION_URL);
    this._status.set('Fetching collection data from URL: ' + SPRING_CONNECTION_URL);
    this.http
      .get<any>(SPRING_CONNECTION_URL)
      .pipe(
        tap((data) => {
          console.log('Collection data loaded successfully.');
          this._status.set('Collection data loaded successfully.');
          this.treeData.set(data);
        })
      )
      .subscribe({
        error: (error) => {
          console.error('Failed to load collection data', error);
          this._status.set('Failed to load collection data' + error);
      },
      });
  }

  // Private writable signal
  private _status = signal<string>('');

  // Public readonly signal
  readonly connectionStatus = this._status.asReadonly();

  setConnectionStatus(newStatus: string): void {
    this._status.set(newStatus);
  }
}

const SPRING_CONNECTION_URL = 'http://localhost:8001/api/jpo';
