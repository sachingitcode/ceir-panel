/* eslint-disable @typescript-eslint/no-explicit-any */
import { Injectable } from '@angular/core';
import { ngxCsv } from 'ngx-csv/ngx-csv';
import { UserModel } from '../../models/user.model';
import { GroupModel } from '../../models/group.model';
import { FeatureModel } from '../../models/feature.model';
import { TagModel } from '../../models/tag.model';
import { ModuleMangeModel } from '../../models/module.manage.model';
import { GroupFeatureModel } from '../../models/group.feature.model';
import { RoleModel } from '../../models/role.model';
import { AclModel } from '../../models/acl.model';
import { GroupRoleDto } from '../../../group/dto/group.role.dto';
import { FeatureModuleDto } from '../../../feature-module/dto/feature.module.dto';
import { UserRoleDto } from '../../../user/dto/user.role.dto';
import { UserGroupDto } from '../../../user-group/dto/user-group.dto';
import { UserFeatureDto } from '../../../user/dto/user.feature.dto';
import { GroupFeatureDto } from '../../../group/dto/group.feature.dto';
import { TicketModel } from '../../models/ticket.model';

@Injectable({
  providedIn: 'root'
})
export class ExportService {
  private export(data: any [], fileName: string, options: any) {
    new ngxCsv(data, fileName, options);
  }
  public users(users: Array<UserModel>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({id:u.id,created: u.createdOn,firstName: u.profile.firstName, lastName: u.profile.lastName,userName:u?.userName,organization:u?.profile?.companyName,status:u.currentStatus}))
    this.export(data, fileName, options);
  }
  public groups(users: Array<GroupModel>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({id:u?.id,created: u?.createdOn,groupName: u?.groupName, parentGroupName: u.parent? u.parent?.groupName: 'NA'}));
    this.export(data, fileName, options);
  }
  public features(users: Array<FeatureModel>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({id:u?.id,created: u?.createdOn,featureName: u?.featureName, category: u.category,status: u.status}));
    this.export(data, fileName, options);
  }
  public tags(users: Array<TagModel>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({id:u?.id,created: u?.createdOn,featureName: u?.moduleTagName,status: u?.status}));
    this.export(data, fileName, options);
  }
  public modules(users: Array<ModuleMangeModel>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({id:u?.id,created: u?.createdOn,moduleName: u.moduleName,status: u?.status}));
    this.export(data, fileName, options);
  }
  public featureModules(users: Array<FeatureModuleDto>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({created:u?.createdOn,feature:u?.feature?.featureName,module: u?.module?.moduleName,status:u?.status}));
    this.export(data, fileName, options);
  }
  public groupFeatures(users: Array<GroupFeatureDto>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({created: u?.createdOn,groupName: u?.group?.groupName, featureName: u?.feature?.featureName,status:u?.status}));
    this.export(data, fileName, options);
  }
  public userFeatures(users: Array<UserFeatureDto>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({created: u?.createdOn,userName: u?.user?.userName, featureName: u?.feature?.featureName,status: u?.status}));
    this.export(data, fileName, options);
  }
  public userGroups(users: Array<UserGroupDto>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({created: u?.createdOn,userName: u?.user?.userName, groupName: u?.group?.groupName,status:u?.status}));
    this.export(data, fileName, options);
  }
  public roles(users: Array<RoleModel>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({id:u?.id,created: u?.createdOn,roleName: u.roleName, access: u.access,status: u.status}));
    this.export(data, fileName, options);
  }
  public userRoles(users: Array<UserRoleDto>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({created: u?.createdOn,userName: u?.user.userName, role: u?.role?.roleName,status:u?.status}));
    this.export(data, fileName, options);
  }
  public groupRoles(users: Array<GroupRoleDto>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({created: u?.createdOn,groupName: u?.group?.groupName,role: u?.role?.roleName,status: u?.status}));
    this.export(data, fileName, options);
  }
  public acl(users: Array<AclModel>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({created: u?.createdOn,roleName: u?.role?.roleName,feature: u?.feature?.featureName, module: u?.module?.moduleName}));
    this.export(data, fileName, options);
  }
  public tickets(users: Array<TicketModel>, fileName: string, options: any) {
    const data: any[] = [] as any;
    users.forEach(u => data.push({id:u?.ticketId,phone:u?.mobileNumber,subject:u.issue.subject,createdOn:u?.issue.createdOn,modifedOn:u?.issue.updatedOn,raisedBy:u?.raisedBy,status:u?.issue?.status?.name}));
    this.export(data, fileName, options);
  }
}
