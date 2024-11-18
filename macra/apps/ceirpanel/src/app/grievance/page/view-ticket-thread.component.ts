/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Options } from 'ngx-qrcode-styling';
import { SearchImeiModel } from '../../core/models/search.imdi.model';
import { TicketModel, TicketNoteDto } from '../../core/models/ticket.model';
import { TicketService } from '../service/ticket.service';
import * as _ from 'lodash';
import { NgForm } from '@angular/forms';
import { ClrForm } from '@clr/angular';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { MenuTransportService } from '../../core/services/common/menu.transport.service';
import { AuthService } from '../../core/services/common/auth.service';
import { TicketResolveComponent } from '../component/ticket-resolve.component';
import { NgxPermissionsService } from 'ngx-permissions';
import {Location} from '@angular/common';
import { ConfigService } from 'ng-config-service';

@Component({
  selector: 'ceirpanel-view-ticket-thread',
  templateUrl: '../html/view-ticket-thread.component.html',
  styles: [`
  .subjectline {
    font-size:11px;
    color:#828282;
    margin-right: .6rem !important;
  }
  .green {
    color: #04C100;
  }
  .blue {
    color: #2F80ED;
  }
  .text-secondary {
    color: #828282 !important;
  }
  .h5, h5 {
    font-size: .8rem !important;
  }
  `],
})
export class ViewTicketThreadComponent implements OnInit {
  imei: SearchImeiModel = {} as SearchImeiModel;
  public cancel = false;
  public config: Options = {};
  public ticketId!: string;
  public ticket: TicketModel = {
    subject: 'Not accessible',
    createdOn: '2023-10-24 19:05:09',
    updatedOn: '2023-10-24 19:05:09',
    raisedBy: 'Vivek Kumar',
    category: 'Mobile',
    currentStatus: 'In progress',
    description:
      'End user’s first Ticket description will be written here. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet ne. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes',
  } as any;
  public notes: TicketNoteDto = { visibility: 'private' } as any;

  public documentObject: Array<any> = [] as Array<any>;
  @ViewChild(ClrForm, { static: true }) private clrForm!: ClrForm;
  public open = false;
  public feedback = false;
  @ViewChild(TicketResolveComponent) private ticketResoved!: TicketResolveComponent;
  @ViewChild("f") public ngForm!: NgForm;
  endUsers: Array<any> = [];
  allowStatusForComments: Array<any> = [];

  constructor(
    private route: ActivatedRoute,
    private ticketService: TicketService,
    private apicall: ApiUtilService,
    private transport: MenuTransportService,
    public authService: AuthService,
    public permissionService: NgxPermissionsService,
    public _location: Location,
    public cnf: ConfigService
  ) {
    this.ticketId = this.route.snapshot.paramMap.get('ticketId') || '';
  }
  ngOnInit(): void {
    this.endUsers = this.cnf.get('endUsers') || [];
    this.allowStatusForComments = this.cnf.get('allowStatusForComment') || [];
    this.notes = { visibility: this.permissionService.getPermission('TICKET_VISIBILITY') ? 'private': 'public' } as any;
    if (!_.isEmpty(this.ticketId)) {
      this.ticketService.get(this.ticketId).subscribe({
        next: (result) => {
          this.ticket = (result as any).data as TicketModel,
          console.log('journals: {}', this.ticket.issue.journals);
        },
      });
    }
  }
  documentSelect(event: any) {
    for (let i = 0; i < event.target.files.length; i++) {
      const localImageUrl = window.URL
        ? window.URL.createObjectURL(event.target.files[i])
        : (window as any).webkitURL.createObjectURL(event.target.files[i]);
      this.documentObject.push({
        file: event.target.files[i],
        image: localImageUrl,
        thumbImage: localImageUrl,
        name: event.target.files[i].name,
      });
    }
  }
  onSubmit(userForm: NgForm) {
    if (userForm.invalid) {
      this.clrForm.markAsTouched();
      return;
    }
    this.saveNote();
  }
  saveNote() {
    const formData: FormData = new FormData();
    this.documentObject.forEach((d) => formData.append('attachments', d.file));
    const privateNotes:any = _.isEmpty(this.notes.visibility) ? false: _.isEqual(this.notes.visibility, 'public') ? false: true;
    formData.append('notes', this.notes.notes);
    formData.append('ticketId', this.ticketId);
    formData.append('privateNotes', privateNotes);
    this.apicall.post(`/ticket/save/note`, formData).subscribe({
      next: (_data) => {
        console.log(_data);
        this.transport.alert = {
          message: 'registerTicketSuccess',
          type: 'info',
        } as unknown;
        this.ticket = _data as TicketModel;
        console.log('journals: {}', this.ticket.issue.journals);
        this.ticket.issue.journals = _.sortBy(this.ticket.issue.journals, ['createdOn'], 'asc');
      },
      complete: () => {
        setTimeout(() => (this.transport.progress = false), 3000);
        this.notes = { visibility: this.authService.isLogin() ? 'private': 'public' , notes: ''} as any;
      },
    });
  }
  resolve(event: any) {
    this.open = event.open;
    const formData: FormData = new FormData();
    formData.append('notes', event.notes);
    formData.append('ticketId', this.ticketId);
    formData.append('privateNotes', true as any);
    if (_.isEqual(event.resolve, 'yes')) {
      this.apicall.get(`/ticket/resolve/${this.ticketId}`).subscribe({
        next: (_res) => {
          this.ticket = _res as TicketModel;
          if(!_.isEmpty(event.notes)) {
            this.apicall.post(`/ticket/save/note`, formData).subscribe({
              next: (_data) => {
                console.log('res: ', _res);
                this.ticket = _data as TicketModel;
                this.notes = { visibility: this.authService.isLogin() ? 'private': 'public' , notes: ''} as any;
                setTimeout(() => this.openFeedback(true), 3000);
              }
            });
          }
        },
      });
    }
  }
  endUserFeedback(event: any) {
    this.feedback = event.open;
    console.log('event', event);
    if (_.isEqual(event.resolve, 'yes')) {
      this.apicall.post(`/ticket/save/rate`, {feedback: event.feedback, ratings: event.ratings, ticketId: this.ticketId}).subscribe({
        next: () => {
          console.log();
        },
      });
    }
  }
  openResolved(open: boolean) {
    this.ticketResoved.ngForm.reset();
    this.open = open;
  }
  openFeedback(open: boolean) {
    this.feedback = open;
  }
  get sortData() {
    return this.ticket.issue.journals.sort((a, b) => {
      return <any>new Date(b.createdOn) - <any>new Date(a.createdOn);
    });
  }
}
