import { BrowserModule } from '@angular/platform-browser';
import {APP_INITIALIZER, isDevMode, NgModule, OnInit} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi, withXsrfConfiguration } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { ContactComponent } from './components/contact/contact.component';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { LogoutComponent } from './components/logout/logout.component';
import { NoticesComponent } from './components/notices/notices.component';
import { AccountComponent } from './components/account/account.component';
import { BalanceComponent } from './components/balance/balance.component';
import { LoansComponent } from './components/loans/loans.component';
import { CardsComponent } from './components/cards/cards.component';
import { HomeComponent } from './components/home/home.component';
import { KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import {environment} from "../environments/environment";
import { RegisterComponent } from "./components/register/register.component";

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: environment.keycloak.config,
      initOptions: {
        pkceMethod: 'S256',
        redirectUri: environment.uiUrl + '/dashboard',
      },
      loadUserProfileAtStartUp: false
    });
}

@NgModule({ declarations: [
        AppComponent,
        HeaderComponent,
        ContactComponent,
        LoginComponent,
        DashboardComponent,
        LogoutComponent,
        NoticesComponent,
        AccountComponent,
        BalanceComponent,
        LoansComponent,
        CardsComponent,
        HomeComponent,
        RegisterComponent
    ],
    bootstrap: [AppComponent], imports: [BrowserModule,
        AppRoutingModule,
        FormsModule,
        KeycloakAngularModule], providers: [
        {
            provide: APP_INITIALIZER,
            useFactory: initializeKeycloak,
            multi: true,
            deps: [KeycloakService],
        },
        provideHttpClient(withInterceptorsFromDi(), withXsrfConfiguration({
            cookieName: 'XSRF-TOKEN',
            headerName: 'X-XSRF-TOKEN',
        }))
    ] })
export class AppModule implements OnInit {
  ngOnInit(): void {
    if (isDevMode()) {
      console.log('Development!');
    } else {
      console.log('Production!');
    }
  }

}
