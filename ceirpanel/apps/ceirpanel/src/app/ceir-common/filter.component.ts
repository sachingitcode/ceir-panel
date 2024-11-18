/* eslint-disable @typescript-eslint/no-non-null-assertion */
/* eslint-disable @typescript-eslint/no-explicit-any */
import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild, inject } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ClrForm } from '@clr/angular';
import { NgbCalendar, NgbDate, NgbDateAdapter, NgbDateNativeAdapter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import * as _ from "lodash";
import { FilterModel } from '../core/models/filter.model';



@Component({
    selector: 'ceirpanel-filter',
    template: `
    <form clrForm clrLayout="vertical" #f="ngForm" (ngSubmit)="f.form.valid && onSubmit()" class="m-0 p-0">
    <div class="row m-0 p-0 mt-2" #clrlabel>
        <div class="clr-col-2 m-0 p-0" *ngIf="startDate">
            <clr-date-container>
                <label>{{ "placeholder.startDate" | translate }}</label>
                <input type="date" name="startDate" [(ngModel)]="startModel" clrDate autocomplete="off" [max]="max" (clrDateChange)="onStart($event)"/> 
            </clr-date-container>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="endDate">
            <clr-date-container>
                <label>{{ "placeholder.endDate" | translate }}</label>
                <input type="date" name="endDate" [(ngModel)]="endModel" clrDate [min]="min" [max]="max"/>
            </clr-date-container>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="userName">
            <div class="form-group">
                <label>{{ "placeholder.userName" | translate }}</label>
                <input type="text" id="userName" name="userName" [(ngModel)]="filterModel.userName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.userName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="firstName">
            <div class="form-group">
                <label>{{ "placeholder.firstName" | translate }}</label>
                <input type="text" id="firstName" name="firstName" [(ngModel)]="filterModel.firstName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.firstName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="lastName">
            <div class="form-group">
                <label>{{ "placeholder.lastName" | translate }}</label>
                <input type="text" id="lastName" name="lastName" [(ngModel)]="filterModel.lastName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.lastName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="organization">
            <div class="form-group">
                <label>{{ "placeholder.organization" | translate }}</label>
                <input type="text" id="organization" name="companyName" [(ngModel)]="filterModel.companyName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.organization' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="parentGroupName">
            <div class="form-group">
                <label>{{ "placeholder.parentGroupName" | translate }}</label>
                <input type="text" id="parentGroupName" name="parentGroupName" [(ngModel)]="filterModel.parentGroupName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.parentGroupName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="groupName">
            <div class="form-group">
                <label>{{ "placeholder.groupName" | translate }}</label>
                <input type="text" id="groupName" name="groupName" [(ngModel)]="filterModel.groupName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.groupName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="roleName">
            <div class="form-group">
                <label>{{ "placeholder.roleName" | translate }}</label>
                <input type="text" id="roleName" name="roleName" [(ngModel)]="filterModel.roleName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.roleName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="moduleTagName">
            <div class="form-group">
                <label>{{ "placeholder.moduleTagName" | translate }}</label>
                <input type="text" id="moduleTagName" name="moduleTagName" [(ngModel)]="filterModel.moduleTagName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.moduleTagName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="moduleName">
            <div class="form-group">
                <label>{{ "placeholder.moduleName" | translate }}</label>
                <input type="text" id="moduleName" name="moduleName" [(ngModel)]="filterModel.moduleName" class="form-control form-control-sm bg-primary-subtle"   
                [placeholder]="'filter.moduleName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="featureName">
            <div class="form-group">
                <label>{{ "placeholder.featureName" | translate }}</label>
                <input type="text" id="featureName" name="featureName" [(ngModel)]="filterModel.featureName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'filter.featureName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="featureModuleName">
            <div class="form-group">
                <label>{{ "placeholder.featureModuleName" | translate }}</label>
                <input type="text" id="featureModuleName" name="featureModuleName" [(ngModel)]="filterModel.featureModuleName" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'placeholder.featureModuleName' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="ticketId">
            <div class="form-group">
                <label>{{ "placeholder.ticketId" | translate }}</label>
                <input type="text" id="ticketId" name="ticketId" [(ngModel)]="filterModel.ticketId" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'placeholder.ticketId' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="phoneNo">
            <div class="form-group">
                <label>{{ "placeholder.phoneNo" | translate }}</label>
                <input type="text" id="mobileNumber" name="mobileNumber" [(ngModel)]="filterModel.mobileNumber" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'placeholder.phoneNo' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="raisedBy">
            <div class="form-group">
                <label>{{ "placeholder.raisedBy" | translate }}</label>
                <input type="text" id="raisedBy" name="raisedBy" [(ngModel)]="filterModel.raisedBy" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'placeholder.raisedBy' | translate"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="userId">
            <div class="form-group">
                <label>User ID</label>
                <input type="text" id="userId" name="raisedBy" [(ngModel)]="filterModel.raisedBy" class="form-control form-control-sm bg-primary-subtle"  
                [placeholder]="'User ID'"/>
            </div>
        </div>
        <div class="clr-col-2 m-0 p-0 ms-1" *ngIf="ticketStatus">
            <div class="form-group">
                <label>{{ "placeholder.ticketStatus" | translate }}</label>
                <select name="options" [(ngModel)]="filterModel.ticketStatus" name="ticketStatus" class="form-select form-control form-control-sm bg-primary-subtle">
                    <option selected [value]="''">Select All</option>
                    <option value="New">New</option>
                    <option value="InProgress">InProgress</option>
                    <option value="Resolved">Resolved</option>
                    <option value="Closed">Closed</option>
                </select>
            </div>
        </div>
    </div>
</form>
  `,
    styles: [`
    :host ::ng-deep .clr-control-container {
        display: inline-block;
        display: block;
        width: 100%;
        padding: 0.240rem 0.75rem;
        font-size: 1rem;
        line-height: .1;
        color: #495057;
        background-color: #fff;
        background-clip: padding-box;
        border: 1px solid #ced4da;
        border-radius: 0.25rem;
        transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
        background-color: #EFF5FF;
    }
    :host ::ng-deep .clr-form-control {
        margin-top: 0.05rem !important;
    }
    :host ::ng-deep .clr-input-group .clr-input-group-icon-action {
        padding: 0 0rem;
    }
    :host ::ng-deep .clr-input-group {
        color: #000;
        color: var(--clr-forms-text-color, hsl(198, 0%, 0%));
        border-bottom: none;
        max-width: 90%;
    }
    :host ::ng-deep .clr-input-group:focus {
        border-bottom: none;
    }
    .clr-form-control {
        margin-top: 0rem;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
    }

  `],
     providers: [
        { provide: NgbDateAdapter, useClass: NgbDateNativeAdapter },
        //{ provide: NgbDateParserFormatter, useClass: NgbDateISOParserFormatter }
    ]
})
export class FilterComponent implements OnInit, AfterViewInit {
    
    @ViewChild(ClrForm, { static: true }) public clrForm!: ClrForm;
    @Input() openFilter = false;
    @Input() startDate = false;
    @Input() endDate = false;
    @Input() groupName = false;
    @Input() parentGroupName = false;
    @Input() moduleTagName = false;
    @Input() moduleName = false;
    @Input() featureName = false;
    @Input() featureModuleName = false;
    @Input() roleName = false;
    @Input() userName = false;
    @Input() firstName = false;
    @Input() lastName = false;
    @Input() organization = false;
    @Input() phoneNo = false;
    @Input() ticketId = false;
    @Input() raisedBy = false;
    @Input() userId = false;
    @Input() ticketStatus = false;
    @ViewChild('clrlabel', { read: ElementRef, static:false }) clrlabel!: ElementRef;

    @Output() public filter: EventEmitter<any> = new EventEmitter();
    @Input() filterModel: FilterModel = {ticketStatus: 'All'} as FilterModel;
    @ViewChild('f') private filterForm! : NgForm;

    startModel!: any;
    endModel!: any;
    calendar = inject(NgbCalendar);
    toDate: NgbDate | null = this.calendar.getNext(this.calendar.getToday(), 'd', 10);
    fromDate: NgbDate | null = this.calendar.getToday();
    minEndDate: any = new Date();
    minPickerDate = {
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
        day: new Date().getDate()
    };
    model!: string;
    current = new Date();
    month = `0${this.current.getMonth() +1 }`.slice(-2);
    max = `${this.current.getFullYear()}-${this.month}-${this.current.getDate()}`;
    min = this.max;
    
    ngOnInit(): void {
        console.log();
    }
    ngAfterViewInit(): void {
        this.clrlabel.nativeElement.querySelectorAll('.clr-control-label').forEach(
            (question: any) => {
                question.classList.remove('clr-control-label');
            }
        );
    }
    onSubmit() {
        if(!_.isUndefined(this.startModel) && !_.isEmpty(this.startModel))this.filterModel.startDate = this.toSystemDate(this.startModel);
        if(!_.isUndefined(this.endModel) && !_.isEmpty(this.endModel))this.filterModel.endDate = this.toSystemDate(this.endModel);
        this.filter.emit(this.filterModel);
    }
    reset(){
        this.filterForm.reset({ticketStatus: null});
        this.filterModel.startDate = null!;
        this.filterModel.endDate = null!;
        this.filterModel.ticketStatus = ''!;
        this.filter.emit(this.filterModel);
    }
    toSystemDate(date: NgbDateStruct) {
        console.log('date:',date);
        return `${date}`;
    }
    onStart(startDate: Date) {
        if(!_.isEmpty(this.endModel)) {
            const end = new Date(_.toString(this.endModel));
            if(end < startDate) {
                const month = Number(`0${startDate.getMonth() +1 }`.slice(-2));
                this.endModel = `${startDate.getFullYear()}-${month}-${startDate.getDate()}`;
            }
        }
        const month = `0${startDate.getMonth() +1 }`.slice(-2);
        this.min = `${startDate.getFullYear()}-${month}-${startDate.getDate()}`;
    }
}