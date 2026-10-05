import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, map, of } from 'rxjs';
import { HelloService } from './api/hello.service';

@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  private readonly hello = inject(HelloService);

  protected readonly message = toSignal(
    this.hello.getHello().pipe(
      map((res) => res.message),
      catchError(() => of('Backend unreachable')),
    ),
    { initialValue: 'Loading...' },
  );
}
