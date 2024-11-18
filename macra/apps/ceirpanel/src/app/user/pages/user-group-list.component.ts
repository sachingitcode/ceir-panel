/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-non-null-assertion */
import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClrDatagridStateInterface } from '@clr/angular';
import * as _ from "lodash";
import { take } from 'rxjs';
import { ExtendableListComponent } from '../../ceir-common/extendable-list';
import { ExportService } from '../../core/services/common/export.service';
import { UserGroupDto } from '../../user-group/dto/user-group.dto';
import { UserGroupDeleteComponent } from '../component/user-group-delete.component';
import { UserGroupService } from '../service/user.group.service';
import { UserService } from '../service/user.service';


@Component({
  selector: 'ceirpanel-user-group-list',
  templateUrl: '../html/user-group-list.component.html',
  styles: [`.dropdown-toggle::after{display:none}`],
})
export class UserGroupListComponent extends ExtendableListComponent {
  list: UserGroupDto = {totalElements: 0} as UserGroupDto;
  @ViewChild(UserGroupDeleteComponent) userGroupDelete!: UserGroupDeleteComponent;
  groupId = 0;
  public loading = true;
  constructor(
    private userService: UserService,
    private userGroupService: UserGroupService,
    private cdRef: ChangeDetectorRef,
    public exportService: ExportService,
    route: ActivatedRoute,
    private router: Router
  ) {
    super();
    route.params.subscribe(param => {
      this.groupId = param['groupId'] || 0;
      if(this.state)this.refresh(this.state);
    });
  }
  refresh(state: ClrDatagridStateInterface) {
    this.loading = true;
    this.state = state;
    if(this.groupId > 0) this.pushFilter({groupId: this.groupId});
    this.cdRef.detectChanges();
    this.userGroupService.pagination(state).subscribe({
      next: (users: UserGroupDto) => {
        this.list = users;
        this.loading = false;
        this.cdRef.detectChanges();
      }
    });
  }
  export(state: ClrDatagridStateInterface) {
    const st = _.cloneDeep(state);
    if(st && st.page) st.page.size = 10000;
    this.userGroupService
      .pagination(st)
      .pipe(take(1))
      .subscribe((modules: UserGroupDto) => {
        this.exportService.userGroups(
          modules.content,
          `user-groups-${new Date().getMilliseconds()}`,
          {
            showLabels: true,
            headers: ['Created On', 'User', 'Group','Status'],
          }
        );
      });
  }

  deleteRecord(data: UserGroupDto | Array<UserGroupDto>) {
    this.selectedData = [];
    this.userGroupService.delete(this.userGroupService.getDeleteDto(data)).pipe(take(1)).subscribe(() => this.refresh(this.state));
  }
  activateRecord(data: UserGroupDto | Array<UserGroupDto>) {
    this.selectedData = [];
    this.userGroupService.activate(this.userGroupService.getDeleteDto(data)).pipe(take(1)).subscribe(() => this.refresh(this.state));
  }
  
  openDeleteModel(data: UserGroupDto | Array<UserGroupDto>) {
    this.userGroupDelete.userGroup = null!;
    if(_.isArray(data) && data.length > 1){
      this.userGroupDelete.userGroups = data;
    } else if(_.isArray(data) && data.length == 1){
      this.userGroupDelete.userGroup = data[0];
      this.userGroupDelete.status = data[0].status;
    } else if(!_.isArray(data)){
      this.userGroupDelete.userGroup = data;
      this.userGroupDelete.status = data.status;
    }
    this.userGroupDelete.open = true; 
  }
  
}
