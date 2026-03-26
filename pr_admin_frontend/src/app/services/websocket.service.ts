import { Injectable } from '@angular/core';
import { Client, IStompSocket } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import SockJS from 'sockjs-client';

import { environment } from '../../environments/environment';
import { WsProgress } from '../interfaces/ws-progress.interface';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  private client?: Client;
  private progressSubject = new BehaviorSubject<WsProgress | null>(null);

  readonly progress$: Observable<WsProgress | null> = this.progressSubject.asObservable();

  private readonly baseUrl = environment.apiUrl;
  private token?: string;

  constructor() {
    this.initToken();
    this.connect();
  }

  private initToken(): void {
    const rawToken = localStorage.getItem('token');
    this.token = rawToken?.replace(/['"]+/g, '');
  }

  private connect(): void {
    const socket = new SockJS(`${this.baseUrl}/ws`);

    this.client = new Client({
      webSocketFactory: () => socket as IStompSocket,
      connectHeaders: this.buildHeaders(),
      reconnectDelay: 5000, // ⭐ auto-reconnect
      onConnect: () => this.onConnected(),
      onStompError: (frame) => {
        console.error('STOMP error', frame);
      }
    });

    this.client.activate();
  }

  private onConnected(): void {
    this.client?.subscribe(
      '/topic/update-progress-with-details',
      (message) => this.handleMessage(message.body)
    );
  }

  private handleMessage(body: string): void {
    try {
      const progress: WsProgress = JSON.parse(body);
      this.progressSubject.next(progress);
    } catch (error) {
      console.error('Error parsing WS message', error);
    }
  }

  publish(destination: string, payload: unknown): void {
    if (!this.client?.connected) {
      console.warn('WebSocket no conectado');
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(payload),
      headers: this.buildHeaders(),
    });
  }

  disconnect(): void {
    this.client?.deactivate();
  }

  clearProgress(): void {
    this.progressSubject.next(null);
  }

  private buildHeaders(): Record<string, string> {
    return this.token
      ? { Authorization: `Bearer ${this.token}` }
      : {};
  }
}