import * as am5 from "@amcharts/amcharts5";

export function destroyChart(nombreDiv: string) {
  am5.array.each(am5.registry.rootElements, function (root) {
    if (root != undefined) {
      if (root.dom != undefined) {
        if (root.dom.id == nombreDiv) {
          root.dispose();
        }
      }
    }
  });
}
