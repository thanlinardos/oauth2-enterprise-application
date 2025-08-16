import {Component, OnInit} from '@angular/core';
import {User} from 'src/app/model/user.model';
import {environment} from "../../../environments/environment";
import {KeycloakProfile} from "keycloak-js";
import {KeycloakService} from "keycloak-angular";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

    user = new User();

    public isLoggedIn: boolean = false;
    public userProfile: KeycloakProfile | null = null;

    constructor(private readonly keycloak: KeycloakService) {
    }

    public ngOnInit() {
        void (async () => { // avoid return type SQ warning 'S6544'
            this.isLoggedIn = this.keycloak.isLoggedIn();

            if (this.isLoggedIn) {
                this.userProfile = await this.keycloak.loadUserProfile();
                this.user.name = this.userProfile.username ?? '';
                this.user.details.email = this.userProfile.email ?? '';
                this.user.authDetails.authStatus = 'AUTH';
                window.sessionStorage.setItem('userdetails', JSON.stringify(this.user));
            }
        })();
    }

    public login() {
        this.keycloak.login({redirectUri: environment.uiUrl + '/dashboard'});
    }

    public logout() {
        this.keycloak.logout(environment.uiUrl + '/home');
    }

}
