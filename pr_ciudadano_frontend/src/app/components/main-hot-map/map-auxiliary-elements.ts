import * as am5 from '@amcharts/amcharts5';
import * as am5map from '@amcharts/amcharts5/map';
import { IHotMapUbigeoItem } from '../../interfaces/hot-map.interfaces';
import { MAP_ICONS } from './maps.constants';
import { cargarBotones } from '../../helpers/map.cargar-botones';
import { LEGEND_HEIGHT } from '../../settings/map.settings';
import { getDecimalPipe } from '../../helpers/numbers-helper.common';
import { getTranslatedString } from '../../helpers/basic-helpers/string.helper';

const TIME_TO_DELAY_CUSTOM_ELEMENTS = 500; // in miliseconds
const BUTTON_SCROLL_LOCK_DURATION_MS = 700;
let buttonScrollLockRelease: (() => void) | null = null;
let buttonScrollLockTimer: ReturnType<typeof setTimeout> | null = null;

// SETTING OF NEW CUSTOM BUTTONS
export function setCustomButtonsForZoom(rootMap: am5.Root, chartMap: am5map.MapChart) {
  cargarBotones(rootMap, chartMap);
}

export function loadMapCustomHomeButton(
  rootMap: am5.Root,
  chartMap: am5map.MapChart,
  callbackAction: () => void,
  position: MapButtonPosition = 1,
  useSvgIcon: boolean = false
) {
  setTimeout(() => {
    if (useSvgIcon) {
      const zoomControl = chartMap.get('zoomControl');
      const buttonIndex = getZoomControlButtonIndex(position);
      const iconStringHome =
        'M2.5,13.1h.9v8h6.9v-4.6h3.4v4.6h6.9v-8h1L12,2.9,2.5,13.1Z';

      const homeIconButton = zoomControl.children.moveValue(
        am5.Button.new(rootMap, {
          paddingTop: 9,
          paddingBottom: 9,
          width: 36,
          height: 36,
          icon: am5.Graphics.new(rootMap, {
            svgPath: iconStringHome,
            fill: am5.color(0x003874),
            scale: 0.9,
          })
        }),
        buttonIndex
      );

      settingButtonColors(homeIconButton, callbackAction);
      return;
    }

    const customHtmlIcon = getHTMLCustomButton(MAP_ICONS.HOME.inline_blue);
    const button = chartMap.children.push(
      am5.Button.new(rootMap, getCustomButtonSettings(rootMap, customHtmlIcon, chartMap, position))
    );
    settingButtonColors(button, callbackAction);
  }, TIME_TO_DELAY_CUSTOM_ELEMENTS);
}

export function loadMapCustomWorldButton(
  rootMap: am5.Root,
  chartMap: am5map.MapChart,
  callbackAction: () => void,
  position: MapButtonPosition = 1,
  mostrarBoton: boolean = true
) {
  if (!mostrarBoton) {
    return;
  }
  
  setTimeout(() => {
    const zoomControl = chartMap.get('zoomControl');
    const buttonIndex = getZoomControlButtonIndex(position);

    const iconStringWorld =
      'M7.629,15.258a7.428,7.428,0,0,1-2.975-.6A7.617,7.617,0,0,1,.6,10.6,7.428,7.428,0,0,1,0,7.629,7.428,7.428,0,0,1,.6,4.654,7.617,7.617,0,0,1,4.654.6,7.428,7.428,0,0,1,7.629,0,7.428,7.428,0,0,1,10.6.6a7.617,7.617,0,0,1,4.053,4.053,7.428,7.428,0,0,1,.6,2.975,7.428,7.428,0,0,1-.6,2.975A7.617,7.617,0,0,1,10.6,14.657a7.428,7.428,0,0,1-2.975.6m-.82-1.163V12.531a1.471,1.471,0,0,1-1.125-.5,1.66,1.66,0,0,1-.458-1.163v-.839l-3.91-3.91a5.363,5.363,0,0,0-.134.753,7.341,7.341,0,0,0-.038.753,6.384,6.384,0,0,0,1.612,4.33,6.15,6.15,0,0,0,4.053,2.136m5.607-2.06a6.306,6.306,0,0,0,.734-.973,6.791,6.791,0,0,0,.534-1.078,6.107,6.107,0,0,0,.324-1.154,6.875,6.875,0,0,0,.1-1.2,6.539,6.539,0,0,0-4.063-6.084v.343a1.66,1.66,0,0,1-.458,1.163,1.471,1.471,0,0,1-1.125.5H6.809V5.207a.659.659,0,0,1-.257.534.9.9,0,0,1-.582.21H4.387V7.629H9.308a.669.669,0,0,1,.534.248.859.859,0,0,1,.21.572v2.422h.82a1.545,1.545,0,0,1,.973.324,1.615,1.615,0,0,1,.572.839';

    let iconSeleccionado = iconStringWorld;
    const homeIcondButton = zoomControl.children.moveValue(
      am5.Button.new(rootMap, {
        paddingTop: 10,
        paddingBottom: 10,
        width: 36,
        height: 36,
        icon: am5.Graphics.new(rootMap, {
          svgPath: iconSeleccionado,
          fill: am5.color(0x003874),
          scale: 1.125
        })
      }),
      buttonIndex
    );
    homeIcondButton.get('background').setAll({
      fill: am5.color(0xffffff),
      fillOpacity: 0.8,
      stroke: am5.color(0x003874)
    });
    homeIcondButton
      .get('background')
      .states.create('hover', {})
      .setAll({
        fill: am5.color(0xbcd1e6),
        fillOpacity: 1
      });
    homeIcondButton.set('cursorOverStyle', 'pointer');

    settingButtonColors(homeIcondButton, callbackAction);
  }, TIME_TO_DELAY_CUSTOM_ELEMENTS);
}

export function loadMapCustomPeruButton(
  rootMap: am5.Root,
  chartMap: am5map.MapChart,
  callbackAction: () => void,
  position: MapButtonPosition = 1,
  mostrarBoton: boolean = true
) {
  if (!mostrarBoton) {
    return;
  }
  
  setTimeout(() => {
    const zoomControl = chartMap.get('zoomControl');
    const buttonIndex = getZoomControlButtonIndex(position);
    const iconStringPeru =
      'M18.1,23.9c-1-.4-1.4-1.5-2.4-1.9-1.9-.9-4-1.7-5.1-3.6,0-.1-.1-.3,0-.4.2-.6-.6-1.2-.8-1.8-.8-1-1.4-2.1-1.9-3.3-1-1.6-1.6-3.9-3.5-4.6-.2,0-.5-.1-.5-.5.5-.3.5-.4,0-.8,0,0,0-.2,0-.2.1-.4-.6-.9,0-1.3,1.1-1.5,1.8-.6.9-.2,0,.2,0,.4,0,.6,1.3-.6,1.5,1.5,2.1.3.5-.4.4-1.1.6-1.6,0,0,.2.1.3,0,.6-1.2,2.3-.9,3-2.1.4-.3.4-.9.7-1.2.2-.3-.2-.8-.4-1.1.7-.2,1.4.2,1.7.9.2.6,1,.5,1.1,1.1,0,0,.1.2.2.2.7.2-.3,1.1,1.3.8.5,0,.7-.5,1.2-.2.3.1.7-.1,1,0,.2.2.8.3.7.6-.1.3-1.2,1.6-.3,1.2.2.2.9,1,0,.6-.4-.3-.7.1-1,.2-.9,0-2.2.5-2.5,1.4,0,.4-.4.6-.2.9,0,.2,0,.3-.1.4,0,0,0,0,0,0-.6.1-.5.8-.8,1.2.3.3.3.7.6,1.1.3.4,1,.8.4,1.4.6,0,1.2,0,1.3.7.7.2,1.5,0,1.9-.6.4-.1,0,1.8.2,1.7.4.6,1-.4,1.4.2.4,1,1.5,1.9.7,2.8-.2.4-.2.9,0,1.3-.1.4-.9.8-.5,1.2-.6,2.6,1.6.5-.4,2.6-.3.1,0,.3,0,.6-.7.4,0,1-1,1';

    let iconSeleccionado = iconStringPeru;
    const homeIcondButton = zoomControl.children.moveValue(
      am5.Button.new(rootMap, {
        paddingTop: 8,
        paddingBottom: 8,
        width: 36,
        height: 36,
        icon: am5.Graphics.new(rootMap, {
          svgPath: iconSeleccionado,
          fill: am5.color(0x003874),
          scale: 0.825
        })
      }),
      buttonIndex
    );
    homeIcondButton.get('background').setAll({
      fill: am5.color(0xffffff),
      fillOpacity: 0.8,
      stroke: am5.color(0x003874)
    });
    homeIcondButton
      .get('background')
      .states.create('hover', {})
      .setAll({
        fill: am5.color(0xbcd1e6),
        fillOpacity: 1
      });
    homeIcondButton.set('cursorOverStyle', 'pointer');

    settingButtonColors(homeIcondButton, callbackAction);
  }, TIME_TO_DELAY_CUSTOM_ELEMENTS);
}

export function loadMapCustomExtrangeroButton(
  rootMap: am5.Root,
  chartMap: am5map.MapChart,
  callbackAction: () => void,
  position: MapButtonPosition = 1,
  mostrarBoton: boolean = true
) {
  loadMapCustomWorldButton(rootMap, chartMap, callbackAction, position, mostrarBoton);
}

/**
 * CONFIG BUTTON COLORS ONLY IN THIS FILE
 * @param button
 * @param callbackAction
 */
function settingButtonColors(button: am5.Button, callbackAction: () => void) {
  button.get('background').setAll({
    fill: am5.color(0xffffff),
    stroke: am5.color(0x003874)
  });

  button
    .get('background')
    .states.create('hover', {})
    .setAll({
      fill: am5.color('#6DB2E2'),
      fillOpacity: 0.7
    });
  button
    .get('background')
    .states.create('down', {})
    .setAll({
      fill: am5.color('#6DB2E2'),
      fillOpacity: 1
    });
  button
    .get('background')
    .states.create('pressed', {})
    .setAll({
      fill: am5.color('#6DB2E2'),
      fillOpacity: 0.7
    });
  button.events.on('click', (ev) => {
    // Detener la propagación del evento para evitar que active elementos subyacentes
    if (ev.originalEvent) {
      const originalEvent = ev.originalEvent as any;
      if (originalEvent.stopPropagation) {
        originalEvent.stopPropagation();
      }
      if (originalEvent.stopImmediatePropagation) {
        originalEvent.stopImmediatePropagation();
      }
      if (originalEvent.preventDefault) {
        originalEvent.preventDefault();
      }
    }

    lockScrollPositionTemporarilyForButtons();

    setTimeout(() => {
      callbackAction();
    }, 0);
  });
  button.set('cursorOverStyle', 'pointer');
}

function lockScrollPositionTemporarilyForButtons(durationMs: number = BUTTON_SCROLL_LOCK_DURATION_MS): void {
  buttonScrollLockRelease?.();

  if (buttonScrollLockTimer) {
    clearTimeout(buttonScrollLockTimer);
    buttonScrollLockTimer = null;
  }

  const x = window.scrollX;
  const y = window.scrollY;
  const restore = () => window.scrollTo({ left: x, top: y, behavior: 'auto' });
  const onScroll = () => restore();

  window.addEventListener('scroll', onScroll, { passive: true });
  buttonScrollLockRelease = () => {
    window.removeEventListener('scroll', onScroll);
    buttonScrollLockRelease = null;
  };

  restore();
  requestAnimationFrame(restore);
  setTimeout(restore, 0);
  setTimeout(restore, 120);
  setTimeout(restore, 300);

  buttonScrollLockTimer = setTimeout(() => {
    buttonScrollLockRelease?.();
    buttonScrollLockTimer = null;
  }, durationMs);
}

function getHTMLCustomButton(iconUrl: string): string {
  return `
    <div style="padding-top: 0px; padding-left: 0px; margin-left: -2px">
      <img alt="iconurl" style="width: 20px; height: 20px" src="${iconUrl}" />
    </div>
  `;
}

export type MapButtonPosition = 1 | 2 | 3;

function getZoomControlButtonIndex(position: MapButtonPosition): number {
  return Math.max(0, 2 - position);
}

function getCustomButtonSettings(
  rootMap: am5.Root,
  html: string,
  chartMap: am5map.MapChart,
  position: MapButtonPosition
): am5.IButtonSettings {
  const chartWidth = chartMap.width();
  const chartHeight = chartMap.height();

  return {
    label: am5.Label.new(rootMap, {
      html: html,
      fontSize: 12,
      textAlign: 'center'
    }),
    x: chartWidth - 46,
    y: chartHeight - (125 + 40 * (position - 1)),
    width: 36,
    height: 36,
    paddingBottom: 0,
    paddingLeft: 0,
    paddingRight: 0,
    paddingTop: 0
  } as am5.IButtonSettings;
}
// SETTINGS OF HEAT LEGENDS

export type HeatLegendTextType = 'type_1' | 'type_2' | 'participacion';
export const HEAT_LEGEND_TEXT_01 = 'Porcentaje de actas contabilizadas';
export const HEAT_LEGEND_PARTICIPACION = 'Porcentaje de participación ciudadana';
export const HEAT_LEGEND_TEXT_02 = 'Porcentaje de votos válidos';
export const HEAT_LEGEND_TEXTS: { [key in HeatLegendTextType]: string } = {
  type_1: HEAT_LEGEND_TEXT_01,
  type_2: HEAT_LEGEND_TEXT_02,
  participacion: HEAT_LEGEND_PARTICIPACION
};

export function loadMapCustomHeatLegend(
  rootMap: am5.Root,
  chartMap: am5map.MapChart,
  colors: { init: string; end: string },
  heatLegendText: string = HEAT_LEGEND_TEXT_01
) {
  setTimeout(() => {
    const chartHeight = chartMap.height();

    const heatLegend = chartMap.children.push(
      am5.HeatLegend.new(rootMap, {
        orientation: 'horizontal',
        startColor: am5.color(colors.init),
        endColor: am5.color(colors.end),
        startText: '0%',
        endText: '100%',
        width: am5.percent(50),
        x: am5.percent(2),
        y: chartHeight - LEGEND_HEIGHT
      })
    );

    heatLegend.startLabel.setAll({
      fontSize: 12,
      fill: am5.color(0x295789)
    });
    heatLegend.endLabel.setAll({
      fontSize: 12,
      fill: am5.color(0x295789)
    });

    heatLegend.children.push(
      am5.Label.new(rootMap, {
        text: heatLegendText,
        fill: am5.color(0x295789),
        fontSize: 12,
        paddingLeft: 0,
        paddingTop: 0
      })
    );
  }, TIME_TO_DELAY_CUSTOM_ELEMENTS);
}

// SETTING OF HTML TOOLTIP

export type TooltipType = 'default' | 'tooltip_01';

const traslateQuantityVotes: string = 'resultado-ubicacion-geografica.cantidad_votos';
const traslateVotesValids: string = 'resultado-ubicacion-geografica.votos_validos';

export function getHTMLTooltip(hotMapUbigeo: IHotMapUbigeoItem): string {
  const { ubigeoName, candidateName, validVotes, percentageValidVotes } = hotMapUbigeo;
  const fontSize = 'font-size: 12px;';
  const fontFamily = 'fontFamily: NotoSans-regular;';
  const fontColor = 'color: white;';
  const alignment = 'text-align: left;';
  const cellPadding = 'padding: 1px 5px;';
  const textAlign = 'text-align: center;';
  const styles = `${fontSize} ${fontFamily} ${fontColor} ${alignment} ${cellPadding} ${textAlign}`;
  const _percentageValidVotes = getDecimalPipe(percentageValidVotes, 3, true);

  return `
    <div style="${styles}">
      ${ubigeoName.toUpperCase()} <br>
      ${candidateName} <br>
      ${getTranslatedString(traslateQuantityVotes)}: ${validVotes} <br>
      ${getTranslatedString(traslateVotesValids)}: ${_percentageValidVotes}%
    </div>
  `;
}

export function getDefaultHTMLTooltip(ubigeoName: string) {
  const fontSize = 'font-size: 12px';
  return `<div style="${fontSize}; fontFamily: NotoSans-regular; color: white;">${ubigeoName.toUpperCase()}</div>`;
}

// METHODS TO HANDLE COLORS

interface RGBValue {
  red: number;
  green: number;
  blue: number;
}

/**
 * @param percentage should be a value between 0 and 100
 */
export function getDegradatedColorFromPercentage(percentage: number, colors: { init: string; end: string }): string {
  const initialColor = convertHexToRGB(colors.init);
  const finalColor = convertHexToRGB(colors.end);
  const newColor: RGBValue = {
    blue: getNewValueInRange(initialColor.blue, finalColor.blue, percentage / 100),
    green: getNewValueInRange(initialColor.green, finalColor.green, percentage / 100),
    red: getNewValueInRange(initialColor.red, finalColor.red, percentage / 100)
  };
  const newColorHex = convertRGBToHex(newColor.red, newColor.green, newColor.blue);
  return newColorHex;
}

function convertHexToRGB(color: string): RGBValue {
  const red = parseInt(color.substring(1, 3), 16);
  const green = parseInt(color.substring(3, 5), 16);
  const blue = parseInt(color.substring(5, 7), 16);
  return { red, green, blue };
}

function convertRGBToHex(red: number, green: number, blue: number): string {
  const hexRed = red.toString(16);
  const stringRed = hexRed.length == 1 ? '0' + hexRed : hexRed;
  const hexGreen = green.toString(16);
  const stringGreen = hexGreen.length == 1 ? '0' + hexGreen : hexGreen;
  const hexBlue = blue.toString(16);
  const stringBlue = hexBlue.length == 1 ? '0' + hexBlue : hexBlue;
  return '#' + stringRed + stringGreen + stringBlue;
}

/**
 * @param value should be a value between 0 and 1
 */
function getNewValueInRange(init: number, end: number, value: number): number {
  return Math.round(init + (end - init) * value);
}
