import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./pages/login/login').then((m) => m.LoginPage),
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    loadComponent: () => import('./pages/register/register').then((m) => m.RegisterPage),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.DashboardPage),
  },
  {
    path: 'clients',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/clients/clients').then((m) => m.ClientsPage),
  },
  {
    path: 'invoices',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/invoices/invoices').then((m) => m.InvoicesPage),
  },
  {
    path: 'invoices/new',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/invoices/invoice-form').then((m) => m.InvoiceFormPage),
  },
  { path: '**', redirectTo: 'dashboard' },
];
