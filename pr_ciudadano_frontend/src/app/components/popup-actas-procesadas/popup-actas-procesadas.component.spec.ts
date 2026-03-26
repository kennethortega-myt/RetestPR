import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupActasProcesadasComponent } from './popup-actas-procesadas.component';

describe('PopupActasProcesadasComponent', () => {
  let component: PopupActasProcesadasComponent;
  let fixture: ComponentFixture<PopupActasProcesadasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PopupActasProcesadasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupActasProcesadasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
