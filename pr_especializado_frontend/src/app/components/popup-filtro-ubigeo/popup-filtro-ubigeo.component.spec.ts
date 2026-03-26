import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupFiltroUbigeoComponent } from './popup-filtro-ubigeo.component';

describe('PopupFiltroUbigeoComponent', () => {
  let component: PopupFiltroUbigeoComponent;
  let fixture: ComponentFixture<PopupFiltroUbigeoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PopupFiltroUbigeoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupFiltroUbigeoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
