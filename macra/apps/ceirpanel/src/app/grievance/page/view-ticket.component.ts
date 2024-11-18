/* eslint-disable @typescript-eslint/no-explicit-any */
import { ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { TicketModel } from '../../core/models/ticket.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { NgForm } from '@angular/forms';
import { ClrForm } from '@clr/angular';
import * as _ from 'lodash';
import { MenuTransportService } from '../../core/services/common/menu.transport.service';
import { DOCUMENT } from '@angular/common';
import { ResizedEvent } from 'angular-resize-event';

@Component({
  selector: 'ceirpanel-view-ticket',
  templateUrl: '../html/view-ticket.component.html',
  styles: [
    `
      .g-recaptcha {
        -moz-transform: scale(1.1);
        -ms-transform: scale(1.1);
        -o-transform: scale(1.1);
        -moz-transform-origin: 0;
        -ms-transform-origin: 0;
        -o-transform-origin: 0;
        -webkit-transform: scale(1.1);
        transform: scale(1.1);
        -webkit-transform-origin: 0 0;
        transform-origin: 0;
        filter: progid:DXImageTransform.Microsoft.Matrix(M11=1.1,M12=0,M21=0,M22=1.1,SizingMethod='auto expand');
      }
    `,
  ],
})
export class ViewTicketComponent implements OnInit {
  ticket: TicketModel = {captcha: ''} as TicketModel;
  @ViewChild(ClrForm, { static: true }) private clrForm!: ClrForm;
  siteKey = '';
  isLocalhost = true;
  scale!:number;
  lang = 'en';
  header = 'yes';

  constructor(
    private cdref: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private apicall: ApiUtilService,
    private router: Router,
    private transport: MenuTransportService,
    @Inject(DOCUMENT) private document: any
  ) {
    this.apicall.get('/config/frontend').subscribe({
      next: (data:any) => {
        this.siteKey = data.siteKey;
      }
    });
    this.isLocalhost = this.document.location.host.includes('localhost');
    this.route.queryParams.subscribe(queryParams => {
      this.lang = queryParams['lang'] || 'en';
      this.header = queryParams['header'] || 'yes';
    });
  }
  onResized(event: ResizedEvent) {
    if (event.newRect.width < 302) {
      this.scale = event.newRect.width / 302;
    } else{
      this.scale = 1.2; 
    }
    this.scale = 1.1; 
  }
  ngOnInit(): void {
    console.log();
  }
  onSubmit(userForm: NgForm) {
    console.log(userForm);
    if (userForm.invalid) {
      this.clrForm.markAsTouched();
      return;
    }
    this.apicall.get(`/ticket/${this.ticket.ticketId}`).subscribe({
      next: (_data) => {
        console.log('message:', _data as any);
        if (_.isEqual(_.get(_data, 'message'), 'notValidTicketId')) {
          this.transport.alert = {
            message: 'notValidTicketId',
            type: 'info',
          } as unknown;
        } else {
          this.router.navigate(['ticket/ticket-thread',(_data as any).data.ticketId]);
        }
      }
    });
  }
  onOtpChange(event: unknown) {
    console.log(event);
  }
}
