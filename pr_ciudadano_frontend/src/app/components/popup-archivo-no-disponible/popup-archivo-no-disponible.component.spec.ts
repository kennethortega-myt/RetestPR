import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupArchivoNoDisponibleComponent } from './popup-archivo-no-disponible.component';

describe('PopupArchivoNoDisponibleComponent', () => {
  let component: PopupArchivoNoDisponibleComponent;
  let fixture: ComponentFixture<PopupArchivoNoDisponibleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PopupArchivoNoDisponibleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupArchivoNoDisponibleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
