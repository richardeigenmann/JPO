import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { SpringConnection } from './spring-connection';
import { SelectedNodeState } from './selected-node-state';
import { signal } from '@angular/core';

describe('App', () => {
  const mockSpringConnection = {
    treeData: signal([]),
    isLoading: signal(false),
    error: signal(null),
    connectionStatus: signal('Some status to avoid error'),
  };

  const mockNodeStateService = {
    navigator: signal(null),
    selectedChild: signal(null),
    searchResults: signal(null),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SpringConnection, useValue: mockSpringConnection },
        { provide: SelectedNodeState, useValue: mockNodeStateService },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render title', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('h1')?.textContent).toContain('Welcome to JPO');
  });
});
