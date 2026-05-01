import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { SpringConnection } from './spring-connection';
import { provideHttpClient } from '@angular/common/http';

describe('SpringConnection', () => {
  let service: SpringConnection;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [provideHttpClient(), provideHttpClientTesting(), SpringConnection],
    });
    service = TestBed.inject(SpringConnection);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Verify that there are no outstanding HTTP requests.
    if (httpMock) {
      httpMock.verify();
    }
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
    // The service constructor calls `loadCollection`, which makes an HTTP GET request.
    // We need to expect this request and flush it for the test to complete.
    const req = httpMock.expectOne('http://localhost:8086/api/jpo');
    expect(req.request.method).toBe('GET');
    req.flush([]); // Flush a mock response
  });
});
