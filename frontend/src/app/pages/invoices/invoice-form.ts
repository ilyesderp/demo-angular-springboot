import { CurrencyPipe } from '@angular/common';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../api/api.service';
import { Client } from '../../api/models';
import { describeError } from '../../core/errors';
import { toCents } from '../../core/money';

const CURRENCY = 'EUR';

function isoDate(daysFromToday: number): string {
  const date = new Date();
  date.setDate(date.getDate() + daysFromToday);
  return date.toISOString().slice(0, 10);
}

@Component({
  selector: 'app-invoice-form',
  imports: [ReactiveFormsModule, CurrencyPipe, RouterLink],
  template: `
    <h1>New invoice</h1>

    @if (clientsLoaded() && clients().length === 0) {
      <p class="card">You need a client first. <a routerLink="/clients">Add a client</a>.</p>
    } @else {
      <form class="card" [formGroup]="form" (ngSubmit)="save()">
        <label>
          Client
          <select formControlName="clientId">
            <option value="" disabled>Select a client</option>
            @for (client of clients(); track client.id) {
              <option [value]="client.id">{{ client.name }}</option>
            }
          </select>
        </label>
        <div class="grid">
          <label>
            Issue date
            <input type="date" formControlName="issueDate" />
          </label>
          <label>
            Due date
            <input type="date" formControlName="dueDate" />
          </label>
        </div>

        <h2>Items</h2>
        <div formArrayName="items">
          @for (item of items.controls; track item; let i = $index) {
            <div class="grid" [formGroupName]="i">
              <label>
                Description
                <input type="text" formControlName="description" />
              </label>
              <label>
                Quantity
                <input type="number" min="1" step="1" formControlName="quantity" />
              </label>
              <label>
                Unit price ({{ currency }})
                <input type="number" min="0" step="0.01" formControlName="unitPrice" />
              </label>
              <div>
                <button
                  type="button"
                  class="secondary"
                  [disabled]="items.length === 1"
                  (click)="removeItem(i)"
                >
                  Remove
                </button>
              </div>
            </div>
          }
        </div>
        <p><button type="button" class="secondary" (click)="addItem()">Add item</button></p>

        <p><strong>Total: {{ total() | currency: currency }}</strong></p>

        @if (error()) {
          <p class="error" role="alert">{{ error() }}</p>
        }
        <button type="submit" [disabled]="saving()">Save draft</button>
      </form>
    }
  `,
})
export class InvoiceFormPage {
  private readonly api = inject(ApiService);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly currency = CURRENCY;
  protected readonly clients = signal<Client[]>([]);
  protected readonly clientsLoaded = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly saving = signal(false);

  protected readonly form = this.fb.group({
    clientId: ['', Validators.required],
    issueDate: [isoDate(0), Validators.required],
    dueDate: [isoDate(30), Validators.required],
    items: this.fb.array([this.newItem()]),
  });

  protected get items() {
    return this.form.controls.items;
  }

  constructor() {
    this.api
      .clients()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (clients) => {
          this.clients.set(clients);
          this.clientsLoaded.set(true);
        },
        error: (err: unknown) => this.error.set(describeError(err)),
      });
  }

  protected addItem(): void {
    this.items.push(this.newItem());
  }

  protected removeItem(index: number): void {
    if (this.items.length > 1) {
      this.items.removeAt(index);
    }
  }

  protected total(): number {
    return this.items.controls.reduce((sum, item) => {
      const { quantity, unitPrice } = item.getRawValue();
      return sum + quantity * toCents(unitPrice);
    }, 0) / 100;
  }

  protected save(): void {
    if (this.form.invalid) {
      this.error.set('Choose a client, set the dates, and complete every item.');
      return;
    }
    const value = this.form.getRawValue();
    this.error.set(null);
    this.saving.set(true);
    this.api
      .createInvoice({
        clientId: value.clientId,
        currency: CURRENCY,
        issueDate: value.issueDate,
        dueDate: value.dueDate,
        items: value.items.map((item) => ({
          description: item.description,
          quantity: item.quantity,
          unitPriceCents: toCents(item.unitPrice),
        })),
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => void this.router.navigateByUrl('/invoices'),
        error: (err: unknown) => {
          this.error.set(describeError(err));
          this.saving.set(false);
        },
      });
  }

  private newItem() {
    return this.fb.group({
      description: ['', [Validators.required, Validators.maxLength(500)]],
      quantity: [1, [Validators.required, Validators.min(1)]],
      unitPrice: [0, [Validators.required, Validators.min(0)]],
    });
  }
}
