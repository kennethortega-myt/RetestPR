import { Component } from '@angular/core';
import { Router, RouterLink, RouterOutlet, RouterModule, NavigationEnd } from '@angular/router';
import { DEFAULT_INTERRUPTSOURCES, Idle } from '@ng-idle/core';
import { ComponentsModule } from '../../components/components.module';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { filter, Subscription } from 'rxjs';
import { AuthComponent } from '../../helpers/auth-component';
import { Constantes } from '../../helpers/constantes';
import { UsuarioService } from '../../services/usuario.service';
import { RefreshTokenResponse } from '../../interfaces/RefreshTokenResponse';
import { openDialogMensaje } from '../../utils/funciones';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  imports: [ComponentsModule, RouterOutlet, RouterLink, RouterModule, NgIf, NgFor, NgClass],
})
export class MainComponent extends AuthComponent {
  isPrincipal: boolean = false;
  accesos: any;
  time: number = 0;
  interval: any;
  timedOut = false;
  private idleSubscription!: Subscription;
  private routerSubscription!: Subscription;

  constructor(
    public router: Router,
    public idle: Idle,
    public dialog: MatDialog,
    private readonly usuarioService: UsuarioService ) {
    super();
    idle.setIdle(Constantes.INACTIVIDAD);
    idle.setTimeout(Constantes.DURATION);
    idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);
    idle.watch();

    idle.onIdleEnd.subscribe(() => {
      this.reset();
    });

    this.idleSubscription = idle.onTimeout.subscribe(() => {
      this.timedOut = true;
      this.usuarioService.cerrarSesion();
    });

    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isPrincipal = event.urlAfterRedirects === '/main/principal';
      });
  }
  
  ngOnInit(): void {
    this.accesos = this.getAccesos();
    this.time = this.usuarioService.getDuration();  
    this.startTimer();
  }

  reset() {
    this.idle.watch();
    this.timedOut = false;
  }
  
  startTimer() {
    if (this.interval) {
      clearInterval(this.interval);
    }
    this.interval = setInterval(() => {
      if (!localStorage.getItem(Constantes.TOKEN) || !localStorage.getItem(Constantes.REFRESH_TOKEN)) {        
        clearInterval(this.interval);
        this.idle.stop();
        this.router.navigate(['/inicio']);
      } else {
        this.refrescarToken();
      }
    }, this.time * Constantes.START_TIME);
  }

  refrescarToken() {
    let refreshToken = localStorage.getItem(Constantes.REFRESH_TOKEN)!;
    refreshToken = refreshToken.replace(/^"|"$/g, '');
    this.usuarioService.refreshToken(refreshToken).subscribe({
      next: (response: RefreshTokenResponse) => {          
        localStorage.setItem(Constantes.TOKEN, response.token);
        localStorage.setItem(Constantes.REFRESH_TOKEN, response.refreshToken);
      },
      error: () => {
          openDialogMensaje(this.dialog,'Error al renovar la sesión', 2).afterClosed().subscribe(() => {
            this.router.navigate(['/inicio']);
          });
        }
      });
  }
  
  isActive(url: string): boolean {
    return this.router.url === `/${url}`;
  }
  
  openMenuItemProc() {
    this.router.navigateByUrl('/main/proceso');
  }
  openMenuItemConfig() {
    this.router.navigateByUrl('/main/configuraciones/paso1');
  }
  openMenuItemVerifActas() {
    this.router.navigateByUrl('/main/verificacion-actas');
  }
  openMenuItemMonitor() {
    this.router.navigateByUrl('/main/monitoreo');
  }
  openMenuItemControl() {
    this.router.navigateByUrl('/main/control');
  }
  openMenuItemPrincipal() {
    this.router.navigateByUrl('/main/instalacion');
  }

  openOptMenu(item: number) {
    let destino: string = '';
    switch (item) {
      case 1:
        destino = '/main/instalacion';
        break;
      case 2:
        destino = '/main/proceso';
        break;
      case 3:
        destino = '/main/configuraciones/paso1';
        break;
      case 4:
        destino = '/main/verificacion-actas';
        break;
      case 5:
        destino = '/main/monitoreo';
        break;
      case 7:
        destino = '/main/control';
        break;
      case 8:
        destino = '/main/instalacion';
        break;
      case 9:
        destino = '/main/principal';
        break;
      case 10:
        destino = '/main/principal';
        break;
      case 11:
        destino = '/main/principal';
        break;
      case 12:
        destino = '/main/principal';
        break;
      case 13:
        destino = '/main/principal';
        break;
      case 14:
        destino = '/main/principal';
        break;
      case 15:
        destino = '/main/principal';
        break;
      case 16:
        destino = '/main/principal';
        break;
      case 17:
        destino = '/main/principal';
        break;
      case 18:
        destino = '/main/principal';
        break;
      case 19:
        destino = '/main/semaforo';
        break;
    }
    localStorage.setItem('itemMenu', item.toString());
    this.router.navigateByUrl(destino);
  }

  ngOnDestroy() {
    if (this.interval) {
      clearInterval(this.interval);
      this.interval = null;
    }

    this.idle.stop();

    if (this.idleSubscription) {
      this.idleSubscription.unsubscribe();
    }
    
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
  }
  
  activarItem(item: number) {
    let activo: string;
    activo = localStorage.getItem('itemMenu') + '';
    if (parseInt(activo) == item) {
      return 'active';
    } else {
      return '';
    }
  }
}
