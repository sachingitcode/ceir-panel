/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
import { DOCUMENT } from '@angular/common';
import { ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClrForm } from '@clr/angular';
import * as _ from 'lodash';
import { NgxPermissionsService } from 'ngx-permissions';
import { AlertComponent } from '../../core/components/alert.component';
import { PageType } from '../../core/constants/page.type';
import { GroupModel } from '../../core/models/group.model';
import { TicketModel } from '../../core/models/ticket.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { AuthService } from '../../core/services/common/auth.service';
import { MenuTransportService } from '../../core/services/common/menu.transport.service';
import { TicketService } from '../service/ticket.service';
import { ConfigService } from 'ng-config-service';

@Component({
  selector: 'ceirpanel-register-ticket',
  templateUrl: '../html/register-ticket.component.html',
  styles: [
    `
      :host ::ng-deep .clr-combobox-wrapper {
        min-width: 95% !important;
      }
      :host ::ng-deep .input-group-sm>.btn, .input-group-sm>.form-control, .input-group-sm>.form-select, .input-group-sm>.input-group-text {
        font-size: 16px;
      }
    `,
  ],
  providers: [TicketService],
})
export class RegisterTicketComponent implements OnInit {
  public page: string = '' as string;
  public path: string = '' as string;
  public id: number = 0 as number;
  readonly = false;
  @ViewChild(ClrForm, { static: true }) private clrForm!: ClrForm;
  templateForm: object = { parentGroup: '' } as object;
  ticket: TicketModel = {category: ''} as TicketModel;
  groups: Array<GroupModel> = [];
  public open = false;
  public cancel = false;
  public delete = false;
  documentFile!: File;
  imageObject: Array<any> = [] as Array<any>;
  categories: Array<any> = [] as Array<any>;
  public documentObject: Array<any> = [] as Array<any>;
isLocalhost = true;
  siteKey!:string;
  allow = true;
  lang = 'en';
  header = 'yes';
  countryCode = '+855';
  mobileRegex!:string;
  @ViewChild(AlertComponent, { static: true }) private alert!: AlertComponent;

  constructor(
    private cdref: ChangeDetectorRef,
    private route: ActivatedRoute,
    private apicall: ApiUtilService,
    private router: Router,
    private transport: MenuTransportService,
    public permissionService: NgxPermissionsService,
    private ticketService: TicketService,
    public authService: AuthService,
    public config: ConfigService,
    @Inject(DOCUMENT) private document: any
  ) {
    this.apicall.get('/config/frontend').subscribe({  
      next: (data:any) => {
        this.siteKey = data.siteKey;
        this.mobileRegex = data.mobileRegex;
      }
    });
    if(!authService.isLogin()) {
      this.apicall.get('/acl/isAllowInYourRegion').subscribe({
        next: (data:any) => {
          if (_.isEqual(_.get(data, 'allow'), false)) {
            console.log('access not allowed');
            this.router.navigate(['/region-denied']);
          }
        }
      });
    }
    this.isLocalhost = this.document.location.host.includes('localhost');
    if(!authService.isLogin()) {
      permissionService.addPermission('TICKET_ATTACHMENT');
    }
    this.route.queryParams.subscribe(queryParams => {
      this.lang = queryParams['lang'] || 'en';
      this.header = queryParams['header'] || 'yes';
    });
  }

  ngOnInit(): void {
    this.countryCode = this.config.get('countryCode') || '+855';
    this.path = this.route.snapshot.url[0].path;
    this.page = this.route.snapshot.paramMap.get('page') || '';
    this.id = Number(this.route.snapshot.paramMap.get('id') || 0);
    if (this.page === PageType.view) {
      this.readonly = true;
    }
    console.log('page: ', this.path);
    this.findCategories();
    if (this.id > 0) {
      this.apicall.get(`/ticket/${this.id}`).subscribe({
        next: (data) => {
          this.ticket = data as TicketModel;
        },
        error: (e) => console.log(e),
        complete: () => console.info('complete'),
      });
    }
  }
  onSubmit(userForm: NgForm) {
    console.log('submit form: ', userForm.invalid);
    if (userForm.invalid) {
      this.clrForm.markAsTouched();
      return;
    }
    this.save();
  }
  save() {
    this.transport.progress = true;
    const formData: FormData = new FormData();
    this.documentObject.forEach((d) => formData.append('documents', d.file));
    this.ticketService.putData(formData, this.ticket);
    PageType.edit ? this.ticketService.create(formData, this.page, this.lang, this.header) : this.ticketService.update(formData);
  }
  openClose(open: boolean) {
    this.open = open;
  }
  cancelOpenClose(cancel: boolean) {
    this.cancel = false;
    if (cancel == true) this.router.navigate([this.path]);
  }
  findCategories() {
    this.apicall
      .get('/ticket/category')
      .subscribe({ next: (result) => (this.categories = result as any[]) });
  }
  documentSelect(event: any) {
    const messages:Array<string> = [];
    for (let i = 0; i < event.target.files.length; i++) {
      console.log('size: ', event.target.files[i].size);
      if(event.target.files[i].size < 2000000){
        const localImageUrl = window.URL
        ? window.URL.createObjectURL(event.target.files[i])
        : (window as any).webkitURL.createObjectURL(event.target.files[i]);
        if(this.documentObject.length < 3) {
          this.documentObject.push({
            file: event.target.files[i],
            image: localImageUrl,
            thumbImage: localImageUrl,
            name: event.target.files[i].name,
            size: this.formatBytes(event.target.files[i].size)
          });
        }
      } else {
        messages.push(`File:${event.target.files[i].name} size is exceeded more than 2MB`);
      }
    }
    if(messages.length > 0 ) {
      this.alert.messages = messages;
      this.alert.open = true;
    }
  }
  formatBytes(bytes: number){
    const kb = 1024;
    const ndx = Math.floor( Math.log(bytes) / Math.log(kb) );
    const fileSizeTypes = ["bytes", "kb", "mb", "gb", "tb", "pb", "eb", "zb", "yb"];
  
    return {
      size: +(bytes / kb / kb).toFixed(2),
      type: fileSizeTypes[ndx]
    };
  }
  resolved(captchaResponse: string) {
    console.log(`Resolved captcha with response: ${captchaResponse}`);
  }
}
