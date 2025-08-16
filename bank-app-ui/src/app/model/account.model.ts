export class Account {

  public username: string;
  public accountNumber: number;
  public accountType: string;
  public branchAddress: string;


  constructor(username?: string, accountNumber?: number, accountType?: string, branchAddress?: string) {
    this.username = username ?? '';
    this.accountNumber = accountNumber ?? 0;
    this.accountType = accountType ?? '';
    this.branchAddress = branchAddress ?? '';
  }

}
