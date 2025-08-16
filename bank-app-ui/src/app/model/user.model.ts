import {RegisterDetails} from "./registerDetails.model";
import {AuthDetails} from "./authDetails.model";

export class User {

    public id: number;
    public uuid: string;
    public name: string;
    public details: RegisterDetails;
    public authDetails: AuthDetails;



    constructor(id?: number, uuid?: string, name?: string, details?: RegisterDetails, authDetails?: AuthDetails) {
        this.id = id ?? 0;
        this.uuid = uuid ?? '';
        this.name = name ?? '';
        this.details = details ?? new RegisterDetails();
        this.authDetails = authDetails ?? new AuthDetails();
    }

}
