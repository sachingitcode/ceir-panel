/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { ClrDatagridStateInterface } from '@clr/angular';
import * as _ from 'lodash';
import { take } from 'rxjs';
import { ExtendableListComponent } from '../../ceir-common/extendable-list';
import { ExportService } from '../../core/services/common/export.service';
import { GroupFeatureDeleteComponent } from '../component/group-feature-delete.component';
import { GroupFeatureDto } from '../dto/group.feature.dto';
import { GroupFeatureService } from '../service/group.feature.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'ceirpanel-group-feature-list',
  templateUrl: '../html/group-feature-list.component.html',
  styles: [
    `
      .dropdown-toggle::after {
        display: none;
      }
    `,
  ],
})
export class GroupFeatureListComponent extends ExtendableListComponent {
  @ViewChild(GroupFeatureDeleteComponent) groupFeatureDelete!: GroupFeatureDeleteComponent;
  list: GroupFeatureDto = { totalElements: 0 } as GroupFeatureDto;
  groupId = 0;
  public loading = true;
  constructor(
    private cdRef: ChangeDetectorRef,
    public exportService: ExportService,
    private groupFeatureService: GroupFeatureService,
    route: ActivatedRoute
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
    this.groupFeatureService.pagination(state).subscribe({
      next: (users: GroupFeatureDto) => {
        this.list = users;
        this.loading = false;
        this.cdRef.detectChanges();
      }
    });
  }
  export(state: ClrDatagridStateInterface) {
    const st = _.cloneDeep(state);
    if(st && st.page) st.page.size = 10000;
    this.groupFeatureService
      .pagination(st)
      .pipe(take(1))
      .subscribe((modules: GroupFeatureDto) => {
        this.exportService.groupFeatures(
          modules.content,
          `group-features-${new Date().getMilliseconds()}`,
          {
            showLabels: true,
            headers: ['Created On', 'Group', 'Feature','Status'],
          }
        );
      });
  }
  deleteRecord(data: GroupFeatureDto | Array<GroupFeatureDto>) {
    this.selectedData = [];
    this.groupFeatureService.delete(this.groupFeatureService.getDeleteDto(data)).pipe(take(1)).subscribe(() => this.refresh(this.state));
  }
  activateRecord(data: GroupFeatureDto | Array<GroupFeatureDto>) {
    this.selectedData = [];
    this.groupFeatureService.activate(this.groupFeatureService.getDeleteDto(data)).pipe(take(1)).subscribe(() => this.refresh(this.state));
  }
  
  openDeleteModel(data: GroupFeatureDto | Array<GroupFeatureDto>, status?: string) {
    this.groupFeatureDelete.userGroup = null!;
    if(_.isArray(data) && data.length > 1){
      this.groupFeatureDelete.userGroups = data;
    } else if(_.isArray(data) && data.length == 1){
      this.groupFeatureDelete.userGroup = data[0];
      this.groupFeatureDelete.status = data[0].status;
    } else if(!_.isArray(data)){
      this.groupFeatureDelete.userGroup = data;
      this.groupFeatureDelete.status = data.status;
    }
    this.groupFeatureDelete.open = true; 
  }
}
