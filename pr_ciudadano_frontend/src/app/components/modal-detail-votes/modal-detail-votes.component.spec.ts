import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalDetailVotesComponent } from './modal-detail-votes.component';

describe('ModalDetailVotesComponent', () => {
  let component: ModalDetailVotesComponent;
  let fixture: ComponentFixture<ModalDetailVotesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalDetailVotesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalDetailVotesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
