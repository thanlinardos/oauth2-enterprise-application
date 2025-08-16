import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ContactComponent } from './components/contact/contact.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LogoutComponent } from './components/logout/logout.component';
import { AccountComponent } from './components/account/account.component';
import { BalanceComponent } from './components/balance/balance.component';
import { NoticesComponent } from './components/notices/notices.component';
import { LoansComponent } from './components/loans/loans.component';
import { CardsComponent } from './components/cards/cards.component';
import { AuthKeyCloakGuard } from './routeguards/auth.routeguard';
import { HomeComponent } from './components/home/home.component';
import {RegisterComponent} from "./components/register/register.component";

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full'},
  { path: 'home', component: HomeComponent},
  { path: 'contact', component: ContactComponent},
  { path: 'notices', component: NoticesComponent},
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthKeyCloakGuard], data: { roles: ['USER', 'GUEST']}},
  { path: 'logout', component: LogoutComponent},
  { path: 'myAccount', component: AccountComponent, canActivate: [AuthKeyCloakGuard], data: { roles: ['USER']}},
  { path: 'myBalance', component: BalanceComponent, canActivate: [AuthKeyCloakGuard], data: { roles: ['USER']}},
  { path: 'myLoans', component: LoansComponent, canActivate: [AuthKeyCloakGuard], data: { roles: ['USER']}},
  { path: 'myCards', component: CardsComponent, canActivate: [AuthKeyCloakGuard], data: { roles: ['ADMIN']}},
  { path: 'register', component: RegisterComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
