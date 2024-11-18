/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UserFeatureDto } from '../dto/user.feature.dto';

export enum ModalSize {
    small = 'sm',
    medium = 'md',
    large = 'lg',
    extraLarge = 'xl',
  }

@Component({
  selector: 'ceirpanel-user-feature-delete',
  template: `
  <clr-modal [(clrModalOpen)]="open" [clrModalStaticBackdrop]="false" [clrModalSize]="modalSize.large" [clrModalClosable]="false">
    <h3 class="modal-title">
        <div *ngIf="status!=='4' && status !== '0'; then delete else active"></div>
        <ng-template #delete>Remove User From Role</ng-template>
        <ng-template #active>Active User For Role</ng-template>
    </h3>
    <div class="modal-body">
        <div *ngIf="userGroup; then singleselect else multiselect"></div>
        <ng-template #singleselect>
            <form clrStepper clrForm clrLayout="vertical" class="m-0 p-0" #f="ngForm" #singleselect>
                <fieldset [disabled]="readonly" class="scheduler-border">
                    <legend class="scheduler-border">User Role Information</legend>
                    <div class="clr-row m-0 p-0">
                    <div class="clr-col-6 m-0 p-0">
                        <div class="form-group">
                            <label>{{ "placeholder.userName" | translate }}</label>
                            <input type="text" name="firstName" class="form-control form-control-sm" [(ngModel)]="userGroup.user.userName" required/>
                        </div>
                    </div>
                    <div class="clr-col-6">
                        <div class="form-group">
                            <label class="clr-required-mark">{{ "placeholder.featureName" | translate }}</label>
                            <input type="text" name="lastName" class="form-control form-control-sm" [(ngModel)]="userGroup.feature.featureName" required/>
                    </div>
                    </div>
                    </div>
                </fieldset>
                <div class="clr-row clr-justify-content-end mt-3">
                    <div class="clr-col-2">
                        <div *ngIf="status!=='4' && status !== '0'; then delete else active"></div>
                        <ng-template #delete>
                            <button type="submit" class="btn btn-primary btn-block" (click)="open = false;confirmation.emit(userGroup)">{{ "button.delete" | translate }}</button>
                        </ng-template>
                        <ng-template #active>
                            <button type="submit" class="btn btn-primary btn-block" (click)="open = false;aconfirmation.emit(userGroup)">{{ "button.active" | translate }}</button>
                        </ng-template>
                    </div>
                    <div class="clr-col-2">
                        <button type="button" class="btn btn-outline btn-block" (click)="open = false">{{ "button.cancel" | translate }}</button>
                    </div>
                </div>
            </form>
        </ng-template>
        <ng-template #multiselect>
            <table class="table m-0 p-0">
                <thead>
                    <tr>
                        <th>User Name</th>
                        <th>Role Name</th>
                    </tr>
                </thead>
                <tbody>
                    <tr *ngFor="let g of userGroups">
                        <td>{{g?.user?.userName}}</td>
                        <td>{{g?.role?.roleName}}</td>
                    </tr>
                </tbody>
            </table>
            <div class="clr-row clr-justify-content-end mt-3">
                <div class="clr-col-2">
                    <button type="submit" class="btn btn-primary btn-block" (click)="open = false;confirmation.emit(userGroups)">{{ "button.delete" | translate }}</button>
                </div>
                <div class="clr-col-2">
                    <button type="button" class="btn btn-outline btn-block" (click)="open = false;">{{ "button.cancel" | translate }}</button>
                </div>
            </div>
        </ng-template>
    </div>
</clr-modal>
  `,
  styles: [`
    :host ::ng-deep .modal-footer {
    display: flex;
    justify-content: flex-end;
    padding: 1.2rem 1rem 1rem 1rem;
    }
  `],
  providers: []
})
export class UserFeatureDeleteComponent{
    modalSize = ModalSize;
    selectedSize!: ModalSize;
    @Input() open = false;
    userGroup!: UserFeatureDto;
    userGroups: Array<any> = [];
    @Output() public confirmation: EventEmitter<UserFeatureDto | Array<UserFeatureDto>> = new EventEmitter();
    @Output() public aconfirmation: EventEmitter<UserFeatureDto | Array<UserFeatureDto>> = new EventEmitter();
    readonly = true;
    parentGroupName = 'Not Available';
    status = '4';
}