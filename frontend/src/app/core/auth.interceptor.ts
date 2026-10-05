import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

const isApiCall = (url: string) => url.startsWith('/api/');
const isAuthCall = (url: string) => url.startsWith('/api/auth/');

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();

  const request =
    token && isApiCall(req.url) && !isAuthCall(req.url)
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(request).pipe(
    catchError((error: unknown) => {
      // An expired or invalid token on a protected call ends the session.
      if (error instanceof HttpErrorResponse && error.status === 401 && !isAuthCall(req.url)) {
        auth.logout();
      }
      return throwError(() => error);
    }),
  );
};
