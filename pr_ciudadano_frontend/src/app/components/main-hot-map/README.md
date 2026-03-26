## Documentación para la implementación del mapa de calor

### Detalle de implementación del componente Mapa dentro de tu componente

```Typescript
export class TU_COMPONENTE {

  /**
   * Esto es obligatorio para poder cargar el mapa cuando lo necesites
   */
  @ViewChild(MainHotMapComponent) mainHotMapComponent: MainHotMapComponent;

  /**
   * Esta es una manera de hacer la carga inicial del mapa
   */
  ngAfterViewInit(): void {
    if (this.mainHotMapComponent) {
      this.mainHotMapComponent.loadInitialUbigeoPeru();
    }
  }

  /**
   * @important Este método no es obligatorio, solo se implementa si lo configuras en el html
   * Si necesitas que se ejecute algo cuando se da click al icono "Casita" o "Mundo" aquí debes poner tu lógica.
   * Internamente se hace el cambio de mapa automáticamente.
   */
  public changeRegionFromMap($event: RegionValue) {
    // TU CÓDIGO
  }

  /**
   * @important Este método no es obligatorio, solo se implementa si lo configuras en el html
   * Si necesitas que se ejecute algo cuando se da click a un ubigeo específico aquí debes poner tu lógica.
   * Internamente se hace el cambio de mapa automáticamente.
   */
  public ubigeoParamsChangedFromMap($event: FilterByLocationParams) {
    const { departmentUbigeoId, provinceUbigeoId, districtUbigeoId } = $event;
    // TU CÓDIGO
  }
}
```

```html
<!-- El template de TU_COMPONENTE -->

<app-main-hot-map (regionChanged)="changeRegionFromMap($event)" (ubigeoParamsChanged)="ubigeoParamsChangedFromMap($event)"> </app-main-hot-map>
```
