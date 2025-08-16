import {Component, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {LoginService} from 'src/app/services/login/login.service';
import {RegisterDetails} from "../../model/registerDetails.model";


@Component({
    selector: 'app-login',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
    model = new RegisterDetails();
    showPassword = false;
    confirmedPassword = '';
    receivedEmail: string | undefined;

    constructor(private readonly loginService: LoginService) {

    }

    ngOnInit() {
        // do nothing
    }

    registerUser(registerForm: NgForm) {
        if (this.model.password !== this.confirmedPassword) {
            return;
        }
        this.loginService.registerUser(this.model).subscribe(
            responseData => {
                this.receivedEmail = <any>responseData.headers.get('Location')?.split('/').pop();
                registerForm.resetForm();
            });
    }
}
