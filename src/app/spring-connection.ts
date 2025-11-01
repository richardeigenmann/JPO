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

  constructor( private http: HttpClient ) {
    console.log('SpringConnectionService Initialized.');
    this.loadCollection();
  }

  /**
   * Fetches the collection data from the remote URL and updates the signal.
   * Components should call this method to load the data.
   */
  loadCollection(): void {
    console.log('Fetching collection data from URL: ' + SPRING_CONNECTION_URL);
    this.http.get<any>(SPRING_CONNECTION_URL)
      .pipe(
        tap(data => {
          console.log('Collection data loaded successfully.');
          this.treeData.set(data);
        })
      )
      .subscribe({
        error: (error) => console.error('Failed to load beer data', error)
      });
  }

}

const SPRING_CONNECTION_URL = 'http://localhost:8001/api/jpo';
