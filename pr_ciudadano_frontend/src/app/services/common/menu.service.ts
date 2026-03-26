import { Injectable } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { ListaMenu } from '../../helpers/constantes';
import { encryptStorageEleccion } from '../../settings/encrypt-storage.settings';
import { URL_PATHS_TO_REDIRECT, VALID_URLS_FOR_NAVIGATION } from '../../settings/app.routes.settings';
import { HeaderNagivatinReporter, ReporteWatcherService } from '../elecciones-generales/reporte-watcher.service';

@Injectable({
    providedIn: 'root'
})
export class MenuService {
    // Propiedades compartidas
    public elecciones: ListaMenu[];
    public eleccionSeleccionada: ListaMenu;
    public eleccionPadreSeleccionada: ListaMenu = {};
    public eleccionHijaSeleccionada: ListaMenu = {};
    public listaEleccionesMenu: ListaMenu[];

    // Data para los reportes, puede ser inicializada aquí si es constante
    private headerNavigationReport: HeaderNagivatinReporter[] = [
        { id: 1, reportType: 'resumen_general' },
        { id: 2, reportType: 'presidenciales' },
        { id: 3, reportType: 'diputados' },
        { id: 5, reportType: 'parlamento_andino' },
        { id: 6, reportType: 'participacion_ciudadana' },
        { id: 7, reportType: 'actas' },
    ];

    constructor(
        private sanitizer: DomSanitizer,
        private router: Router,
        private reporteWatcherService: ReporteWatcherService
    ) {
        // Inicialización del servicio al construirse
        this.initializeElections();
        this.setupRouterEvents();
    }

    /**
     * Inicializa las listas de elecciones y el estado del menú
     * basado en la ruta actual.
     */
    private initializeElections(): void {
        this.elecciones = this.getElecciones(); // Carga las elecciones desde storage
        const tmpListaElecciones = [...this.elecciones]; // Usa una copia para manipular
        const tmpListaPadres = this.getListaPadres(tmpListaElecciones);
        const rutaActual = this.router.url;

        this.deseleccionarPadres(tmpListaElecciones, tmpListaPadres);
        this.seleccionarElementos(tmpListaElecciones, tmpListaPadres, rutaActual);
        this.listaEleccionesMenu = tmpListaPadres;
    }

    // Métodos Utilitarios Comunes

    /**
     * Obtiene las elecciones del `encryptStorageEleccion`.
     * @returns Lista de objetos `ListaMenu`.
     */
    public getElecciones(): ListaMenu[] {
        const eleccionesString = encryptStorageEleccion.getItem('ELECCIONES');
        return eleccionesString ? JSON.parse(eleccionesString) : [];
    }

    /**
     * Filtra las elecciones para obtener solo los elementos padres (aquellos con `padre === 0`).
     * @param elecciones Lista completa de elecciones.
     * @returns Lista de elementos padres.
     */
    public getListaPadres(elecciones: ListaMenu[]): ListaMenu[] {
        return elecciones.filter(x => x.padre === 0);
    }

    /**
     * Deselecciona un elemento del menú.
     * @param element El elemento del menú a deseleccionar.
     */
    public deseleccionar(element: ListaMenu): void {
        if (element) {
            element.seleccionado = false;
        }
    }

    /**
     * Formatea un nombre añadiendo saltos de línea para palabras cortas.
     * @param nombre El nombre a formatear.
     * @returns HTML seguro con el nombre formateado.
     */
    public formatNombre(nombre: string): SafeHtml {
        const palabrasCortas = ['de', 'y', 'en', 'la', 'el', 'a', 'con', 'por', 'del', 'al'];
        const palabras = nombre.split(' ');

        const resultado: string[] = [];
        for (let i = 0; i < palabras.length; i++) {
            const palabra = palabras[i];
            if (i > 0 && palabrasCortas.includes(palabra.toLowerCase())) {
                resultado[resultado.length - 1] += ` ${palabra}`; // Une la palabra corta con la anterior
            } else {
                resultado.push(palabra); // Añade nueva palabra
            }
        }
        // Usamos <br/> para saltos de línea, como en el HeaderComponent
        const formattedText = resultado.join(' ');
        return this.sanitizer.bypassSecurityTrustHtml(formattedText);
    }

    // Lógica de Selección de Menú y Eventos del Router

    /**
     * Deselecciona todos los padres y sus hijos en una lista de elecciones.
     * @param elecciones Lista completa de elecciones.
     * @param padres Lista de elementos padres.
     */
    private deseleccionarPadres(elecciones: ListaMenu[], padres: ListaMenu[]): void {
        elecciones.forEach(element => {
            if (element.padre !== 0) return; // Solo procesa padres

            const padre = padres.find(y => y.id === element.id);
            if (!padre) return;

            this.deseleccionar(padre);
            if (element.hijos) { // Si tiene hijos
                padre.listaHijos = elecciones.filter(y => y.padre === element.id);
                padre.listaHijos.forEach((x: ListaMenu) => this.deseleccionar(x));
            }
        });
    }

    /**
     * Selecciona los elementos del menú (padre e hijo) que coinciden con la ruta actual.
     * @param elecciones Lista completa de elecciones.
     * @param padres Lista de elementos padres.
     * @param rutaActual La URL actual del router.
     */
    private seleccionarElementos(elecciones: ListaMenu[], padres: ListaMenu[], rutaActual: string): void {
        elecciones.forEach(element => {
            if (element.url !== rutaActual) return;

            if (element.padre === 0) {
                // Es un elemento padre
                const foundParent = padres.find(y => y.id === element.id);
                if (foundParent) {
                    foundParent.seleccionado = true;
                }
            } else {
                // Es un elemento hijo, busca su padre
                const padre = padres.find(y => y.id === element.padre);
                if (padre) {
                    padre.seleccionado = true;
                    const foundChild = padre.listaHijos?.find((y: ListaMenu) => y.id === element.id);
                    if (foundChild) {
                        foundChild.seleccionado = true;
                    }
                }
            }
        });
    }

    /**
     * Configura los eventos del router para actualizar la selección del menú
     * cuando la ruta cambia.
     */
    private setupRouterEvents(): void {
        this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe((event: NavigationEnd) => {
            const rutaEncontrada = this.elecciones.find((x) => x.url === event.url);

            // Determina la elección seleccionada, por defecto resumen si no se encuentra
            this.eleccionSeleccionada =
                rutaEncontrada ||
                this.elecciones.find((election) => election.url === URL_PATHS_TO_REDIRECT.resumen);

            if (this.eleccionSeleccionada) {
                if (this.eleccionSeleccionada.padre !== 0) {
                    // Si la elección seleccionada es un hijo, encuentra su padre
                    this.eleccionPadreSeleccionada = this.elecciones.find((x) => x.id === this.eleccionSeleccionada.padre);
                    this.updateMenuSeleccionado();
                } else {
                    // Si la elección seleccionada es un padre
                    this.updateMenuSinPadre();
                }
                this.saveSeleccionadas();
            }

            // Redirige a resumen si la ruta no existe y no es ya la ruta de resumen
            if (!rutaEncontrada && event.url !== URL_PATHS_TO_REDIRECT.resumen) {
                const rutaNavegable = VALID_URLS_FOR_NAVIGATION.find((x) => x === event.url);
                const rutaFinal = rutaNavegable ?? URL_PATHS_TO_REDIRECT.resumen;
                this.router.navigate([rutaFinal]);
            }
        });
    }

    /**
     * Actualiza el estado de selección del menú cuando se selecciona un hijo.
     */
    private updateMenuSeleccionado(): void {
        this.listaEleccionesMenu = this.listaEleccionesMenu.map((x) => {
            if (x.id === this.eleccionSeleccionada.padre) {
                x.seleccionado = true;
                x.listaHijos?.forEach((y) => {
                    y.seleccionado = y.id === this.eleccionSeleccionada.id;
                });
            } else {
                x.seleccionado = false;
                // Opcional: deseleccionar hijos de otros padres si la lógica lo requiere
                x.listaHijos?.forEach(y => y.seleccionado = false);
            }
            return x;
        });
    }

    /**
     * Actualiza el estado de selección del menú cuando se selecciona un padre.
     */
    private updateMenuSinPadre(): void {
        this.listaEleccionesMenu = this.listaEleccionesMenu.map((x) => {
            x.seleccionado = x.id === this.eleccionSeleccionada.id;
            // Opcional: deseleccionar hijos de todos los padres si la lógica lo requiere
            x.listaHijos?.forEach(y => y.seleccionado = false);
            return x;
        });
    }

    /**
     * Guarda la elección seleccionada en el almacenamiento local.
     */
    private saveSeleccionadas(): void {
        if (this.eleccionSeleccionada) {
            encryptStorageEleccion.setItem('ELECCION_SELECCIONADA', this.eleccionSeleccionada.id);
            encryptStorageEleccion.setItem('ID_ELECCION_SELECCIONADA', this.eleccionSeleccionada.idEleccion);
        }
    }

    /**
     * Determina si un elemento del menú debe tener la clase 'active'.
     * @param electionId El ID de la elección.
     * @returns 'active' si es la elección seleccionada, o una cadena vacía.
     */
    public activarItem(electionId: number): string {
        const { id = 0, padre = 0 } = this.eleccionSeleccionada ?? {};
        return (id === electionId || padre === electionId) ? 'active' : '';
    }

    /**
     * Activa la descarga del reporte para la elección actualmente seleccionada.
     */
    public descargarReporte(): void {
        if (this.eleccionSeleccionada && this.eleccionSeleccionada.id) {
            const selectedElectionId = this.headerNavigationReport.find((el) => el.id === this.eleccionSeleccionada.id);
            if (selectedElectionId) {
                this.reporteWatcherService.watcherSubject.next(selectedElectionId.reportType);
            }
        }
    }

    /**
     * Redirige a la URL de un padre seleccionado.
     * @param electionId El ID de la elección (padre).
     * @param eleccionPadreSeleccionada El objeto `ListaMenu` del padre seleccionado.
     */
    public redirectToPadre(electionId: number, eleccionPadreSeleccionada: ListaMenu): void {
        this.eleccionHijaSeleccionada = {}; // Asegura que no haya un hijo seleccionado
        this.eleccionPadreSeleccionada = eleccionPadreSeleccionada;
        this.redirectTo(electionId);
    }

    /**
     * Redirige a la URL de un hijo seleccionado.
     * @param electionId El ID de la elección (hijo).
     * @param eleccionHijaSeleccionada El objeto `ListaMenu` del hijo seleccionado.
     */
    public redirectToHija(electionId: number, eleccionHijaSeleccionada: ListaMenu): void {
        this.desactivarHijos(); // Desactiva la selección de otros hijos
        eleccionHijaSeleccionada.seleccionado = true; // Activa la selección del hijo actual
        this.redirectTo(electionId);
    }

    /**
     * Desactiva la propiedad `seleccionado` de todos los elementos hijos en el menú.
     */
    public desactivarHijos(): void {
        this.listaEleccionesMenu.forEach((x) => {
            if (x.hijos && x.listaHijos?.length > 0) {
                x.listaHijos.forEach((y) => (y.seleccionado = false));
            }
        });
    }

    /**
     * Realiza la navegación a la URL de la elección especificada y guarda su información.
     * @param electionId El ID de la elección a la que se desea redirigir.
     */
    public redirectTo(electionId: number): void {
        const currentElectionType = this.elecciones.find((election) => election.id === electionId);

        if (currentElectionType) {
            encryptStorageEleccion.setItem('ID_ELECCION_SELECCIONADA', currentElectionType.idEleccion);
            encryptStorageEleccion.setItem('ELECCION_SELECCIONADA', currentElectionType);
            localStorage.setItem('id', electionId.toString()); // Asegúrate de que esto sea necesario
            this.router.navigate([currentElectionType.url]);
        }
    }
}