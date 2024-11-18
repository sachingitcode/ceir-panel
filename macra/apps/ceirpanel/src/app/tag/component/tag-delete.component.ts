/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TagModel } from '../../core/models/tag.model';

export enum ModalSize {
    small = 'sm',
    medium = 'md',
    large = 'lg',
    extraLarge = 'xl',
  }

@Component({
  selector: 'ceirpanel-tag-delete',
  template: `
  <clr-modal [(clrModalOpen)]="open" [clrModalStaticBackdrop]="false" [clrModalSize]="modalSize.large" [clrModalClosable]="false">
    <h3 class="modal-title">
        <div *ngIf="status!=='4' && status !=='0'; then delete else active"></div>
        <ng-template #delete>Remove Tag</ng-template>
        <ng-template #active>Active Tag</ng-template>
    </h3>
    <div class="modal-body">
        <div *ngIf="tag; then singleselect else multiselect"></div>
        <ng-template #singleselect>
            <form clrStepper clrForm clrLayout="vertical" class="m-0 p-0" #f="ngForm" #singleselect>
                <fieldset [disabled]="readonly" class="scheduler-border">
                    <legend class="scheduler-border">Module Tag Information</legend>
                    <div class="clr-row m-0 p-0">
                    <div class="clr-col-6 m-0 p-0">
                        <div class="form-group">
                            <label class="clr-required-mark">{{ "tag.moduleTagName.label" | translate }}</label>
                            <input type="text" name="tagName" class="form-control form-control-sm" [(ngModel)]="tag.moduleTagName" [placeholder]="'tag.moduleTagName.placeholder' | translate" required/>
                        </div>
                    </div>
                    <div class="clr-col-12 m-0 p-0">
                        <div class="form-group">
                            <label class="clr-mark">{{ "tag.description.label" | translate }}</label>
                            <textarea name="description" class="form-control form-control-sm" [(ngModel)]="tag.description" [placeholder]="'tag.description.placeholder' | translate"></textarea>  
                        </div>
                    </div>
                    </div>
                </fieldset>
                <div class="clr-row clr-justify-content-end mt-3">
                    <div class="clr-col-2">
                        <div *ngIf="status!=='4' && status!=='0'; then delete else active"></div>
                        <ng-template #delete>
                            <button type="submit" class="btn btn-primary btn-block" (click)="open = false;confirmation.emit(tag)">{{ "button.delete" | translate }}</button>
                        </ng-template>
                        <ng-template #active>
                            <button type="submit" class="btn btn-primary btn-block" (click)="open = false;aconfirmation.emit(tag)">{{ "button.active" | translate }}</button>
                        </ng-template>
                    </div>
                    <div class="clr-col-2">
                        <button type="button" class="btn btn-outline btn-block" (click)="open = false;">{{ "button.cancel" | translate }}</button>
                    </div>
                </div>
            </form>
        </ng-template>
        <ng-template #multiselect>
            <table class="table m-0 p-0">
                <thead>
                    <tr>
                        <th>Module Tag Name</th>
                        <th>Description</th>
                    </tr>
                </thead>
                <tbody>
                    <tr *ngFor="let tag of tags">
                        <td>{{tag?.tagName}}</td>
                        <td>{{tag?.description}}</td>
                    </tr>
                </tbody>
            </table>
            <div class="clr-row clr-justify-content-end mt-3">
                <div class="clr-col-2">
                    <button type="submit" class="btn btn-primary btn-block" (click)="open = false;confirmation.emit(tags)">{{ "button.delete" | translate }}</button>
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
    :host ::ng-deep .modal-header, .modal-header--accessible {
    border-bottom: none;
    padding: 0 0 0.1rem 0;
    }
  `],
  providers: []
})
export class TagDeleteComponent{
    modalSize = ModalSize;
    selectedSize!: ModalSize;
    @Input() open = false;
    tag: TagModel = {} as TagModel;
    @Input() tags: Array<any> = [];
    @Output() public confirmation: EventEmitter<TagModel | Array<TagModel>> = new EventEmitter();
    @Output() public aconfirmation: EventEmitter<TagModel | Array<TagModel>> = new EventEmitter();
    readonly = true;
    status = '4';
}