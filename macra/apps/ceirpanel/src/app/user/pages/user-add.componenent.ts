/* eslint-disable @typescript-eslint/no-explicit-any */
import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ClrForm, ClrStepButton, ClrStepper } from '@clr/angular';
import * as _ from 'lodash';
import { Observable, take } from 'rxjs';
import { PageType } from '../../core/constants/page.type';
import { Address, Contact, Id, Name, Password, Reporting, Security, UserCreate } from '../../core/models/user-create.model';
import { UserModel } from '../../core/models/user.model';
import { ApiUtilService } from '../../core/services/common/api.util.service';
import { MenuTransportService } from '../../core/services/common/menu.transport.service';
import { UserService } from '../service/user.service';


@Component({
  selector: 'ceirpanel-user-add',
  templateUrl: '../html/user-add.component.html',
  styles: [`
  :host ::ng-deep .clr-input-group {
    width: 100% !important;
  }
  `],
  providers: []
})
export class UserAddComponent implements OnInit, AfterViewInit {
  initialStep = 'name';
  steps = ['name', 'address', 'id', 'reporting', 'password', 'contact', 'security'];
  @ViewChild(ClrStepButton, { static: false }) stepButton!: ClrStepButton;
  @ViewChild(ClrStepper, { static: false }) clrStepper!: ClrStepper;
  @ViewChild(ClrForm, { static: true }) private clrForm!: ClrForm;
  readonly = false;
  public page: string = '' as string;
  public path: string = '' as string;
  public id: number = 0 as number;
  public cancel = false;
  public questions: Array<any> = [];
  public alreadyExist = true;
  public idCardFile!: File;
  public nidFile!: File;
  public photoFile!: File;
  idCardObject: Array<any> = [] as Array<any>;
  nidCardObject: Array<any> = [] as Array<any>;
  photoObject: Array<any> = [] as Array<any>;
  countries: Array<any> = [] as Array<any>;
  provinces: Array<any> = [] as Array<any>;
  districts: Array<any> = [] as Array<any>;
  communes: Array<any> = [] as Array<any>;
  villages: Array<any> = [] as Array<any>;

  templateForm: UserCreate = {
    name: { firstName: '', lastName: '' } as Name, address: {country:'',province:'',district:'',commune:'', village:''} as Address, 
    id: {} as Id, reporting: {} as Reporting,
    password: {} as Password, contact: {} as Contact, security: { question1: 0, question2: 0, question3: 0 } as Security
  } as UserCreate;

  constructor(
    private userService: UserService,
    private cdref: ChangeDetectorRef,
    private route: ActivatedRoute,
    private apicall: ApiUtilService,
    private router: Router,
    private transport: MenuTransportService,
    private el: ElementRef) { }

  ngOnInit(): void {
    this.path = this.route.snapshot.url[0].path;
    this.page = this.route.snapshot.paramMap.get('page') || '';
    this.id = Number(this.route.snapshot.paramMap.get('id') || 0);
    this.initializeStep();
    this.findQuestions();
    this.readonly = this.page === 'view' ? true: false;
    if (this.id > 0) {
      this.userService.view(this.id).pipe(take(1)).subscribe((obj: UserModel) => {
        this.templateForm = this.userService.getUserCreateObj(obj);
        this.findCountries();
        this.nidCardObject = this.userService.getNidCardObject(obj);
        this.idCardObject = this.userService.getIdCardObject(obj);
        this.photoObject = this.userService.getPhotoObject(obj);
      });
    } else {
      this.findCountries();
    }
  }
  onNidSelect(event: any) {
    this.nidCardObject = [];
    this.nidFile = event.target.files[0];
    const localImageUrl = (window.URL) ? window.URL.createObjectURL(this.nidFile) : (window as any).webkitURL.createObjectURL(this.nidFile);
    this.nidCardObject.push({ image:localImageUrl , thumbImage:localImageUrl });
  }
  onUserPhotoSelect(event: any) {
    this.photoObject = [];
    this.photoFile = event.target.files[0];
    const localImageUrl = (window.URL) ? window.URL.createObjectURL(this.photoFile) : (window as any).webkitURL.createObjectURL(this.photoFile);
    this.photoObject.push({ image: localImageUrl, thumbImage: localImageUrl });
  }
  onIdSelect(event: any) {
    this.idCardObject = [];
    this.idCardFile = event.target.files[0];
    const localImageUrl = (window.URL) ? window.URL.createObjectURL(this.idCardFile) : (window as any).webkitURL.createObjectURL(this.idCardFile);
    this.idCardObject.push({ image: localImageUrl, thumbImage: localImageUrl });
  }
  ngAfterViewInit(): void {
    this.el.nativeElement.querySelector('input[type="password"]').setAttribute('autocomplete', 'new-password');
    this.toggleOn();
  }

  initializeStep() {
    this.steps.forEach(step => this.initialStep = step);
  }

  toggleOn() {
    this.readonly = this.page == PageType.view;
    this.clrStepper.panels.forEach(pannel => {
      if (this.steps.indexOf(pannel.id) > -1) {
        pannel.togglePanel();
      }
    });
    this.cdref.detectChanges();
  }
  onSubmit(userForm: NgForm) {
    console.log('invalid: ',userForm.invalid);
    if (userForm.invalid) { this.clrForm.markAsTouched(); return; }
    this.save();
  }
  putData(formData: FormData, feature: any) {
    const filters: any[] = [];
    Object.keys(feature).forEach((key) => {
      filters.push({ property: key, value: feature[key] });
      formData.append(key, feature[key]);
    });
  }
  save() {
    const obj: object = Object.assign(this.templateForm.name, this.templateForm.address,
      this.templateForm.contact, this.templateForm.id, this.templateForm.password, this.templateForm.reporting,
      this.templateForm.security);
    this.transport.progress = true;
    const formData: FormData = new FormData();
    if(this.nidFile)formData.append('nidFile', this.nidFile);
    if(this.idCardFile)formData.append('idCardFile', this.idCardFile);
    if(this.photoFile)formData.append('photoFile', this.photoFile);
    this.putData(formData, obj);
    const form: FormData = new FormData();
    formData.forEach((v,k) => {if(!_.isNull(v) && !_.isEqual(v,'null'))form.append(k,v);});
    const observable: Observable<unknown> = this.page == PageType.edit ? this.userService.update(this.id, form) : this.userService.save(form);
    observable.pipe(take(1)).subscribe((_data) => {
      if (_.isEqual(_.get(_data, 'body.status'), 'failed')) {
        this.transport.alert = {type: 'danger', message: _.get(_data,'body.message')};
      } else {
        this.transport.alert = {type: 'success', message: _.get(_data,'body.message')};
        setTimeout(() => this.transport.progress = false, 3000);
        this.router.navigate(['/user']);
      }
    });
  }
  cancelOpenClose(cancel: boolean) {
    this.cancel = false;
    if (cancel == true) this.router.navigate([this.path]);
  }
  findQuestions() {
    this.apicall.get('/security/questions').subscribe({
      next: (result) => { this.questions = (result as Array<any>); }
    });
  }
  findCountries() {
    this.apicall.get('/country/list').subscribe({
      next: (result) => { 
        this.countries = (result as Array<any>);
        this.provinces = _.get(_.find(this.countries,{name: this.templateForm.address.country}),'provinces',[]);
        this.districts = _.get(_.find(this.provinces,{name: this.templateForm.address.province}),'districts',[]);
        this.communes = _.get(_.find(this.districts,{name: this.templateForm.address.district}),'communes',[]);
        this.villages = _.get(_.find(this.communes,{name: this.templateForm.address.commune}),'villages',[]);
      }
    });
  }
  onCountryChange($event: any){
    this.provinces = _.get(_.find(this.countries,{name: $event.target.value}),'provinces',[]);
    console.log('country change proviences: ', $event.target.value);
  }
  onProvinceChange($event: any){
    this.districts = _.get(_.find(this.provinces,{name: $event.target.value}),'districts',[]);
    console.log('provience change districts: ', $event.target.value);
  }
  onDistrictChange($event: any){
    this.communes = _.get(_.find(this.districts,{name: $event.target.value}),'communes',[]);
    console.log('district change communes: ', $event.target.value);
  }
  onCommuneChange($event: any) {
    this.villages = _.get(_.find(this.communes,{name:$event.target.value}),'villages',[]);
  }
  onVillageChange($event: any){
    console.log('village: ', $event);
  }
}