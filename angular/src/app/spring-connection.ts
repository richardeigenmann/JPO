import { Injectable, inject, InjectionToken } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './app.config.tokens';

export interface PictureDTO {
  id: string;
  title: string;
  thumbnailUrl: string; // URL for the thumbnail image
  fullImageUrl: string; // URL for the full image
}

export interface JpoNode {
  id: string;
  label: string;
  isGroup: boolean;
  picture?: PictureDTO;
  children?: JpoNode[];
}

@Injectable({
  providedIn: 'root',
})
export class SpringConnection {
  private http = inject(HttpClient);
  public readonly baseUrl = inject(API_BASE_URL);

  // Modern way to fetch data in Angular 19+
  readonly resource = httpResource<JpoNode[]>(() => this.baseUrl);

  // Computed signals for backward compatibility and ease of use
  readonly treeData = () => this.resource.value() ?? [];
  readonly isLoading = this.resource.isLoading;
  readonly error = this.resource.error;

  constructor() {
    console.log(`SpringConnectionService Initialized with URL: ${this.baseUrl}`);
  }

  search(term: string): Observable<JpoNode[]> {
    return this.http.get<JpoNode[]>(`${this.baseUrl}/search?query=${term}`);
  }
}
