import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-c-table',
  templateUrl: './c-table.component.html',
  styleUrl: './c-table.component.scss',
  standalone: false
})
export class CTableComponent {
  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol', 'btn'];
  dataSource = new MatTableDataSource<PeriodicElement>(ELEMENT_DATA);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
}

export interface PeriodicElement {
  name: string;
  position: number;
  weight: number;
  symbol: string;
  btn: string;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {position: 1, name: 'Hydrogen', weight: 1.0079, symbol: 'H', btn: ''},
  {position: 2, name: 'Helium', weight: 4.0026, symbol: 'He', btn: ''},
  {position: 3, name: 'Lithium', weight: 6.941, symbol: 'Li', btn: ''},
  {position: 4, name: 'Beryllium', weight: 9.0122, symbol: 'Be', btn: ''},
  {position: 5, name: 'Boron', weight: 10.811, symbol: 'B', btn: ''},
  {position: 6, name: 'Carbon', weight: 12.0107, symbol: 'C', btn: ''},
  {position: 7, name: 'Nitrogen', weight: 14.0067, symbol: 'N', btn: ''},
  {position: 8, name: 'Oxygen', weight: 15.9994, symbol: 'O', btn: ''},
  {position: 9, name: 'Fluorine', weight: 18.9984, symbol: 'F', btn: ''},
  {position: 10, name: 'Neon', weight: 20.1797, symbol: 'Ne', btn: ''},
  {position: 11, name: 'Sodium', weight: 22.9897, symbol: 'Na', btn: ''},
  {position: 12, name: 'Magnesium', weight: 24.305, symbol: 'Mg', btn: ''},
  {position: 13, name: 'Aluminum', weight: 26.9815, symbol: 'Al', btn: ''},
  {position: 14, name: 'Silicon', weight: 28.0855, symbol: 'Si', btn: ''},
  {position: 15, name: 'Phosphorus', weight: 30.9738, symbol: 'P', btn: ''},
  {position: 16, name: 'Sulfur', weight: 32.065, symbol: 'S', btn: ''},
  {position: 17, name: 'Chlorine', weight: 35.453, symbol: 'Cl', btn: ''},
  {position: 18, name: 'Argon', weight: 39.948, symbol: 'Ar', btn: ''},
  {position: 19, name: 'Potassium', weight: 39.0983, symbol: 'K', btn: ''},
  {position: 20, name: 'Calcium', weight: 40.078, symbol: 'Ca', btn: ''},
];