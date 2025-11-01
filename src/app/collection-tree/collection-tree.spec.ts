import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollectionTree } from './collection-tree';
import { SpringConnection } from '../spring-connection';
import { signal } from '@angular/core';

describe('CollectionTree', () => {
  let component: CollectionTree;
  let fixture: ComponentFixture<CollectionTree>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CollectionTree,],
      providers: [
        // Provide a mock for SpringConnection. This is better for isolating the component
        // and avoids needing the HttpClient provider here.
        { provide: SpringConnection, useValue: { treeData: signal([]) } }
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollectionTree);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
