import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {User} from '../model/user.model';
import {KeycloakAuthGuard, KeycloakService} from "keycloak-angular";
import {KeycloakProfile} from "keycloak-js";

@Injectable({
  providedIn: 'root'
})
export class AuthKeyCloakGuard extends KeycloakAuthGuard {
  user = new User();
  public userProfile: KeycloakProfile | null = null;

  constructor(protected override readonly router: Router,
              protected readonly keycloak: KeycloakService) {
    super(router, keycloak);
  }

  private async loadAndStoreUserProfile(): Promise<void> {
    this.userProfile = await this.keycloak.loadUserProfile();
    this.user.uuid = this.userProfile.id ?? '';
    this.user.authDetails.authStatus = 'AUTH';
    this.user.authDetails.roles = this.roles;
    this.user.name = this.userProfile.username ?? '';
    this.user.details.email = this.userProfile.email ?? '';
    this.user.details.firstName = this.userProfile.firstName ?? '';
    this.user.details.lastName = this.userProfile.lastName ?? '';
    this.user.details.mobileNumber = (this.userProfile.attributes?.['mobileNumber'] as string[])?.at(0) ?? '';
    window.sessionStorage.setItem('userdetails', JSON.stringify(this.user));
  }

  public async isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (!this.authenticated) {
      await this.keycloak.login({
        redirectUri: window.location.origin + state.url,
      });
    } else {
      await this.loadAndStoreUserProfile();
    }

    const requiredRoles = route.data['roles'];
    if (!requiredRoles || !(requiredRoles instanceof Array) || requiredRoles.length === 0) {
      return true;
    }
    return requiredRoles.some((role) => this.roles.includes(role));
  }

}
