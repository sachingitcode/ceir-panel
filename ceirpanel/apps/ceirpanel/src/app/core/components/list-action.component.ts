import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ClrDatagridStateInterface } from '@clr/angular';
import * as _ from "lodash";

export enum ModalSize {
  small = 'sm',
  medium = 'md',
  large = 'lg',
  extraLarge = 'xl',
}

@Component({
  selector: 'ceirpanel-list-action',
  template: `
 <div class="row">
    <div class="col-12 text-right">
      <ng-template [ngxPermissionsOnly]="[feature + '_FILTER']" [ngxPermissionsOnlyThen]="showFilter"></ng-template>
      <ng-template [ngxPermissionsOnly]="[feature + '_EXPORT']" [ngxPermissionsOnlyThen]="showExport"></ng-template>
      <button type="button" class="btn btn-icon btn-secondary" (click)="refresh.emit(state)" *ngIf="!isTicket">
        <cds-icon shape="refresh"></cds-icon>
      </button>
      <ng-template #showExport>
        <button type="button" class="btn btn-icon btn-secondary" (click)="download.emit(state)">
          {{ "button.export" | translate }}
          <cds-icon shape="download"></cds-icon>
        </button>
      </ng-template>
      <ng-template #showFilter>
        <button type="button" class="btn btn-warning btn-outline" (click)="resetFilter.emit()">Reset All</button>
        <button type="button" class="btn btn-primary" (click)="filter.emit()">{{ "button.filter" | translate }} <img src="assets/images/filter-icon.svg" alt="icon" class="img-fluid ml-1"></button>
      </ng-template>
    </div>
  </div>
  `,
  styles: [``],
})
export class ListActionComponent implements OnInit {
  @Input() open = false;
  @Input() feature!:string;
  @Input() public state!: ClrDatagridStateInterface;
  @Output() public download: EventEmitter<ClrDatagridStateInterface> = new EventEmitter();
  @Output() public refresh: EventEmitter<ClrDatagridStateInterface> = new EventEmitter();
  @Output() public filter: EventEmitter<void> = new EventEmitter();
  @Output() public resetFilter: EventEmitter<void> = new EventEmitter();
  public isTicket = false;

  constructor(private route: ActivatedRoute) {
    this.isTicket = this.route.snapshot.url[0].path.includes('ticket');
  }

  ngOnInit(): void {
    console.log('error');
  }

}
