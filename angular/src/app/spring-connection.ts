import { Injectable } from '@angular/core';
import { httpResource } from '@angular/common/http';

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
  readonly SPRING_CONNECTION_URL = 'http://localhost:8001/api/jpo';

  // Modern way to fetch data in Angular 19+
  readonly resource = httpResource<JpoNode[]>(() => this.SPRING_CONNECTION_URL);

  // Computed signals for backward compatibility and ease of use
  readonly treeData = () => this.resource.value() ?? [];
  readonly isLoading = this.resource.isLoading;
  readonly error = this.resource.error;

  constructor() {
    console.log('SpringConnectionService Initialized with httpResource.');
  }

}
