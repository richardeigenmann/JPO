import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ThumbnailPanel } from './thumbnail-panel';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SelectedNodeState } from '../selected-node-state';
import { of } from 'rxjs';

describe('ThumbnailPanel', () => {
  let component: ThumbnailPanel;
  let fixture: ComponentFixture<ThumbnailPanel>;

  const mockNodeStateService = {
    // Provide a mocked observable for selectedJpoNode$
    selectedJpoNode$: of(null),
    getCurrentJpoNode: () => null
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThumbnailPanel, HttpClientTestingModule],
      providers: [
        // CRUCIAL FIX: Provide the mock for the service that ThumbnailPanel injects
        { provide: SelectedNodeState, useValue: mockNodeStateService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ThumbnailPanel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.myNode).toBeNull();
  });
});
