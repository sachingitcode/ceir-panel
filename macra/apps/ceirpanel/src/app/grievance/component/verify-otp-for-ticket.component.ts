/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ExtendableListComponent } from '../../ceir-common/extendable-list';
import { TicketModel } from '../../core/models/ticket.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { OtpModel } from '../../core/models/otp.model';
import * as _ from "lodash";

@Component({
  selector: 'ceirpanel-verify-ticket-otp',
  template: `
    <clr-modal [(clrModalOpen)]="open" [clrModalStaticBackdrop]="false">
      <div class="modal-body">
        <form
          clrForm
          clrLayout="vertical"
          class="m-0 p-0"
          #f="ngForm"
          (ngSubmit)="f.form.valid && onSubmit(f)"
        >
          <figure class="text-center">
            <blockquote class="blockquote">
              <h3>
                <small class="fs-5 fw-bold" style="color: #1E4076;"
                  >View Ticket Status</small
                >
              </h3>
            </blockquote>
            <figcaption class="blockquote-footer m-0 p-0 mt-1">
              SMS verify code sent to phone number
              <cite title="Source Title">+855 {{mobileNumber}}</cite>
              <p class="m-0 p-0">Please enter the code to verify</p>
            </figcaption>
          </figure>
          <div class="clr-row">
            <div class="clr-col-12 text-center m-0 p-0">
              <ng-otp-input
                (onInputChange)="onOtpChange($event)"
                [config]="{ length: 4 }"
              ></ng-otp-input>
            </div>
          </div>
          <div class="clr-row mt-3">
            <div class="clr-col-12 text-center m-0 p-0">
              <button type="submit" class="btn btn-primary btn-block m-0 p-0">
                Verify
              </button>
            </div>
          </div>
          <div class="clr-row clr-justify-content-center m-0 p-0">
            <div class="clr-col-4 m-0 p-0">
              <button
                type="button"
                class="btn btn-link m-0 p-0"
                [disabled]="!finished"
                (click)="sendOtp()"
              >
                Resend
              </button>
            </div>
            <div class="clr-col-4 text-end m-0 p-0">
              <button type="button" class="btn btn-link m-0 p-0" (click)="open = false">
                Back
              </button>
            </div>
          </div>
          <div class="clr-row clr-justify-content-center">
            <div class="clr-col-4 m-0 p-0">
              <p class="m-0 p-0 text-primary">Time remaining: 00:{{countdown}}</p>
            </div>
            <div class="clr-col-4 text-end m-0 p-0"></div>
          </div>
        </form>
      </div>
    </clr-modal>
  `,
  styles: [``],
})
export class VerifyOtpForTicketComponent extends ExtendableListComponent {
  public tickets!: TicketModel[];
  public total!: number;
  public loading = true;
  public selected: any[] = [];
  public override open = false;
  @Input() public mobileNumber!: string;
  @Input() public ticketid!: number;
  otpModel: OtpModel = {otp: ""} as OtpModel;
  @Output() public verifyEvent: EventEmitter<any> = new EventEmitter();
  countdown:any = '00';
  timerobj:any;
  finished = false;

  constructor(private apicall: ApiUtilService){
    super();
  }

  public clearTimer(){
    this.countdown = '00';
    this.finished = false;
    clearInterval(this.timerobj);
  }
  public openTimer(){
    this.timer(1);
    setTimeout(() => this.open = true, 100);
  }
  sendOtp() {
    this.finished = false;
    this.apicall.get(`/otp/send/${this.mobileNumber}`).subscribe({
      next: (result) => {
        console.log('result: ', result);
        this.timer(1);
      }
    });
  }
  onSubmit(userForm: NgForm) {
    console.log(userForm.value);
    console.log(this.otpModel);
    this.otpModel.mobileNumber = this.mobileNumber;
    this.verifyOtp();
  }
  onOtpChange(event: unknown) {
    console.log(event);
    this.otpModel.otp = event as string;
  }
  verifyOtp() {
    this.apicall.post(`/otp/verify`, this.otpModel).subscribe({
      next: (result) => {
        console.log('result: ', result);
        if (_.has(result, 'verifyOtpSuccess')) {
          this.verifyEvent.emit(true);
        } else {
          this.verifyEvent.emit(false);
        }
      }
    });
  }
  timer(minute: number) {
    let seconds: number = minute * 60;
    let textSec: any = "0";
    let statSec = 60;
    const prefix = minute < 10 ? "0" : "";
    this.timerobj = setInterval(() => {
      seconds--;
      if (statSec != 0) statSec--;
      else statSec = 59;
      if (statSec < 10) {
        textSec = "0" + statSec;
      } else textSec = statSec;
      this.countdown = `${prefix}${Math.floor(seconds / 60)}:${textSec}`;
      if (seconds == 0) {
        clearInterval(this.timerobj);
        this.finished = true;
      }
    }, 1000);
  }
}
