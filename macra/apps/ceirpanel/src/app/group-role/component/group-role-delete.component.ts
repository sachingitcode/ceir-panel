/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { RoleModel } from '../../core/models/role.model';

export enum ModalSize {
    small = 'sm',
    medium = 'md',
    large = 'lg',
    extraLarge = 'xl',
  }

@Component({
  selector: 'ceirpanel-group-role-delete',
  template: `
  <clr-modal [(clrModalOpen)]="open" [clrModalStaticBackdrop]="false" [clrModalSize]="modalSize.large" [clrModalClosable]="false">
    <h3 class="modal-title">Remove Role From Group</h3>
    <div class="modal-body">
        <div *ngIf="group; then singleselect else multiselect"></div>
        <ng-template #singleselect>
            <form clrStepper clrForm clrLayout="vertical" class="m-0 p-0" #f="ngForm" #singleselect>
                <fieldset [disabled]="readonly" class="scheduler-border">
                    <legend class="scheduler-border">Group Role Information</legend>
                    <div class="clr-row m-0 p-0">
                        <div class="clr-col-6 m-0 p-0">
                            <clr-input-container>
                                <label>{{ "role.roleName.label" | translate }}</label>
                                <input clrInput type="text" name="roleName" class="w-100" [(ngModel)]="group.roleName" [placeholder]="'role.roleName.placeholder' | translate" required/>
                            </clr-input-container>
                        </div>
                        <div class="clr-col-6">
                            <clr-input-container>
                                <label class="clr-required-mark">{{ "role.description.label" | translate }}</label>
                                <input clrInput type="text" name="description" class="w-100" [(ngModel)]="group.description" [placeholder]="'role.description.placeholder' | translate" required/>
                            </clr-input-container>
                        </div>
                    </div>
                </fieldset>
                <div class="clr-row clr-justify-content-end mt-3">
                    <div class="clr-col-2">
                        <button type="submit" class="btn btn-primary btn-block" (click)="open = false;confirmation.emit(true)">{{ "button.delete" | translate }}</button>
                    </div>
                    <div class="clr-col-2">
                        <button type="button" class="btn btn-outline btn-block" (click)="open = false;confirmation.emit(false)">{{ "button.cancel" | translate }}</button>
                    </div>
                </div>
            </form>
        </ng-template>
        <ng-template #multiselect>
            <table class="table m-0 p-0">
                <thead>
                    <tr>
                        <th>Role Name</th>
                        <th>Description</th>
                    </tr>
                </thead>
                <tbody>
                    <tr *ngFor="let g of groups">
                        <td>{{g?.roleName}}</td>
                        <td>{{g?.description}}</td>
                    </tr>
                </tbody>
            </table>
            <div class="clr-row clr-justify-content-end mt-3">
                <div class="clr-col-2">
                    <button type="submit" class="btn btn-primary btn-block" (click)="open = false;confirmation.emit(true)">{{ "button.delete" | translate }}</button>
                </div>
                <div class="clr-col-2">
                    <button type="button" class="btn btn-outline btn-block" (click)="open = false;confirmation.emit(false)">{{ "button.cancel" | translate }}</button>
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
export class GroupRoleDeleteComponent implements OnInit{
    modalSize = ModalSize;
    selectedSize!: ModalSize;
    @Input() open = false;
    group: RoleModel = {} as RoleModel;
    @Input() groups: Array<any> = [];
    @Output() public confirmation: EventEmitter<boolean> = new EventEmitter();
    readonly = true;
    parentGroupName = 'Not Available';
    ngOnInit(): void {
        console.log('groups: ', this.groups);
        this.group = this.groups.length == 1 ? this.groups[0]: null;
        console.log('group: ', this.group);
    }
}