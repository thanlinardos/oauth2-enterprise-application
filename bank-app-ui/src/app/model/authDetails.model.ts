export class AuthDetails {

    public roles: string[];
    public statusCd: string;
    public statusMsg: string;
    public authStatus: string;

    constructor(roles?: string[], statusCd?: string, statusMsg?: string, authStatus?: string) {
        this.roles = roles ?? [];
        this.statusCd = statusCd ?? '';
        this.statusMsg = statusMsg ?? '';
        this.authStatus = authStatus ?? '';
    }
}
