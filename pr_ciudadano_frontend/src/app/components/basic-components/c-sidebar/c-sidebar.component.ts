import { Component } from '@angular/core';

@Component({
  selector: 'app-c-sidebar',
  templateUrl: './c-sidebar.component.html',
  standalone: false
})
export class CSidebarComponent {
  public Inicio = 'c-sidebar.Inicio'
  public Servicios = 'c-sidebar.Servicios'
  public Acercade = 'c-sidebar.Acercade'
  public Contacto = 'c-sidebar.Contacto'
  public Actualizaciondatos = 'c-sidebar.Actualizacióndatos'
  public Perfilescompatibles = 'c-sidebar.Perfilescompatibles'
  public preferente = 'c-sidebar.preferente'
}
