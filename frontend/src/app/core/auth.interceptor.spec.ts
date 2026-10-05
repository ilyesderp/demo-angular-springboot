import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

describe('authInterceptor', () => {
  let client: HttpClient;
  let http: HttpTestingController;
  let auth: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([{ path: 'login', children: [] }]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    client = TestBed.inject(HttpClient);
    http = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(AuthService);
  });

  function signIn(): void {
    auth.login('a@b.test', 'secret-password').subscribe();
    http
      .expectOne('/api/auth/login')
      .flush({ token: 'jwt-token', userId: 'u', workspaceId: 'w', email: 'a@b.test' });
  }

  it('adds the bearer token to API calls', () => {
    signIn();
    client.get('/api/clients').subscribe();
    expect(http.expectOne('/api/clients').request.headers.get('Authorization')).toBe('Bearer jwt-token');
  });

  it('does not attach the token to auth calls', () => {
    signIn();
    client.post('/api/auth/login', {}).subscribe();
    expect(http.expectOne('/api/auth/login').request.headers.has('Authorization')).toBe(false);
  });

  it('signs the user out when a protected call returns 401', () => {
    signIn();
    client.get('/api/clients').subscribe({ error: () => undefined });
    http.expectOne('/api/clients').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(auth.isAuthenticated()).toBe(false);
  });
});
