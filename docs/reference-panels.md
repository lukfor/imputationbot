# Reference Panels

The `refpanels` command can be used to list available reference panels of all configured instances. In addition, it lists all supported populations that can be used for quality control.

## Examples

```sh
imputationbot refpanels
```

Output:


```sh
imputation-bot 1.0.0 🤖
https://imputationserver.sph.umich.edu
(c) 2019 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger
Built by null on null

╔═══════════════════╤═════════════════════════════════════════════════════════╤═════════════════════════════╤════════════════════════════╗
║ ID                │ Name                                                    │ Populations                 │ Instance                   ║
╠═══════════════════╪═════════════════════════════════════════════════════════╪═════════════════════════════╪════════════════════════════╣
║ 1000g-phase-1     │ 1000G Phase 1 v3 Shapeit2 (no singletons) (GRCh37/hg19) │ afr       AFR               │ Michigan Imputation Server ║
║                   │                                                         │ amr       AMR               │                            ║
║                   │                                                         │ asn       ASN               │                            ║
║                   │                                                         │ eur       EUR               │                            ║
║                   │                                                         │ mixed     Other/Mixed       │                            ║
╟───────────────────┼─────────────────────────────────────────────────────────┼─────────────────────────────┼────────────────────────────╢
║ 1000g-phase-3-v5  │ 1000G Phase 3 v5 (GRCh37/hg19)                          │ afr       AFR               │ Michigan Imputation Server ║
║                   │                                                         │ amr       AMR               │                            ║
║                   │                                                         │ eas       EAS               │                            ║
║                   │                                                         │ sas       SAS               │                            ║
║                   │                                                         │ eur       EUR               │                            ║
║                   │                                                         │ mixed     Other/Mixed       │                            ║
╟───────────────────┼─────────────────────────────────────────────────────────┼─────────────────────────────┼────────────────────────────╢
║ caapa             │ CAAPA African American Panel (GRCh37/hg19)              │ AA        African Americans │ Michigan Imputation Server ║
║                   │                                                         │ mixed     Other/Mixed       │                            ║
╟───────────────────┼─────────────────────────────────────────────────────────┼─────────────────────────────┼────────────────────────────╢
║ genome-asia-panel │ Genome Asia Pilot - GAsP (GRCh37/hg19)                  │ asn       ASN               │ Michigan Imputation Server ║
║                   │                                                         │ mixed     Other/Mixed       │                            ║
╟───────────────────┼─────────────────────────────────────────────────────────┼─────────────────────────────┼────────────────────────────╢
║ hapmap-2          │ HapMap 2 (GRCh37/hg19)                                  │ eur       EUR               │ Michigan Imputation Server ║
╟───────────────────┼─────────────────────────────────────────────────────────┼─────────────────────────────┼────────────────────────────╢
║ hrc-r1.1          │ HRC r1.1 2016 (GRCh37/hg19)                             │ eur       EUR               │ Michigan Imputation Server ║
║                   │                                                         │ mixed     Other/Mixed       │                            ║
╚═══════════════════╧═════════════════════════════════════════════════════════╧═════════════════════════════╧════════════════════════════╝
```
