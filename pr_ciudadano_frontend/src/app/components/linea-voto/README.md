## Here is the way to use this component

```html
<linea-voto
  *ngIf="emptyVotes && geographicalLocationNameItems.length != 0 && showSpecialCount"
  [title]="'VOTOS EN BLANCO'"
  [(votes)]="emptyVotes"
  [isTotal]="false"
  [(percentageOfEmptyAndNullVotes)]="getPercentageOfEmptyAndNullVotes(
    emptyVotes.totalVotosValidos
  )"
></linea-voto>
```
