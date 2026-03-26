import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisclaimerBrowserComponent } from './disclaimer-browser.component';

describe('DisclaimerBrowserComponent', () => {
  let component: DisclaimerBrowserComponent;
  let fixture: ComponentFixture<DisclaimerBrowserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisclaimerBrowserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DisclaimerBrowserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
