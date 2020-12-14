# Submit Jobs

The `impute` command can be used to submit a job. The following command imputes the file `my-study/my-study.chr1.vcf.gz` against reference panel **1000 Genomes Phase 3** and used the **European** population as a reference population during quality control:

```sh
imputationbot impute --files my-study/my-study.chr1.vcf.gz --refpanel 1000g-phase-3-v5 --population eur
```

After all files are uploaded and the job submission was successful, a URL is provided where you can monitor the progress of our job.

#### Submit all files from a folder:

```sh
imputationbot impute --files folder/with/vcf-files --refpanel 1000g-phase-3-v5 --population eur
```

#### Submit only vcf.gz files:

```sh
imputationbot impute --files folder/*.vcf.gz --refpanel 1000g-phase-3-v5 --population eur
```

#### Submit only files matching a certain pattern:

```sh
imputationbot impute --files folder/my_study.*.vcf.gz --refpanel 1000g-phase-3-v5 --population eur
```

#### Auto download

If the `--autoDownload` flag is set then imputation-bot waits until the job is finished and starts the download for you:

```sh
imputationbot impute --files chr20.small.vcf.gz --refpanel 1000g-phase-3-v5 --population eur --autoDownload
```
A new folder is created in the current directory and contains all the downloaded results.

#### Auto download and decryption

You can can combine the `--autoDownload` flag with the `--password` parameter to set a user-defined password to encrypt the result files. In this case imputation-bot decrypts all files after download:

```sh
imputationbot impute --files chr20.small.vcf.gz --refpanel 1000g-phase-3-v5 --population eur --autoDownload --password my_strong_password
```
The output directory contains now also all `*.vcf.gz` and `*.info.gz` files.


## Required parameters

### `--files <file_path>`

Defines the location of the VCF file. To impute more than one file you can either enter the path to a folder or multiple filenames.

### `--refpanel <ref>`

Specifies the ID of the reference panel. For example, the **1000 Genomes Phase 3** panel on Michigan Imputation Server has the id `1000g-phase-3-v5`. If you are not sure what panels are provided by the server, you can use the command `imputationbot refpanels` to get a list of all reference panels and their ID.

### `--population <pop>`

This information is used to compare the allele frequencies between your data and the reference panel. Please note that not every reference panel supports all sub-populations. For example, the ID of the **European** population is `eur`. You can use the command `imputationbot refpanels` to get a list of all reference panels and their supported populations.


## Optional parameters

### `--name <name>`

An optional job name. Default name is the job id.

### `--password <password>`

An user defined password that is used to encrypt the imputed genotypes. If no password is provided, the service creates a random password and sends it to the email address of the owner of the token.

### `--build <build>`

The build of the uploaded data. After submission the server automatically updates the genome positions (liftOver) of the uploaded data to match the reference data. Options: `hg19` or `hg38`. Default: `hg19`.

### `--r2Filter <filter>`

To minimize the file size, Michigan Imputation Server includes an option to filter out all imputed SNPs with a r2-value smaller then the specified value. r2-values are a metric to define the imputation quality. Possible values: `0`, `0.001`, `0.1`, `0.2` and `0.3`. Default: `0` (no filtering).

### `--aesEncryption <yes>`

All Imputation Server results are encrypted by default. Please tick this checkbox if you want to use AES 256 encryption instead of the default encryption method. Please note that AES encryption does not work with standard unzip programs. We recommend to use 7z instead. Possible values: `yes`or `no`. Default: `no`.

### `--autoDownload`

If this flag is set then imputation-bot waits until the job is finished and starts the download. You can can combine the `--autoDownload` flag with the `--password` parameter to set a user-defined password to encrypt the result files. In this case imputation-bot it decrypts all files for you after download.
