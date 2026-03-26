import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupCausalesObservacionComponent } from './popup-causales-observacion.component';

describe('PopupCausalesObservacionComponent', () => {
  let component: PopupCausalesObservacionComponent;
  let fixture: ComponentFixture<PopupCausalesObservacionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PopupCausalesObservacionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupCausalesObservacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
