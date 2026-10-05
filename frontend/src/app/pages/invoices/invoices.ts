import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApiService } from '../../api/api.service';
import { Client, Invoice } from '../../api/models';
import { describeError } from '../../core/errors';

@Component({
  selector: 'app-invoices',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  template: `
    <div class="row">
      <h1>Invoices</h1>
      <a class="button" routerLink="/invoices/new">New invoice</a>
    </div>

    @if (error()) {
      <p class="error" role="alert">{{ error() }}</p>
    } @else if (loaded() && invoices().length === 0) {
      <p class="muted">No invoices yet.</p>
    } @else if (loaded()) {
      <table class="card">
        <thead>
          <tr>
            <th>Number</th><th>Client</th><th>Status</th><th>Due</th><th class="num">Total</th>
          </tr>
        </thead>
        <tbody>
          @for (invoice of invoices(); track invoice.id) {
            <tr>
              <td>{{ invoice.number }}</td>
              <td>{{ clientName(invoice.clientId) }}</td>
              <td><span class="badge">{{ invoice.status }}</span></td>
              <td>{{ invoice.dueDate | date: 'mediumDate' }}</td>
              <td class="num">{{ invoice.totalCents / 100 | currency: invoice.currency }}</td>
            </tr>
          }
        </tbody>
      </table>
    } @else {
      <p class="muted">Loading...</p>
    }
  `,
})
export class InvoicesPage {
  private readonly api = inject(ApiService);

  protected readonly invoices = signal<Invoice[]>([]);
  private readonly clients = signal<Client[]>([]);
  protected readonly loaded = signal(false);
  protected readonly error = signal<string | null>(null);

  private readonly clientNames = computed(
    () => new Map(this.clients().map((client) => [client.id, client.name])),
  );

  constructor() {
    forkJoin({ invoices: this.api.invoices(), clients: this.api.clients() })
      .pipe(takeUntilDestroyed(inject(DestroyRef)))
      .subscribe({
        next: ({ invoices, clients }) => {
          this.invoices.set(invoices);
          this.clients.set(clients);
          this.loaded.set(true);
        },
        error: (err: unknown) => this.error.set(describeError(err)),
      });
  }

  protected clientName(id: string): string {
    return this.clientNames().get(id) ?? 'Unknown client';
  }
}
