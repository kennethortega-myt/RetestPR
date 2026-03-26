import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DrawerService {

  private readonly drawerState = new BehaviorSubject<boolean>(false);
  drawerState$ = this.drawerState.asObservable();

  toggleDrawer() {
    this.drawerState.next(!this.drawerState.value);
  }

  closeDrawer() {
    this.drawerState.next(false);
  }
}
