import { Injectable, inject, InjectionToken, computed } from '@angular/core';
import { HttpClient, httpResource, HttpErrorResponse } from '@angular/common/http';
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

export interface UserProfile {
  name?: string;
  email?: string;
  picture?: string;
}

@Injectable({
  providedIn: 'root',
})
export class SpringConnection {
  private http = inject(HttpClient);
  public readonly baseUrl = inject(API_BASE_URL);

  // Modern way to fetch data in Angular 19+
  readonly resource = httpResource<JpoNode[]>(() => this.baseUrl);
  readonly userResource = httpResource<UserProfile>(() => '/api/user/me');

  // Computed signals for backward compatibility and ease of use
  readonly treeData = () => this.resource.value() ?? [];
  readonly isLoading = this.resource.isLoading;
  readonly error = this.resource.error;
  readonly user = () => this.userResource.value();

  readonly isUnauthenticated = computed(() => {
    const err = this.resource.error();
    if (err instanceof HttpErrorResponse) {
      return err.status === 401;
    }
    return (err as any)?.status === 401;
  });

  constructor() {
    console.log(`SpringConnectionService Initialized with URL: ${this.baseUrl}`);
  }

  search(term: string): Observable<JpoNode[]> {
    return this.http.get<JpoNode[]>(`${this.baseUrl}/search?query=${term}`);
  }

  logout(): void {
    window.location.href = '/logout';
  }
}
