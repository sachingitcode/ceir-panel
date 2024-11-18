/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClrDatagridStateInterface } from '@clr/angular';
import * as _ from "lodash";
import { ExtendableListComponent } from '../../ceir-common/extendable-list';
import { GroupList } from '../../core/models/group.model';
import { TicketList, TicketModel } from '../../core/models/ticket.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { AuthService } from '../../core/services/common/auth.service';
import { ExportService } from '../../core/services/common/export.service';
import { FilterModel } from '../../core/models/filter.model';
import { NgxPermissionsService } from 'ngx-permissions';


@Component({
  selector: 'ceirpanel-ticket-list',
  templateUrl: '../html/ticket-list.component.html',
  styles: [`.dropdown-toggle::after{display:none}`],
})
export class TicketListComponent extends ExtendableListComponent implements OnInit {
  public tickets!: TicketModel[];
  public total!: number;
  public loading = true;
  public selected: any[] = [];
  msisdn!:string;
  raisedBy!:string;
  status!:string;
  clientType!:string;
  filterModel: FilterModel = {} as FilterModel;

  constructor(
    private cdRef: ChangeDetectorRef,
    private apicall: ApiUtilService,
    public exportService: ExportService,
    private route: ActivatedRoute,
    public authService: AuthService,
    private permission: NgxPermissionsService) {
    super();
  }
  ngOnInit(): void {
    this.msisdn = this.route.snapshot.paramMap.get('msisdn') || '';
    this.raisedBy = this.route.snapshot.queryParamMap.get('raisedBy') || '';
    this.status = this.route.snapshot.queryParamMap.get('status') || '';
    this.clientType = this.route.snapshot.queryParamMap.get('clientType') || '';
    this.filterModel.ticketStatus = this.status;
    this.filterModel.raisedBy = this.raisedBy;
    this.filterModel.mobileNumber = this.msisdn;
    console.log('permission: ', this.permission.getPermissions());
  }
  
  refresh(state: ClrDatagridStateInterface) {
    this.loading = true;
    this.state = state;
    if(!_.isEmpty(this.msisdn))this.pushFilter({mobileNumber: this.msisdn});
    if(!_.isEmpty(this.raisedBy))this.pushFilter({raisedBy: this.raisedBy});
    if(!_.isEmpty(this.status))this.pushFilter({ticketStatus: this.status});
    if(!_.isEmpty(this.clientType))this.pushFilter({clientType: this.clientType});
    this.applyExisterFilter();
    this.apicall.post('/ticket/pagination', state).subscribe({
      next: (result) => {
        this.tickets = this.sortBy(state, (result as TicketList).content);
        this.total = (result as TicketList).totalElements;
        this.loading = false;
        this.cdRef.detectChanges();
      },
      error: (e) => this.loading = false,
      complete: () => {
        this.loading = false;
        this.cdRef.detectChanges();
      }
    });
  }
  deleteRecord(data: any) {
    this.apicall.delete(`/group/${data.id}`).subscribe({
      next: (result) => {
        console.log('result:', result);
      },
      error: (e) => this.loading = false,
      complete: () => {
        console.log('completed');
        this.refresh(this.state);
      }
    });
  }
  openClose(open: boolean) {
    this.open = open;
    if (open && this.selectedData.length > 0){
      this.selectedData.forEach(data => this.deleteRecord(data));
    }
  }
  export(state: ClrDatagridStateInterface) {
    const st = _.cloneDeep(state);
    if(st && st.page) st.page.size = 10;
    this.apicall.post('/ticket/pagination', st).subscribe({
      next: (result) => {
        const groups = (result as TicketList).content;
        this.exportService.tickets(groups, `tickets-${new Date().getMilliseconds()}`,
        {showLabels: true,headers: ["Ticket","Phone","Subject", "Created On","Modified On", "Raised By","Status"]});
      }
    });
  }
}