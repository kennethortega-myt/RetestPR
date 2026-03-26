import { ChangeDetectorRef, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Observable } from 'rxjs';
import { ComponentsModule } from "./components/components.module";
import { CLoaderComponent } from "./shared/c-loader/c-loader.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ComponentsModule, CLoaderComponent],
  templateUrl: './app.component.html'
})
export class AppComponent {
  loading$!: Observable<boolean>;

  constructor( private readonly cdr: ChangeDetectorRef) {
  }  

  ngAfterViewInit() {
    this.cdr.detectChanges();
  }
}
