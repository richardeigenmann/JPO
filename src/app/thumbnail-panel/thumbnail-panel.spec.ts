import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ThumbnailPanel } from './thumbnail-panel';

describe('ThumbnailPanel', () => {
  let component: ThumbnailPanel;
  let fixture: ComponentFixture<ThumbnailPanel>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThumbnailPanel]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ThumbnailPanel);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
