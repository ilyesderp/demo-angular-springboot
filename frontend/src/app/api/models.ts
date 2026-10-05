export interface AuthResult {
  token: string;
  userId: string;
  workspaceId: string;
  email: string;
}

export interface Client {
  id: string;
  name: string;
  email: string | null;
}

export type InvoiceStatus = 'DRAFT' | 'SENT' | 'PAID' | 'OVERDUE' | 'VOID';

export interface InvoiceItem {
  description: string;
  quantity: number;
  unitPriceCents: number;
  totalCents: number;
}

export interface Invoice {
  id: string;
  clientId: string;
  number: string;
  status: InvoiceStatus;
  currency: string;
  issueDate: string;
  dueDate: string;
  totalCents: number;
  items: InvoiceItem[];
}

export interface DashboardSummary {
  draftCents: number;
  outstandingCents: number;
  paidCents: number;
  invoiceCount: number;
  clientCount: number;
}

export interface CreateClientRequest {
  name: string;
  email: string | null;
}

export interface CreateInvoiceRequest {
  clientId: string;
  currency: string;
  issueDate: string;
  dueDate: string;
  items: { description: string; quantity: number; unitPriceCents: number }[];
}
