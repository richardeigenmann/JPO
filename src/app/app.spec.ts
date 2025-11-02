import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SpringConnection } from './spring-connection';
import { signal } from '@angular/core';

const mockSpringConnectionForApp = {
  treeData: signal([]),
  connectionStatus: signal('Some status to avoid error'),
  // Add any other required methods/signals
};

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideHttpClientTesting(),
        // Provide a mock for SpringConnection to avoid instantiating the real service
        { provide: SpringConnection, useValue: mockSpringConnectionForApp }
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
    expect(compiled.querySelector('h1')?.textContent).toContain('JpoAngular');
  });
});
