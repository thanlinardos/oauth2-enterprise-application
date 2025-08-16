export class RegisterDetails {

    public mobileNumber: string;
    public email: string;
    public password: string;
    public firstName: string;
    public lastName: string;

    constructor(mobileNumber?: string, email?: string, password?: string, firstName?: string, lastName?: string) {
        this.mobileNumber = mobileNumber ?? '';
        this.email = email ?? '';
        this.password = password ?? '';
        this.firstName = firstName ?? '';
        this.lastName = lastName ?? '';
    }
}
