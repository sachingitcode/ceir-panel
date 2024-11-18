/* eslint-disable @typescript-eslint/no-explicit-any */
import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { SearchImeiModel } from '../../core/models/search.imdi.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { Options } from 'ngx-qrcode-styling';
import { QrcodeService } from '../service/qrcode.service';
import { AuthService } from '../../core/services/common/auth.service';
import { TicketService } from '../service/ticket.service';
import { TicketModel } from '../../core/models/ticket.model';
import {Location} from '@angular/common';
import * as _ from 'lodash';
import { NgxPermissionsService } from 'ngx-permissions';
import { ConfigService } from 'ng-config-service';

@Component({
  selector: 'ceirpanel-register-ticket-success',
  templateUrl: '../html/register-ticket-success.component.html',
  styles: [``],
})
export class RegisterTicketSuccessComponent implements OnInit, OnDestroy {
  imei: SearchImeiModel = {} as SearchImeiModel;
  public cancel = false;
  public config: Options = {};
  public ticketId!: string;
  public ticket: TicketModel = {} as any;
  public eirsportallink = '';
  lang = 'en';
  header = 'yes';

  constructor(
    private cdref: ChangeDetectorRef,
    private route: ActivatedRoute,
    private translate: TranslateService,
    private apicall: ApiUtilService,
    private router: Router,
    public authService: AuthService,
    private qrcodeService: QrcodeService,
    private ticketService: TicketService,
    public _location: Location,
    public permissionService: NgxPermissionsService,
    public cnf: ConfigService
  ) {
    if(!this.authService.isLogin()) {
      this.permissionService.addPermission('TICKET_SSYSADMIN');
    }
    this.ticketId = this.route.snapshot.paramMap.get('ticketId') || '';
    if(!this.authService.isLogin()) {
      this.apicall.get('/config/frontend').subscribe({
        next: (data:any) => {
          this.eirsportallink = data.eirsPortalLink;
        }
      });
    }
    this.route.queryParams.subscribe(queryParams => {
      this.lang = queryParams['lang'] || 'en';
      this.header = queryParams['header'] || 'yes';
    });
  }
  ngOnDestroy(): void {
    this.permissionService.removePermission('TICKET_SSYSADMIN');
  }
  ngOnInit(): void {
    this.ticketService.get(this.ticketId).subscribe({
      next: (result) => {
        this.ticket = (result as any).data as TicketModel;
        this.ticket.countryCode = this.cnf.get('countryCode') || '';
      },
    });
    this.config = this.qrcodeService.get();
    
  }
  onSubmit(userForm: NgForm) {
    console.log(userForm);
  }
  onOtpChange(event: unknown) {
    console.log(event);
  }
  onDownload(qrcode: any): void {
    qrcode.download().subscribe((res: any) => {
      // TO DO something!
      console.log('download:', res);
    });
  }
}
