import { FormControl } from '@angular/forms';

export interface ActasForm {
  region: FormControl<string | number>;
  eleccion: FormControl<string | number>;
  departamento: FormControl<string | number>;
  provincia: FormControl<string | number>;
  distrito: FormControl<string | number>;
  cent_educativo: FormControl<string | number>;
}
