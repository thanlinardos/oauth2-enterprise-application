import { Component, OnInit } from '@angular/core';
import { DashboardService } from 'src/app/services/dashboard/dashboard.service';

@Component({
  selector: 'app-notices',
  templateUrl: './notices.component.html',
  styleUrls: ['./notices.component.css']
})
export class NoticesComponent implements OnInit {

  notices: any = [];

  constructor(private readonly dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.dashboardService.getNoticeDetails().subscribe(
      responseData => {
      this.notices = <any> responseData.body;
      });
  }

}
