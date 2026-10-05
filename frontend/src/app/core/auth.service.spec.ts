import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let auth: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideRouter([{ path: 'login', children: [] }]), provideHttpClient(), provideHttpClientTesting()],
    });
    auth = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  it('starts signed out', () => {
    expect(auth.isAuthenticated()).toBe(false);
    expect(auth.token()).toBeNull();
  });

  it('stores the token after a successful login', () => {
    auth.login('a@b.test', 'secret-password').subscribe();
    const request = http.expectOne('/api/auth/login');
    expect(request.request.body).toEqual({ email: 'a@b.test', password: 'secret-password' });
    request.flush({ token: 'jwt-token', userId: 'u', workspaceId: 'w', email: 'a@b.test' });

    expect(auth.isAuthenticated()).toBe(true);
    expect(auth.token()).toBe('jwt-token');
    expect(localStorage.getItem('ledger.token')).toBe('jwt-token');
  });

  it('clears the token on logout', () => {
    auth.login('a@b.test', 'secret-password').subscribe();
    http
      .expectOne('/api/auth/login')
      .flush({ token: 'jwt-token', userId: 'u', workspaceId: 'w', email: 'a@b.test' });

    auth.logout();

    expect(auth.isAuthenticated()).toBe(false);
    expect(localStorage.getItem('ledger.token')).toBeNull();
  });
});
