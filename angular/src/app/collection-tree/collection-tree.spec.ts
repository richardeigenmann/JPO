import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CollectionTree } from './collection-tree';
import { SpringConnection } from '../spring-connection';
import { signal } from '@angular/core';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { SelectedNodeState } from '../selected-node-state';

describe('CollectionTree', () => {
  let component: CollectionTree;
  let fixture: ComponentFixture<CollectionTree>;

  const mockSpringConnection = {
    treeData: signal([]),
    isLoading: signal(false),
    error: signal(null),
    connectionStatus: signal('Test Status MOCKED'),
  };

  const mockNodeStateService = {
    setSelectedChild: jasmine.createSpy('setSelectedChild'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CollectionTree],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: SpringConnection, useValue: mockSpringConnection },
        { provide: SelectedNodeState, useValue: mockNodeStateService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CollectionTree);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Connected');
    expect(component).toBeTruthy();
  });
});
