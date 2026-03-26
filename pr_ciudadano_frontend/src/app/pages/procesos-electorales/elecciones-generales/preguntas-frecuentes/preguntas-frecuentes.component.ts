import { ChangeDetectionStrategy, Component, signal } from '@angular/core';

@Component({
  selector: 'app-preguntas-frecuentes',
  standalone: false,
  templateUrl: './preguntas-frecuentes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreguntasFrecuentesComponent {
  readonly panelOpenState = signal(false);
}
