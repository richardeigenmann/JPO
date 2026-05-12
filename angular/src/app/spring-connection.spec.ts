import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { SpringConnection } from './spring-connection';
import { provideHttpClient } from '@angular/common/http';

describe('SpringConnection', () => {
  let service: SpringConnection;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        SpringConnection
      ],
    });
    service = TestBed.inject(SpringConnection);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', async () => {
    expect(service).toBeTruthy();
    
    // Accessing value to trigger the resource
    service.resource.value();

    // Small delay to allow internal effects to run
    await new Promise(resolve => setTimeout(resolve, 0));

    const req = httpMock.expectOne('/api/jpo');
    expect(req.request.method).toBe('GET');
    req.flush([]); 
  });

  it('search should make a GET request', () => {
    service.search('test').subscribe();

    const req = httpMock.expectOne('/api/jpo/search?query=test');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});
