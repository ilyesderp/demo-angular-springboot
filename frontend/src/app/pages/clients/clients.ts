import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../api/api.service';
import { Client } from '../../api/models';
import { describeError } from '../../core/errors';

@Component({
  selector: 'app-clients',
  imports: [ReactiveFormsModule],
  template: `
    <h1>Clients</h1>

    <section class="card" style="margin-bottom: 1.5rem">
      <form [formGroup]="form" (ngSubmit)="add()">
        <label>
          Name
          <input type="text" formControlName="name" />
        </label>
        <label>
          Email (optional)
          <input type="email" formControlName="email" />
        </label>
        @if (formError()) {
          <p class="error" role="alert">{{ formError() }}</p>
        }
        <button type="submit" [disabled]="saving()">Add client</button>
      </form>
    </section>

    @if (loadError()) {
      <p class="error" role="alert">{{ loadError() }}</p>
    } @else if (clients().length === 0) {
      <p class="muted">No clients yet.</p>
    } @else {
      <table class="card">
        <thead>
          <tr><th>Name</th><th>Email</th></tr>
        </thead>
        <tbody>
          @for (client of clients(); track client.id) {
            <tr>
              <td>{{ client.name }}</td>
              <td class="muted">{{ client.email ?? '' }}</td>
            </tr>
          }
        </tbody>
      </table>
    }
  `,
})
export class ClientsPage {
  private readonly api = inject(ApiService);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(200)]],
    email: ['', Validators.email],
  });
  protected readonly clients = signal<Client[]>([]);
  protected readonly loadError = signal<string | null>(null);
  protected readonly formError = signal<string | null>(null);
  protected readonly saving = signal(false);

  constructor() {
    this.load();
  }

  protected add(): void {
    if (this.form.invalid) {
      this.formError.set('Enter a name, and a valid email if you provide one.');
      return;
    }
    this.formError.set(null);
    this.saving.set(true);
    const { name, email } = this.form.getRawValue();
    this.api
      .createClient({ name, email: email || null })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.form.reset();
          this.saving.set(false);
          this.load();
        },
        error: (err: unknown) => {
          this.formError.set(describeError(err));
          this.saving.set(false);
        },
      });
  }

  private load(): void {
    this.api
      .clients()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (clients) => this.clients.set(clients),
        error: (err: unknown) => this.loadError.set(describeError(err)),
      });
  }
}
