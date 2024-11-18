/* eslint-disable @typescript-eslint/no-explicit-any */
import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { TicketModel, TicketNoteDto } from '../../core/models/ticket.model';
import { ClrForm } from '@clr/angular';
import { NgForm } from '@angular/forms';

export enum ModalSize {
    small = 'sm',
    medium = 'md',
    large = 'lg',
    extraLarge = 'xl',
  }

@Component({
  selector: 'ceirpanel-ticket-resolve',
  template: `
  <clr-modal [(clrModalOpen)]="open" [clrModalStaticBackdrop]="false" [clrModalSize]="modalSize.medium" [clrModalClosable]="false">
    <h3 class="modal-title">Resolve Ticket?</h3>
    <div class="modal-body m-0 p-0">
      <form clrForm clrLayout="vertical" #f="ngForm" (ngSubmit)="f.form.valid && onSubmit(f)" novalidate>
        <div class="clr-row m-0 p-0">
          <div class="clr-col-12 m-0 p-0">
            <div class="form-group">
              <label class="form-label">Are you sure want to mark this ticket as resolved?</label>
              <textarea class="form-control" class="form-control form-control-sm" [(ngModel)]="notes.notes" name="notes" style="min-height: 60px !important;"
               placeholder="Write new comment here(if any)" #noteObj="ngModel" [ngClass]="{ 'is-invalid': f.submitted && noteObj.errors }"
                ></textarea>
              <div *ngIf="f.submitted && noteObj.errors" class="invalid-feedback">
                <div *ngIf="noteObj.errors['required']">Feedback is required</div>
              </div> 
            </div>
          </div>
          <div class="clr-col-12 m-0 p-0 d-flex justify-content-center mt-4">
            <button type="submit" class="btn btn-primary">Yes</button>
            <button type="button" class="btn btn-outline" (click)="open = false;confirmation.emit({open: false, resolve: 'no'})">No</button>
          </div>
        </div>
      </form>
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
export class TicketResolveComponent implements OnInit{
    modalSize = ModalSize;
    selectedSize!: ModalSize;
    @Input() open = false;
    @Input() ticketId!: any;
    @Output() public confirmation: EventEmitter<any> = new EventEmitter();
    ticket: TicketModel = {} as TicketModel;
    public notes: TicketNoteDto = {} as any;
    @ViewChild(ClrForm, { static: true }) public clrForm!: ClrForm;
    @ViewChild("f") public ngForm!: NgForm;

    constructor(private apicall: ApiUtilService){}
    
    ngOnInit(): void {
        console.log('ticket: ', this.ticketId);
    }
    resolve(){
      this.confirmation.emit({open: false, notes: this.notes.notes, resolve: 'yes'});
    }
    onSubmit(userForm: NgForm) {
      if (userForm.invalid) { this.clrForm.markAsTouched(); return; }
      this.confirmation.emit({open: false, notes: this.notes.notes, resolve: 'yes'});
    }
}