/* eslint-disable @typescript-eslint/no-explicit-any */
import {
  ChangeDetectorRef,
  Component,
  Inject,
  Input,
  OnInit,
  Renderer2,
} from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthService } from '../core/services/common/auth.service';
import { MenuTransportService } from '../core/services/common/menu.transport.service';
import { ApiUtilService } from '../core/services/common/api.util.service';
import * as _ from 'lodash';
import { JwtService } from '../core/services/common/jwt.service';
import { ConfigService } from 'ng-config-service';
import { DOCUMENT, Location } from '@angular/common';

@Component({
  selector: 'ceirpanel-ceir-header',
  template: `
    <clr-alert
      [clrAlertType]="'warning'"
      [clrAlertAppLevel]="true"
      class="bg-danger"
      *ngIf="passwordprompt"
    >
      <clr-alert-item>
        <span class="alert-text text-white text-center">{{'message.passwordExpired' | translate:paswordpromptdays}}</span>
        <div class="alert-actions text-center">
          <button class="btn alert-action" [routerLink]="['change-password']">
            Reset Password Now
          </button>
        </div>
      </clr-alert-item>
    </clr-alert>
    <clr-progress-bar
      clrValue="0"
      clrMax="100"
      clrLoop="true"
      clrFlashDanger="true"
      clrDanger="true"
      *ngIf="progress"
    ></clr-progress-bar>
    <header class="header-3 bg8">
      <div class="branding" *ngIf="header === 'yes'">
        <a routerlink="/" class="logo-and-title" href="/">
          <span class="clr-icon clarity-logo">
            <img
              [src]="logo"
              alt="Clarity Design System"
              class="rounded-circle img-fluid"
            />
          </span>
          <span
            class="text-center text-white fw-bold mt-3"
            [innerHtml]="'title' | translate"
          ></span>
        </a>
      </div>
      <div
        class="username"
        style="margin-left:50px;"
        *ngIf="authService.isLogin()"
      >
        <a
          href="javascript://"
          class="nav-link nav-text text-white border border-0"
          >Welcome EIRS ({{ jwtService.getUsername() }})</a
        >
      </div>
      <div class="header-actions" *ngIf="header === 'no'">
        <span class="text-center text-white fw-bold mt-4">{{headertitle}}</span>
      </div>
      <div class="header-actions mx-2">
        <div *ngIf="header === 'yes' && authService.isLogin(); then head"></div>
        <ng-template #head>
          <a
            download="b82d8486-a931-421c-b594-f4d3129746e5.pdf"
            target="_blank"
            [href]="usermanulaurl"
            class="nav-link nav-text text-white"
          >
            <img
              src="assets/images/Download.svg"
              alt=""
              style="width:26px;height:26px;"
            />
          </a>
        </ng-template>
        <clr-dropdown *ngIf="!authService.isLogin()">
          <a href="javascript://" class="nav-link nav-icon-text mb-2" clrDropdownTrigger aria-label="toggle settings menu" style="opacity: none !important;">
            <span class="nav-text text-white">
              <img [src]="language.image" class="rounded-circle img-fluid" style="width24px;height:24px;"/>
              {{language.name}}
            </span>
          </a>
          <clr-dropdown-menu *clrIfOpen clrPosition="bottom-right" class="text-white m-0 p-0">
            <a [routerLink]="[currenturl]" [queryParams]="{lang:lang.code,type:this.type,header:this.header}" clrDropdownItem *ngFor="let lang of languages">
              <img [src]="lang.image" class="rounded-circle img-fluid" style="width24px;height:24px;"/>
              {{lang.name}}
            </a>
          </clr-dropdown-menu>
        </clr-dropdown>
        <clr-dropdown *ngIf="authService.isLogin()">
          <a
            href="javascript://"
            class="nav-link nav-icon-text mb-2"
            clrDropdownTrigger
            aria-label="toggle settings menu"
            style="opacity: none !important;"
          >
            <span class="nav-text text-white">
              <i class="bi bi-person-circle"></i>
            </span>
          </a>
          <clr-dropdown-menu
            *clrIfOpen
            clrPosition="bottom-right"
            class="text-white m-0 p-0"
          >
            <div class="dropdown-header m-0 p-0">
              <ul
                class="list-group m-0 p-0 border-0"
                style="border-radius: 0px;"
              >
                <li class="list-group-item"></li>
              </ul>
            </div>
            <a clrDropdownItem [routerLink]="['user','edit',jwtService.getUserId()]" [queryParams]="{url,lang:lang,type:this.type,header:this.header}"> {{ 'profile.editProfile' | translate }} </a>
            <a clrDropdownItem [routerLink]="['change-password']">
              {{ 'profile.changePassword' | translate }}
            </a>
            <a clrDropdownItem (click)="logout()">
              {{ 'profile.logout' | translate }}
            </a>
          </clr-dropdown-menu>
        </clr-dropdown>
      </div>
    </header>
  `,
  styles: [
    `
      header .settings > .dropdown > .dropdown-toggle,
      header .header-actions > .dropdown > .dropdown-toggle,
      .header .settings > .dropdown > .dropdown-toggle,
      .header .header-actions > .dropdown > .dropdown-toggle {
        opacity: none !important;
        opacity: var(--clr-header-nav-opacity, none);
      }
      .progress,
      .progress-static {
        background-color: transparent;
        border-radius: 0;
        font-size: inherit;
        height: 1em;
        margin: 0;
        max-height: 0.1rem !important;
        min-height: 0.2rem;
        overflow: hidden;
        display: block;
        width: 100%;
      }
    `,
  ],
})
export class CeirHeaderComponent implements OnInit{
  @Input() title = 'ceirpanel';
  siteLanguage = 'English';
  localeCode = 'en';
  languageList = [
    { code: 'en', label: 'English' },
    { code: 'km', label: 'Deutsch' },
  ];
  titleParam = { company: '' };
  isAuthenticated = false;
  progress = false;
  passwordprompt = false;
  paswordpromptdays:any = {};
  usermanulaurl = '/resource/usermanual';
  type = 0;
  logo = 'assets/images/logo.png';
  header = 'yes';
  lang = 'en';
  url = '/user';
  headertitle = this.title
  private style?: HTMLLinkElement;
  languages:Array<any> = [];
  language = {name:'',image:'',code:''};
  currenturl = '';
  constructor(
    private translate: TranslateService,
    public authService: AuthService,
    public router: Router,
    private transport: MenuTransportService,
    private apiutil: ApiUtilService,
    public jwtService: JwtService,
    public config: ConfigService,
    private cdRef: ChangeDetectorRef,
    private route: ActivatedRoute,
    private readonly location: Location,
    @Inject(DOCUMENT) private document: Document,
    private renderer2: Renderer2
  ) {
    router.events.subscribe(() => this.url = router.url);
    this.url = this.router.url;
    this.logo = this.config.get('logo') || 'assets/images/logo.png';
    this.usermanulaurl = `${this.config.get('api')}${this.usermanulaurl}`;
    this.apiutil.get('/user/isAlertForPasswordExpire').subscribe({
      next: (data) => {
        console.log('data: ', data);
        this.passwordprompt = _.get(data as any, 'allow');
        this.paswordpromptdays = {paswordpromptdays:_.get(data as any, 'days')};
      },
    });
    this.authService.isAuthenticated.subscribe(
      (isAuthenticated) => (this.isAuthenticated = isAuthenticated)
    );
    this.transport._progress.subscribe((progress) => {
      this.progress = progress;
    });
    this.route.queryParams.subscribe((queryParams) => {
      this.lang = queryParams['lang'] || 'en';
      this.type = queryParams['type'] || 0;
      this.header = queryParams['header'] || 'yes';
      this.changeSiteLanguage(this.lang);
      this.removestyle();
      this.addstyle();
      this.setTitle();
    });
  }
  ngOnInit(): void {
    
    this.router.events.subscribe((val) => {
      if(val instanceof NavigationEnd) {
        this.setTitle();
      }
    });
    this.languages = this.config.get('languages') || [];
    console.log('languages: ', this.languages);
    this.updateLanguage();
    this.currenturl = this.router.url.split('?')[0];
    console.log('current url: ',this.router.url.split('?')[0]); 
  }
  setTitle() {
    if (_.isEqual(this.header, 'no')) {
      const key = _.some(['register-ticket-success'], (el) =>
        _.includes(this.router.url, el)
      )
        ? 'ticket.pageTitle.success'
        : _.some(['view-ticket'], (el) => _.includes(this.router.url, el))
        ? 'ticket.viewTicket.title'
        : _.some(['forgot-ticket'], (el) => _.includes(this.router.url, el))
        ? 'ticket.forgotTicket.title'
        : _.some(['register-ticket'], (el) =>
            _.includes(this.router.url, el)
          )
        ? 'ticket.pageTitle.add' : 'ticket.pageTitle.add';
      console.log('key: ',key);
      this.translate.get(key).subscribe((t) => {
        this.headertitle = t;
        console.log('title:- ', t);
      });
    }
  }
  changeSiteLanguage(localeCode: string): void {
    console.log('locale: ', localeCode);
    this.localeCode = localeCode;
    const selectedLanguage = this.languageList
      .find((language) => language.code === localeCode)
      ?.label.toString();
    if (selectedLanguage) {
      this.siteLanguage = selectedLanguage;
      this.translate.use(localeCode);
    }
    const currentLanguage = this.translate.currentLang;
    console.log('currentLanguage', currentLanguage);
    this.updateLanguage();
    
  }
  updateLanguage(){
    this.languages.forEach(l => {
      if (_.isEqual(_.get(l, 'code'), this.lang)) {
        this.language = l;
      }
    });
  }
  logout() {
    this.apiutil.get('/api/auth/logout').subscribe({
      complete: () => {
        this.router.navigate(['/']);
        this.authService.purgeAuth('logout');
        localStorage.removeItem('permissions');
      },
    });
  }
  download() {
    this.apiutil.get('/resource/usermanual').subscribe({
      next: (file: any) => {
        const a = document.createElement('a');
        a.setAttribute('type', 'hidden');
        a.href = URL.createObjectURL(file.data);
        a.download = 'usermanual' + '.xlsx';
        a.click();
        a.remove();
      },
    });
  }
  addstyle() {
    const cssPath = `assets/css/${this.lang}.css`;
    this.style = this.renderer2.createElement('link') as HTMLLinkElement;
    this.renderer2.appendChild(this.document.head, this.style);
    this.renderer2.setProperty(this.style, 'rel', 'stylesheet');
    this.renderer2.setProperty(this.style, 'href', cssPath);
  }
  removestyle() {
    if (_.isEmpty(this.style) == false)
      this.renderer2.removeChild(this.document.head, this.style);
  }
}
