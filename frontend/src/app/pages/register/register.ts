import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { describeError } from '../../core/errors';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <section class="card card-narrow">
      <h1>Create your workspace</h1>
      @if (error()) {
        <p class="error" role="alert">{{ error() }}</p>
      }
      <form [formGroup]="form" (ngSubmit)="submit()">
        <label>
          Workspace name
          <input type="text" formControlName="workspaceName" placeholder="e.g. Acme Studio" />
        </label>
        <label>
          Email
          <input type="email" formControlName="email" autocomplete="email" />
        </label>
        <label>
          Password (8 characters or more)
          <input type="password" formControlName="password" autocomplete="new-password" />
        </label>
        <button type="submit" [disabled]="submitting()">Create account</button>
      </form>
      <p class="muted">Already registered? <a routerLink="/login">Log in</a></p>
    </section>
  `,
})
export class RegisterPage {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly form = this.fb.group({
    workspaceName: ['', [Validators.required, Validators.maxLength(200)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
  });
  protected readonly error = signal<string | null>(null);
  protected readonly submitting = signal(false);

  protected submit(): void {
    if (this.form.invalid) {
      this.error.set('Fill in all fields. The password needs at least 8 characters.');
      return;
    }
    this.error.set(null);
    this.submitting.set(true);
    const { workspaceName, email, password } = this.form.getRawValue();
    this.auth.register(workspaceName, email, password).subscribe({
      next: () => void this.router.navigateByUrl('/dashboard'),
      error: (err: unknown) => {
        this.error.set(describeError(err));
        this.submitting.set(false);
      },
    });
  }
}
