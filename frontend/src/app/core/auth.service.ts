import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResult } from '../api/models';

const TOKEN_KEY = 'ledger.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly tokenState = signal<string | null>(readStoredToken());

  readonly isAuthenticated = computed(() => this.tokenState() !== null);

  token(): string | null {
    return this.tokenState();
  }

  login(email: string, password: string): Observable<AuthResult> {
    return this.http
      .post<AuthResult>('/api/auth/login', { email, password })
      .pipe(tap((result) => this.storeToken(result.token)));
  }

  register(workspaceName: string, email: string, password: string): Observable<AuthResult> {
    return this.http
      .post<AuthResult>('/api/auth/register', { workspaceName, email, password })
      .pipe(tap((result) => this.storeToken(result.token)));
  }

  logout(): void {
    this.storeToken(null);
    void this.router.navigateByUrl('/login');
  }

  private storeToken(token: string | null): void {
    this.tokenState.set(token);
    try {
      if (token) {
        localStorage.setItem(TOKEN_KEY, token);
      } else {
        localStorage.removeItem(TOKEN_KEY);
      }
    } catch {
      // Storage can be unavailable (private mode); the session just won't survive a reload.
    }
  }
}

function readStoredToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}
