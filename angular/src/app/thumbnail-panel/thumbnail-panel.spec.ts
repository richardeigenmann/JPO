import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ThumbnailPanel } from './thumbnail-panel';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { SelectedNodeState } from '../selected-node-state';
import { SpringConnection } from '../spring-connection';
import { signal } from '@angular/core';

describe('ThumbnailPanel', () => {
  let component: ThumbnailPanel;
  let fixture: ComponentFixture<ThumbnailPanel>;

  const mockNodeStateService = {
    navigator: signal(null),
    searchResults: signal(null),
    setSearchResults: jasmine.createSpy('setSearchResults'),
    setSelectedChild: jasmine.createSpy('setSelectedChild'),
  };

  const mockSpringConnection = {
    search: jasmine.createSpy('search'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThumbnailPanel],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SelectedNodeState, useValue: mockNodeStateService },
        { provide: SpringConnection, useValue: mockSpringConnection },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ThumbnailPanel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default zoom level and search term', () => {
    expect(component.zoomLevel).toBe(30);
    expect(component.searchTerm).toBe('');
  });

  it('should calculate thumbnailWidth correctly', () => {
    // zoomLevel 30: 50 + (350 - 50) * (30 - 5) / 95 = 50 + 300 * 25 / 95 ≈ 128.9
    expect(component.thumbnailWidth).toBeCloseTo(128.9, 1);
  });
});
