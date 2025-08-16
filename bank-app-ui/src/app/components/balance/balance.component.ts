import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/user.model';
import { DashboardService } from '../../services/dashboard/dashboard.service';
import {AccountTransactions} from "../../model/account.transactions.model";


@Component({
  selector: 'app-balance',
  templateUrl: './balance.component.html',
  styleUrls: ['./balance.component.css']
})
export class BalanceComponent implements OnInit {

  user = new User();
  transactions: AccountTransactions[] = [];

  constructor(private readonly dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.user = JSON.parse(sessionStorage.getItem('userdetails') ?? "");
    if(this.user){
      this.dashboardService.getAccountTransactions(this.user.details.email).subscribe(
        responseData => {
        this.transactions = <any> responseData.body;
        });
    }
  }

}
