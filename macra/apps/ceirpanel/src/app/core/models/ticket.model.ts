/* eslint-disable @typescript-eslint/no-explicit-any */

export interface TicketModel{
    ticketId: string;
    firstName: string;
    lastName: string;
    mobileNumber: string;
    alternateMobileNumber: string;
    category: any;
    categoryId: number;
    emailAddress: string;
    subject: string;
    description: string;
    userType: string;
    currentStatus: boolean;
    createdOn: Date;
    updatedOn: Date;
    raisedBy: string;
    resolved: boolean;
    issue: TicketIssueModel;
    captcha: string;
    referenceId: string;
    isPrivate: boolean;
}
export interface TicketList {
    content: TicketModel[];
    totalElements: number;
}
export interface TicketIssueModel {
    subject: string;
    description: string;
    createdOn: string;
    updatedOn: string;
    journals: Array<TickerJournalModel>;
    status: TicketStatusModel;
}
export interface TickerJournalModel {
    id: string;
    notes: string;
	createdOn: string;
	user: TicketUserModel;
    privateNotes: boolean;
}
export interface TicketUserModel {
    id: string;
    name: string;
}
export interface TicketStatusModel {
    id: string;
    name: string;
    closed: boolean;
}
export interface TicketNoteDto {
    ticketId: string;
    notes: string;
    visibility: string;
}
export interface TicketFeedbackDto {
    ticketId: string;
    feedback: string;
    ratings: number;
}