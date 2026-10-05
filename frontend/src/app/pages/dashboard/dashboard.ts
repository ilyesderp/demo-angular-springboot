import { CurrencyPipe } from '@angular/common';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../api/api.service';
import { DashboardSummary } from '../../api/models';
import { describeError } from '../../core/errors';

@Component({
  selector: 'app-dashboard',
  imports: [CurrencyPipe, RouterLink],
  template: `
    <div class="row">
      <h1>Dashboard</h1>
      <a class="button" routerLink="/invoices/new">New invoice</a>
    </div>

    @if (error()) {
      <p class="error" role="alert">{{ error() }}</p>
    } @else if (summary(); as s) {
      <div class="grid">
        <div class="card stat">
          <div class="label">Outstanding</div>
          <div class="value">{{ s.outstandingCents / 100 | currency: 'EUR' }}</div>
        </div>
        <div class="card stat">
          <div class="label">Paid</div>
          <div class="value">{{ s.paidCents / 100 | currency: 'EUR' }}</div>
        </div>
        <div class="card stat">
          <div class="label">Drafts</div>
          <div class="value">{{ s.draftCents / 100 | currency: 'EUR' }}</div>
        </div>
        <div class="card stat">
          <div class="label">Invoices</div>
          <div class="value">{{ s.invoiceCount }}</div>
        </div>
        <div class="card stat">
          <div class="label">Clients</div>
          <div class="value">{{ s.clientCount }}</div>
        </div>
      </div>
      @if (s.clientCount === 0) {
        <p class="card">Start by <a routerLink="/clients">adding your first client</a>, then create an invoice.</p>
      }
    } @else {
      <p class="muted">Loading...</p>
    }
  `,
})
export class DashboardPage {
  private readonly api = inject(ApiService);

  protected readonly summary = signal<DashboardSummary | null>(null);
  protected readonly error = signal<string | null>(null);

  constructor() {
    this.api
      .dashboard()
      .pipe(takeUntilDestroyed(inject(DestroyRef)))
      .subscribe({
        next: (summary) => this.summary.set(summary),
        error: (err: unknown) => this.error.set(describeError(err)),
      });
  }
}
