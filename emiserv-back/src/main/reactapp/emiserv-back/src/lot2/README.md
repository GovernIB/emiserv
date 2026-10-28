# Carpeta `lot2`

Aspecte visual comú de les aplicacions (Lot 2), basat en Notib.

## Regles

- **No s'edita a l'aplicació.** La còpia mestra és `src/lot2/` de base-react; els canvis es fan allà.
- **Es copia sencera** des de base-react a `src/lot2/` de cada aplicació (tots els fitxers, sense triar-ne).
- **Només depèn de `reactlib`** (àlies de la llibreria base-react), de llibreries npm i dels seus propis
  fitxers. Mai importa codi de l'aplicació (`../...`) ni de `@programari-limit/base-react`.
- Tot el que és propi d'una aplicació (colors, logos, icones de pantalles) es passa per paràmetre
  (p. ex. `createLot2Themes(colors)`) o es declara fora d'aquesta carpeta.

## Origen dels fitxers

Referència: Notib, `notib-back/src/main/reactapp/notib-back/src`, branca `notib2-dev`, commit `c24d4a7a1`.

| Fitxer | Origen |
| --- | --- |
| `layout.ts` | `utils/layout.ts` (còpia) |
| `icons.ts` | `utils/icons.ts`: claus comunes (pantalles, accions, estats, filtre, selecció); `avis` = `campaign` |
| `theme.ts` | `theme.ts`, parametritzat amb `createLot2Themes(colors)`; `accentColor` i `accentFillColor` iguals |
| `i18n.ts` | claus `comu.*` de `i18n/translation{Ca,Es}.ts` (i `en`), espai de noms `lot2` |
| `ScreenFrame.tsx` | `components/ScreenFrame.tsx`, **sense** `screenFrameSx` (selectors `:has(...)` i `LIB_TITLE_TOOLBAR`): ho fa `lot2ComponentProps.ts` |
| `ScreenFrameContext.ts` | `components/ScreenFrameContext.ts` (còpia) |
| `ScreenTitle.tsx` | `components/ScreenTitle.tsx`; la icona accepta qualsevol nom (`ScreenIconName`) |
| `titleBarControlsSx.ts` | `components/titleBarControlsSx.ts` (còpia) + `titleBarSx`, que a Notib era a `ScreenFrame.tsx` |
| `formScreenTitle.tsx` | `utils/formScreenTitle.tsx`; torna `ReactElement` (MuiForm ja accepta `string \| ReactElement`) |
| `FilterButtons.tsx` | `components/FilterButtons.tsx`; textos de l'espai de noms `lot2` |
| `FilterCount.tsx` | `components/FilterCount.tsx` (prop opcional `count?: number \| ((terms) => number)`); textos de l'espai de noms `lot2` |
| `springFilterCount.ts` | `utils/springFilterCount.ts` (còpia) |
| `StyledBadge.tsx` | `components/StyledBadge.tsx`; `primary` = `accentFillColor`, text `getContrastText` si no es passa `textcolor` |
| `lot2ComponentProps.ts` | nou: `lot2DefaultMuiComponentProps(theme)` i `lot2ScreenGridProps(theme)`, substitueixen el CSS de `ScreenFrame` sobre el DOM de la llibreria |
| `menuColors.ts` | `getMenuColorSet` de `components/BaseApp.tsx` (`TEMA` / `TEMA_INVERTIT` / `PEU` → `theme` / `inverse` / `footer`) com a `lot2MenuColors(theme, appearance)`, i `LOT2_APPEARANCE` (mides de `utils/layout.ts`) |
| `Lot2BaseApp.tsx` | `components/BaseApp.tsx` + la part de marc d'`App.tsx`: `frameSx` → `contentRootStyle`/`contentMainStyle`, `menuColorSetSx` → `appearance.menuColors`, padding de la capçalera (`APPBAR_PADDING_LEFT`) → `headerAppbarStyle`. Sense i18n, router ni menús propis de Notib: l'aplicació els passa com a props de `MuiBaseApp` |
| `DrassanaFooter.tsx` | `components/DrassanaFooter.tsx`; enllaços, títol, etiqueta de revisió i logos per props, i enllaços amb el `linkComponent` de base-react (sense `react-router-dom`) |
| `assets/drassana.png` | `assets/drassana.png` (còpia) |
| `index.ts` | reexporta el públic de la carpeta |

## Barra de títol de les pantalles

- **Formularis:** `lot2DefaultMuiComponentProps(theme)` (a `defaultMuiComponentProps` de `MuiBaseApp`)
  pinta la barra d'eines de tots els `MuiForm` com a barra de títol enganxosa, amb marges de 15px.
- **Graelles:** les props per defecte només hi posen el botó de crear amb text i el filtre ràpid
  plegable (`toolbarQuickFilterCollapsible`: una icona de cerca mentre és buit). La barra de títol és
  **opt-in**: la graella principal de la pantalla, la que porta el `ScreenTitle`, hi afegeix
  `lot2ScreenGridProps(theme)`:

  ```tsx
  const theme = useTheme();
  <ScreenFrame>
      <MuiDataGrid title={<ScreenTitle icon={icons.x} title={t('...')} />} {...lot2ScreenGridProps(theme)} ... />
  </ScreenFrame>
  ```

  Les altres graelles (subtaules, diàlegs de selecció, `DataGridDialog`...) mantenen la barra i els
  marges de la llibreria.
- **Separació:** tots els elements de la barra de títol (botons, botons d'icona, filtre ràpid, selector
  d'accions massives) queden separats per un sol gap de 8px (`columnGap` de `titleBarSx`); els controls
  no hi tenen marges propis.
- **Botons amb text:** `lot2` no canvia l'aparença per defecte (icones). Una pantalla pot posar
  `toolbarButtonsVariant: 'button'` a la graella (tornar, exportar, refrescar) o `variant: 'button'` a un
  `MuiActionReportButton`: a la barra de títol prenen l'aspecte del botó de crear; amb `'icon'`, el del
  botó de refrescar.

## Marc de l'aplicació (`Lot2BaseApp`)

```tsx
<Lot2BaseApp
    code="APP" logo={logoClar} logoDark={logoFosc} headerTitle={...}
    menuAppearance="theme"   // 'theme' | 'inverse' | 'footer'
    footer={<DrassanaFooter title="APP" />}   // per defecte <DrassanaFooter />; false = sense peu
    {...restaDePropsDeMuiBaseApp} />
```

- `headerAppbarStyle`, `contentRootStyle` i `contentMainStyle` de l'aplicació es combinen amb els de
  `lot2` (propietat a propietat) i guanya l'aplicació.
- `defaultMuiComponentProps` es combina **per component i a un sol nivell**: per a cada clau (`dataGrid`,
  `form`...) es fa `{ ...lot2[clau], ...app[clau] }`. Els objectes interns (p. ex. `toolbarSx` o
  `marginsSx`) **no** es combinen: si l'aplicació en passa un, substitueix sencer el de `lot2`.
- El peu per defecte (`<DrassanaFooter />`) pren `headerTitle` com a títol quan és un text. Si
  `headerTitle` és un element, l'aplicació ha de passar el peu amb el títol:
  `footer={<DrassanaFooter title="APP" />}`.
- `lot2HeaderLogoStyle(theme)` (exportat) és el `headerLogoStyle` per defecte: caixa del logo d'amplada
  `MENU_WIDTH - LOGO_BOX_LEFT` amb el separador a la vora del menú obert i `LOGO_SEPARATOR_GAP`.
  El `headerLogoStyle` de l'aplicació es combina a sobre (guanya l'aplicació).
- `headerLogoAlt` per defecte és `"Govern de les Illes Balears"`.
- `syncDocumentLang` (per defecte `true`) posa `<html lang>` als dos primers caràcters de l'idioma
  actual de base-react; `syncDocumentLang={false}` ho desactiva.
- `DrassanaFooter`: el logo té `alt="Drassana"` (sense `cursor: pointer`) i el text de data i revisió
  és `aria-hidden`.
- L'aplicació ha de registrar `lot2Translations` a l'espai de noms `lot2` (textos del peu).
- Els components de `lot2` tradueixen amb `useLot2Translation()` (de `i18n.ts`), no amb
  `useTranslation('lot2')` directament: així no depenen dels tipus i18next que declari l'aplicació
  (`CustomTypeOptions`, p. ex. `enableSelector: "optimize"`, que rebutja claus de tipus string).
