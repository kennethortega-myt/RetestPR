import { Component, Input, OnInit } from "@angular/core";
import { getPersonOnpeMessages, PersonOnpeMessageType } from "./person-onpe.config";
import { getRandomImage } from "../../helpers/random-image";
import { TranslateService } from "@ngx-translate/core";

@Component({
  selector: "app-person-opne-no-information",
  templateUrl: "./person-opne-no-information.component.html",
  standalone: false,
})
export class PersonOpneNoInformationComponent implements OnInit {
  public randomImageUrl: string;
  @Input() personOnpeMessageType: PersonOnpeMessageType = "there_is_no_information";

  public message = "";

  constructor(private translate: TranslateService) {}

  ngOnInit(): void {
    const PERSON_ONPE_MESSAGES = getPersonOnpeMessages(this.translate);
    this.message = PERSON_ONPE_MESSAGES[this.personOnpeMessageType];
    this.randomImageUrl = getRandomImage();
  }
}
