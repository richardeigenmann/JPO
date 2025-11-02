import { TestBed } from '@angular/core/testing';

import { SelectedNodeState } from './selected-node-state';

describe('SelectedNodeState', () => {
  let service: SelectedNodeState;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SelectedNodeState);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
