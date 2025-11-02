import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionTree } from './collection-tree';
import { SpringConnection } from '../spring-connection';
import { signal } from '@angular/core';

const mockSpringConnection = {
  // Use a real signal() to mock the behavior
  treeData: signal([]),
  connectionStatus: signal('Test Status MOCKED'),

  // Note: If you had any methods (like loadCollection), they must also be mocked:
  // loadCollection: () => {},
};

describe('CollectionTree', () => {
  let component: CollectionTree;
  let fixture: ComponentFixture<CollectionTree>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CollectionTree,],
      providers: [
        // Provide a mock for SpringConnection. This is better for isolating the component
        // and avoids needing the HttpClient provider here.
        { provide: SpringConnection,
          useValue: mockSpringConnection }
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionTree);
    component = fixture.componentInstance;
    //mockService = TestBed.inject(SpringConnection);
    fixture.detectChanges();
  });

  it('should create', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Test Status MOCKED');
    expect(component).toBeTruthy();
  });
});
