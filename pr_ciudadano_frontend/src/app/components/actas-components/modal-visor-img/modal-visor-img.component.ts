import { DatePipe } from "@angular/common";
import { Component, ElementRef, Inject, OnInit, ViewChild } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { IModalVisorConfig } from "../modal-visor-pdf/modal-visor-pdf.interface";

let Tiff: any;

@Component({
  selector: "app-modal-visor-img",
  templateUrl: "./modal-visor-img.component.html",
  styleUrls: ["./modal-visor-img.component.scss"],
  standalone: false,
})
export class ModalVisorImgComponent implements OnInit {
  imgSrc: any;

  @ViewChild("divTiff") divTiff: ElementRef<HTMLElement>;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: IModalVisorConfig,
    public datepipe: DatePipe,
    public dialogRef: MatDialogRef<ModalVisorImgComponent>
  ) {}

  ngOnInit(): void {
    this.load(this.divTiff);
  }

  descargarImagen(): void {
    const imageBlog = this.data.file;
    const imageURL = URL.createObjectURL(imageBlog);
    const link = document.createElement("a");
    link.href = imageURL;
    link.download = `Res - ${this.data.numeroDeActa} - ${this.data.nombreDeActa}`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  cerrar(): void {
    this.dialogRef.close(true);
  }

  load(_: ElementRef<HTMLElement>): void {
    fetch(this.data.file)
      .then((res) => res.blob())
      .then((d) => {
        this.cargarTiff(d);
      });
  }

  cargarTiff(bufferArray: Blob) {
    let reader = new FileReader();

    reader.onload = (e) => {
      Tiff.initialize({ TOTAL_MEMORY: 1000000000 });
      const tiff = new Tiff({
        buffer: e.target.result,
      });
      const width = tiff.width();
      const height = tiff.height();
      const tiffCanvas = tiff.toCanvas();
      if (tiffCanvas) {
        tiffCanvas.setAttribute("style", "width:" + width * 0.3 + "px; height: " + height * 0.3 + "px");
      }
      this.divTiff.nativeElement.append(tiffCanvas);
    };
    reader.readAsArrayBuffer(bufferArray);
  }
}
