/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TicketFeedbackDto, TicketModel } from '../../core/models/ticket.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';

export enum ModalSize {
    small = 'sm',
    medium = 'md',
    large = 'lg',
    extraLarge = 'xl',
  }

@Component({
  selector: 'ceirpanel-ticket-feedback',
  template: `
  <clr-modal [(clrModalOpen)]="open" [clrModalStaticBackdrop]="false" [clrModalSize]="modalSize.large" [clrModalClosable]="false">
    <h3 class="modal-title">Share your opinion with us!</h3>
    <div class="modal-body m-0 p-0">
      <div class="clr-row m-0 p-0">
        <div class="clr-col-12 m-0 p-0">
          <label class="form-label clr-required-mark">How would you rate it?</label>
          <ngx-stars [readonly]="false" [size]="2"  [color]="'#A36500'" [initialStars]="rate.ratings" [maxStars]="5" (ratingOutput)="onRatingSet($event)"></ngx-stars>
        </div>
        <div class="clr-col-12 m-0 p-0">
          <clr-textarea-container class="border-0">
            <label>Write Feedback</label>
            <textarea clrTextarea name="notes" required class="w-100" [(ngModel)]="rate.feedback" name="feedback" 
              placeholder="Write your feedback here..."></textarea>
          </clr-textarea-container>
        </div>
      </div>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-outline" (click)="open = false;confirmation.emit({open: false, resolve: 'no'})">No</button>
        <button type="button" class="btn btn-primary" (click)="open = false;confirmation.emit({open: false, ratings: this.rate.ratings, resolve: 'yes', feedback: this.rate.feedback})">Yes</button>
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
export class TicketFeedbackComponent implements OnInit{
    modalSize = ModalSize;
    selectedSize!: ModalSize;
    @Input() open = false;
    @Input() ticketId!: any;
    @Output() public confirmation: EventEmitter<any> = new EventEmitter();
    ticket: TicketModel = {} as TicketModel;
    public rate: TicketFeedbackDto = {ratings: 0} as any;

    constructor(private apicall: ApiUtilService){}
    
    ngOnInit(): void {
        console.log('ticket: ', this.ticketId);
    }
    resolve(){
      this.confirmation.emit({open: false, ratings: this.rate.ratings, resolve: 'yes', feedback: this.rate.feedback});
    }
    onRatingSet(rating: number): void {
      console.log('rating: ', rating);
      this.rate.ratings = rating;
    }
}