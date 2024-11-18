/* eslint-disable @typescript-eslint/no-explicit-any */
import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClrForm } from '@clr/angular';
import { TranslateService } from '@ngx-translate/core';
import { ConfigService } from 'ng-config-service';
import { UserModel } from '../../core/models/user.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { MenuTransportService } from '../../core/services/common/menu.transport.service';
import * as _ from 'lodash';

@Component({
  selector: 'ceirpanel-email-verification',
  templateUrl: '../html/user-mobile-email-verification.html',
  styles: [``],
})
export class UserMobileEmailVerificationComponent implements OnInit {
  user: UserModel = {} as UserModel;
  @ViewChild(ClrForm, { static: true }) private clrForm!: ClrForm;
  emailotp!: string;
  mobileotp!: string;
  subscribeTimer = 50;
  timeLeft = 50;
  interval!: any;
  timeLeftDisplay = "00";
  alert!:any;
  countryCode = '+855';
  userId!:string;
  email!:string;
  msisdn!:string;
  url!:string;

  constructor(
    private cdref: ChangeDetectorRef, 
    private route: ActivatedRoute, 
    private translate: TranslateService,
    private apicall: ApiUtilService,
    private router: Router,
    private transport: MenuTransportService,
    public config: ConfigService) {
    this.userId = this.route.snapshot.paramMap.get('id') || '';
    this.email = this.route.snapshot.paramMap.get('email') || '';
    this.msisdn = this.route.snapshot.paramMap.get('msisdn') || '';
    this.route.queryParams.subscribe((queryParams) => {
        this.url = queryParams['url'] || '/user';
        console.log('complete url: ', this.url);
    });
  }

  ngOnInit(): void {
    this.countryCode = this.config.get('countryCode') || '+855';
    this.apicall.get(`/user/${this.userId}`).subscribe({
        next: (_data) => {
            this.user = (_data) as UserModel;
            this.sendotp();
        }
    });
    this.startTimer();
  }
  onSubmit(userForm: NgForm) {
    if (userForm.invalid) {
      this.clrForm.markAsTouched();
      return;
    }
    if(this.msisdn!== this.user.profile.phoneNo && this.email !== this.user.profile.email) {
        this.verifyEmailAndMsisdnOtp();
    } else if(this.msisdn=== this.user.profile.phoneNo && this.email !== this.user.profile.email) {
        this.verifyEmailOtp();
    }else if(this.msisdn!== this.user.profile.phoneNo && this.email === this.user.profile.email) {
        this.verifyMsisdnOtp();
    }
    
  }
  verifyEmailOtp(){
    this.apicall.get(`/user/verify-otp/${this.user.profile.email}/${this.emailotp}`).subscribe({
        next: (_email) => {
            if(_.isEqual((_email as any).message, 'verifyOtpSuccess')) {
                this.updateEmailAndMsisdn();
            } else {
                this.alert = {type: 'danger', message: _.get(_email,'message')};
                setTimeout(() => this.alert = null, 10000);
            }
        }
    });
  }
  verifyMsisdnOtp() {
    this.apicall.get(`/user/verify-otp/${this.user.profile.phoneNo}/${this.mobileotp}`).subscribe({
        next: (_sms) => {
            if(_.isEqual((_sms as any).message, 'verifyOtpSuccess')) {
                this.updateEmailAndMsisdn();
            } else {
                this.alert = {type: 'danger', message: _.get(_sms,'message')};
                setTimeout(() => this.alert = null, 10000);
            }
        }
    });
  }
  verifyEmailAndMsisdnOtp() {
    this.apicall.get(`/user/verify-otp/${this.user.profile.email}/${this.emailotp}`).subscribe({
        next: (_email) => {
            if(_.isEqual((_email as any).message, 'verifyOtpSuccess')) {
                this.apicall.get(`/user/verify-otp/${this.user.profile.phoneNo}/${this.mobileotp}`).subscribe({
                    next: (_sms) => {
                        if(_.isEqual((_sms as any).message, 'verifyOtpSuccess')) {
                            this.updateEmailAndMsisdn();
                        } else {
                            this.alert = {type: 'danger', message: _.get(_email,'message')};
                            setTimeout(() => this.alert = null, 10000);
                        }
                    }
                });
            } else {
                this.alert = {type: 'danger', message: _.get(_email,'message')};
                setTimeout(() => this.alert = null, 10000);
            }
        }
    });
  }
  updateEmailAndMsisdn() {
    this.apicall.get(`/user/updateEmailAndMsisdn/${this.user.id}/${this.email}/${this.msisdn}`).subscribe({
        next: (_data) => {
            if (_.isEqual(_.get(_data, 'body.status'), 'failed')) {
                this.transport.alert = {type: 'danger', message: _.get(_data,'body.message')};
            } else {
                this.transport.alert = {type: 'success', message: _.get(_data,'body.message')};
                setTimeout(() => this.transport.progress = false, 3000);
                setTimeout(() => this.alert = null, 10000);
                console.log('url: ', this.url);
                setTimeout(() => this.router.navigate([this.url]), 2000);
            }
        }
    });
  }
  onEmailOtpChange(event: unknown){
    this.emailotp = event as any;
  }
  onMobileOtpChange(event: unknown){
    this.mobileotp = event as any;
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
    if(this.email !== this.user.profile.email) {
        this.apicall.get(`/user/send-otp/EMAIL/${this.user.profile.email}`).subscribe({
            next: (_email) => {
                console.log('Email otp send:{}', _email);
                
            }
        });
    }
    if(this.msisdn !== this.user.profile.phoneNo) {
        this.apicall.get(`/user/send-otp/SMS/${this.user.profile.phoneNo}`).subscribe({
            next: (_sms) => {
                console.log('Sms otp send:{}', _sms);
            }
        });
    }
  }
  public navigaterUrl() {
    const menu = JSON.parse(localStorage.getItem('menu') || '/user');
    console.log('cache menu: ', menu, _.sample(menu as any[]).link);
    return _.sample(menu as any[]).link;
  }
}
