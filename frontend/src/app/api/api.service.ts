import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Client,
  CreateClientRequest,
  CreateInvoiceRequest,
  DashboardSummary,
  Invoice,
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  dashboard(): Observable<DashboardSummary> {
    return this.http.get<DashboardSummary>('/api/dashboard');
  }

  clients(): Observable<Client[]> {
    return this.http.get<Client[]>('/api/clients');
  }

  createClient(request: CreateClientRequest): Observable<Client> {
    return this.http.post<Client>('/api/clients', request);
  }

  invoices(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>('/api/invoices');
  }

  createInvoice(request: CreateInvoiceRequest): Observable<Invoice> {
    return this.http.post<Invoice>('/api/invoices', request);
  }
}
