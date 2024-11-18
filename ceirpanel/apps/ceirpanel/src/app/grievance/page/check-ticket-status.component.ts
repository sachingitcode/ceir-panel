import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import * as _ from "lodash";
import { SearchImeiModel } from '../../core/models/search.imdi.model';
import { TicketModel } from '../../core/models/ticket.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { VerifyOtpForTicketComponent } from '../component/verify-otp-for-ticket.component';
import { Router } from '@angular/router';


@Component({
  selector: 'ceirpanel-check-ticket-status',
  templateUrl: '../html/check-ticket-status.component.html',
  styles: [``],
})
export class CheckTicketStatusComponent implements OnInit {
  imei: SearchImeiModel = {} as SearchImeiModel;
  public cancel = false;
  ticket: TicketModel = {} as TicketModel;
  minHeight: number | undefined;
  @ViewChild(VerifyOtpForTicketComponent, { static: false }) verifyOtp!: VerifyOtpForTicketComponent;
  public verified = false;
  constructor(
   private apicall: ApiUtilService,
   private router: Router
  ) {}
  ngOnInit(): void {
    console.log();
  }
  onSubmit(userForm: NgForm) {
    this.verified = false;
    if(_.isEmpty(this.ticket.mobileNumber) && !_.isEmpty(this.ticket.ticketId)) {
      this.apicall.get(`/ticket/${this.ticket.ticketId}`).subscribe({
        next: (result) => {
          if(!_.isEmpty(result)) {
            this.router.navigate(['/check-ticket-status/ticket-thread',this.ticket.ticketId]);
          }
        }
      });
    } else if(!_.isEmpty(this.ticket.mobileNumber)){
      this.verifyOtp.mobileNumber = this.ticket.mobileNumber;
      this.verifyOtp.clearTimer();
      setTimeout(() => this.verifyOtp.openTimer(), 10);
    }
    console.log(userForm);
  }
  onOtpChange(event: unknown) {
    console.log(event);
  }
  getTicket() {
    this.apicall.get(`/ticket/${this.ticket.ticketId}`).subscribe({
      next: (result) => {
        if(!_.isEmpty(result)) {
          this.ticket = (result as TicketModel);
          this.verifyOtp.open = true;
          this.verifyOtp.mobileNumber = this.ticket.mobileNumber;
          this.sendOtp();
        }
      }
    });
  }
  sendOtp() {
    this.apicall.get(`/otp/send/${this.ticket.mobileNumber}`).subscribe({
      next: (result) => {
        console.log('result: ', result);
      }
    });
  }
}
