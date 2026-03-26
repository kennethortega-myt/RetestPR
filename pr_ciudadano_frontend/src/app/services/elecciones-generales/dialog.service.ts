import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { catchError, Observable, of, switchMap, tap } from 'rxjs';
import { ComponentType, Overlay, OverlayContainer } from '@angular/cdk/overlay';
import { DialogComponent } from '../../components/dialog/dialog.component';
import { IDialogConfigData, DIALOG_CONFIGS, DialogKey, TYPE_ICON_MAP, DEFAULT_DIALOG_SIZE } from '../../components/dialog/dialog.constants';

@Injectable({
    providedIn: 'root'
})
export class DialogService {
    
    // private dialogRef?: MatDialogRef<DialogComponent>;
    private dialogRefs: MatDialogRef<any>[] = [];

    constructor(
        private dialog: MatDialog,
        private overlay: Overlay,
        private overlayContainer: OverlayContainer
    ) {}

    private loadImage(imageUrl: string): Observable<string> {
        if (!imageUrl) {
            return of('');
        }

        return new Observable<string>(observer => {
            const img = new Image();
            img.src = imageUrl;

            if (img.complete) {
                observer.next(imageUrl);
                observer.complete();
                return;
            }
            
            img.onload = () => {
                observer.next(imageUrl);
                observer.complete();
            };

            img.onerror = (error) => {
                console.error(`Error al precargar la imagen: ${imageUrl}`, error);
                observer.next('');
                observer.complete();
            };

        }).pipe(
            catchError(err => {
                console.error(`Error capturado en el Observable de carga de imagen: ${imageUrl}`, err);
                return of('');
            })
        );
    }
    private _openDialog(dialogDataConfig: IDialogConfigData, overrideConfig?: MatDialogConfig & { useRootContainer?: boolean }): Observable<boolean> {
        const shouldPreloadImage = !dialogDataConfig.contentTemplate;
        let imagePreload$: Observable<string> = of('');

        if (shouldPreloadImage) {
            let iconPathToPreload: string | undefined;

            if (dialogDataConfig.iconPath) {
                iconPathToPreload = dialogDataConfig.iconPath;
            } else if (
                dialogDataConfig.key &&
                Object.prototype.hasOwnProperty.call(DIALOG_CONFIGS, dialogDataConfig.key)
            ) {
                const predefinedConfig = DIALOG_CONFIGS[dialogDataConfig.key as DialogKey];
                iconPathToPreload = predefinedConfig?.iconPath || TYPE_ICON_MAP[predefinedConfig?.type!];
            } else if (dialogDataConfig.type !== undefined) {
                iconPathToPreload = TYPE_ICON_MAP[dialogDataConfig.type];
            }

            if (iconPathToPreload) {
                imagePreload$ = this.loadImage(iconPathToPreload);
            }
        }

        const dialogConfig: MatDialogConfig = {
            ...DEFAULT_DIALOG_SIZE,
            disableClose: true,
            data: dialogDataConfig,
            ...overrideConfig
        };
        
        if (overrideConfig?.useRootContainer) {
            const rootOverlayContainer = this.overlayContainer.getContainerElement();

            dialogConfig.viewContainerRef = null!;
            (dialogConfig as any).scrollStrategy = this.overlay.scrollStrategies.block(); // evita scroll del fondo
            (dialogConfig as any).positionStrategy = this.overlay
                .position()
                .global()
                .centerHorizontally()
                .centerVertically();

            // 👇 fuerza que se use el overlay global
            setTimeout(() => {
                const overlays = document.querySelectorAll('.cdk-overlay-container');
                overlays.forEach(container => {
                if (container !== rootOverlayContainer) {
                    rootOverlayContainer.append(...container.childNodes);
                }
                });
            });
        }


        const openAndReturn$ = (): Observable<boolean> => {
            const dialogRef = this.dialog.open(DialogComponent, dialogConfig);
            this.dialogRefs.push(dialogRef);

            return dialogRef.afterClosed().pipe(
                // Limpiamos la lista cuando el diálogo se cierra
                tap(() => {
                this.dialogRefs = this.dialogRefs.filter(ref => ref !== dialogRef);
                })
            );
        };

        return shouldPreloadImage ? imagePreload$.pipe(switchMap(openAndReturn$)) : openAndReturn$();
    }

    open(dialogDataConfig: IDialogConfigData, config?: MatDialogConfig): Observable<boolean> {
        return this._openDialog(dialogDataConfig, config);
    }


    show(keyOrData: string | IDialogConfigData, message?: string, title?: string, config?: MatDialogConfig): Observable<boolean> {
        let data: IDialogConfigData;

        if (typeof keyOrData === 'string') {
            data = { key: keyOrData };
        } else {
            data = { ...keyOrData };
        }

        if (message !== undefined) {
            data.message = message;
        }
        if (title !== undefined) {
            data.title = title;
        }

        return this._openDialog(data, config);
    }

    display(keyOrData: string | IDialogConfigData, message?: string, title?: string, config?: MatDialogConfig): void {
        this.show(keyOrData, message, title, config).subscribe({
            error: (err) => {
                console.error('Error al mostrar el diálogo:', err);
            }
        });
    }

    mostrarMensajeError(message: string, title: string = 'Error', config?: MatDialogConfig): void {
        this.display('error', message, title, config)
    }

    mostrarMensajeExito(message: string, title: string = 'Éxito', config?: MatDialogConfig): void {
        this.display('success', message, title, config)
    }

    mostrarMensajeInformacion(message: string, title: string = 'Información', config?: MatDialogConfig): void {
        this.display('info', message, title, config)
    }

    mostrarMensajeAdvertencia(message: string, title: string = 'Advertencia', config?: MatDialogConfig): void {
        this.display('warning', message, title, config)
    }

    mostrarMensajeConfirmacion(message: string, title: string = 'Confirmación', config?: MatDialogConfig): Observable<boolean> {
        return this.show('confirm', message, title, config);
    }

    mostrarMensaje(keyOrData: string | IDialogConfigData, message?: string, title?: string, config?: MatDialogConfig): void {
        this.show(keyOrData, message, title, config).subscribe({
            next: (result) => {                
            },
            error: (err) => {
                console.error('Error en displayMessage:', err);
            }
        });
    }

    verDialogPersonalizado(keyOrData: string | IDialogConfigData, message?: string, title?: string, config?: MatDialogConfig): void {
        this.show(keyOrData, message, title, config).subscribe({
            next: (result) => {                
            },
            error: (err) => {
                console.error('Error en displayMessage:', err);
            }
        });
    }

    cerrarDialog(): void {
        this.cerrarUltimoDialog();
    }

    cerrarUltimoDialog(): void {
        const lastRef = this.dialogRefs.pop();
        lastRef?.close();
    }

    cerrarTodos(): void {
        this.dialogRefs.forEach(ref => ref.close());
        this.dialogRefs = [];
    }

    openComponent(component: ComponentType<any>, config?: MatDialogConfig): Observable<any> {
        const dialogRef = this.dialog.open(component, {
            disableClose: config?.disableClose ?? true,
            ...config
        });
        this.dialogRefs.push(dialogRef);
        return dialogRef.afterClosed().pipe(
            tap(() => {
                this.dialogRefs = this.dialogRefs.filter(ref => ref !== dialogRef);
            })
        );
    }

    openComponentData<T, D = any, R = any>(
        component: ComponentType<T>,
        config?: MatDialogConfig<D>
    ): Observable<R | undefined> {
        const dialogRef = this.dialog.open<T, D, R>(component, {
            disableClose: true,
            ...config
        });

        this.dialogRefs.push(dialogRef);

        return dialogRef.afterClosed().pipe(
            tap(() => {
            this.dialogRefs = this.dialogRefs.filter(ref => ref !== dialogRef);
            })
        );
    }


    cerrarPorKey(key: string): void {
        const ref = this.dialogRefs.find(ref => ref.componentInstance?.data?.key === key);
        ref?.close();
    }

    cerrarDialogDeTipo(component: ComponentType<any>): void {
        const ref = this.dialogRefs.find(ref => ref.componentInstance instanceof component);
        ref?.close();
    }

    isDialogOpen(): boolean {
        return this.dialogRefs.length > 0;
    }

    mostrarMensajeExitoConCallback(
    message: string, 
    onConfirm?: () => void, 
    title: string = 'Éxito', 
    config?: MatDialogConfig
    ): void {
        const dialogConfig: IDialogConfigData = {
            key: 'success',
            message: message,
            title: title,
            onConfirm: onConfirm
        };
        this.display(dialogConfig, undefined, undefined, config);
    }
}