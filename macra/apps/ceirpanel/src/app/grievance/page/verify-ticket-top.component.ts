/* eslint-disable @typescript-eslint/no-explicit-any */
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClrForm } from '@clr/angular';
import { TranslateService } from '@ngx-translate/core';
import * as _ from 'lodash';
import { TicketModel } from '../../core/models/ticket.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { MenuTransportService } from '../../core/services/common/menu.transport.service';

@Component({
  selector: 'ceirpanel-ticket-otp',
  templateUrl: '../html/verify-ticket-otp.component.html',
  styles: [``],
})
export class VerifyTicketOtpComponent implements OnInit {
  ticket: TicketModel = {} as TicketModel;
  @ViewChild(ClrForm, { static: true }) private clrForm!: ClrForm;
  otp!: string;
  subscribeTimer = 20;
  timeLeft = 20;
  interval!: any;
  timeLeftDisplay = "00";
  alert!:any;

  constructor(
    private cdref: ChangeDetectorRef, 
    private route: ActivatedRoute, 
    private translate: TranslateService,
    private apicall: ApiUtilService,
    private router: Router,private transport: MenuTransportService) {
      this.ticket.mobileNumber = this.route.snapshot.paramMap.get('msisdn') || '';
  }

  ngOnInit(): void {
    this.startTimer();
  }
  onSubmit(userForm: NgForm) {
    if (userForm.invalid) {
      this.clrForm.markAsTouched();
      return;
    }
    this.apicall.get(`/ticket/verify-otp/${this.ticket.mobileNumber}/${this.otp}`).subscribe({
      next: (_data) => {
        console.log('message:', _data as any);
        if(_.isEqual((_data as any).message, 'verifyOtpSuccess')) {
          if(_.eq((_data as any).size, 1)) {
            this.router.navigate(['/view-ticket-thread',(_data as any).id]);
          } else {
            this.router.navigate(['/ticket/end-user/list',this.ticket.mobileNumber]);
          }
        } else {
          this.alert = {type: 'danger', message: _.get(_data,'message')};
          setTimeout(() => this.alert = null, 10000);
        }
      }
    });
  }
  onOtpChange(event: unknown){
    this.otp = event as any;
  }
  startTimer() {
    this.interval = setInterval(() => {
      if(this.timeLeft > 0) {
        this.timeLeft--;
        if(String(this.timeLeft).length == 1)this.timeLeftDisplay = `0${this.timeLeft}`;
        else this.timeLeftDisplay = `${this.timeLeft}`;
      }
    },1000)
  }

  pauseTimer() {
    clearInterval(this.interval);
  }
  sendotp(){
    this.apicall.get(`/ticket/send-otp/${this.ticket.mobileNumber}`).subscribe({
      next: (_data) => {
        console.log('message:', _data as any);
        if(_.isEqual((_data as any).message, 'sendOtpSuccess')) {
          this.transport.alert = {
            message: 'sendOtpSuccess',
            type: 'info',
          } as unknown;
          this.timeLeft = 20;
          this.startTimer();
        }
      }
    });
  }
}
