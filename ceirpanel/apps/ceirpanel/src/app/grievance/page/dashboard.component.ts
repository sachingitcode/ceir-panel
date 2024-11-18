/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, OnInit } from '@angular/core';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { JwtService } from '../../core/services/common/jwt.service';

@Component({
  selector: 'ceirpanel-ticket-dashboard',
  templateUrl: '../html/dashboard.component.html',
  styles: [],
})
export class DashboardComponent implements OnInit {
  dashboard:any;

  constructor(
    private apicall: ApiUtilService,
    public jwtService: JwtService
  ) {}
  ngOnInit(): void {
    this.apicall.get(`/ticket/dashboard`).subscribe({
        next: (_data) => {
            this.dashboard = _data as any;
        }
    });
  }
  onOtpChange(event: unknown) {
    console.log(event);
  }
}
