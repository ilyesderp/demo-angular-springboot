import { HttpErrorResponse } from '@angular/common/http';

/** Turns an API error (RFC 7807 problem details) into a message for the user. */
export function describeError(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return 'Cannot reach the server. Is the backend running?';
    }
    const fieldErrors = error.error?.errors as Record<string, string> | undefined;
    if (fieldErrors) {
      return Object.entries(fieldErrors)
        .map(([field, message]) => `${field}: ${message}`)
        .join('; ');
    }
    if (typeof error.error?.detail === 'string') {
      return error.error.detail;
    }
  }
  return 'Something went wrong. Please try again.';
}
