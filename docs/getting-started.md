# Getting started

To use imputation-bot you have to enable API access from your Profile page. The required API token can be obtained from the [Michigan Imputation Server](https://imputationserver.sph.umich.edu) or [TOPMed Imputation Server](https://imputation.biodatacatalyst.nhlbi.nih.gov/) website. You can find configuration instructions [here](instances.md).


## Run imputation

You can use the `impute` command to submit a job:

- The `--files` parameter defines the location of your VCF file(s). If you plan to impute more than one file you can enter the path to a folder or multiple filenames separated by space.
- The `--refpanel` parameter specifies the used reference panel. For the **1000 Genomes Phase 3** panel the ID is `1000g-phase-3-v5`. If you are not sure what panels are provided by the server, you can use `imputationbot refpanels` to get a list of all reference panels and their supported populations.
- The `--population` parameter is used in quality control to compare the allele frequencies between your data and the reference panel. Please note that not every reference panel supports all sub-populations. For **1000 Genomes Phase 3** you can use `eur` which stands for **European**.

The complete command using this [example file](https://github.com/lukfor/imputationserver-ashg20/raw/main/files/chr20.R50.merged.1.330k.recode.small.vcf.gz) looks like this:


```sh
imputationbot impute --files chr20.small.vcf.gz --refpanel 1000g-phase-3-v5 --population eur
```

After submission we get the URL where we can monitor the progress of our job.

### Auto download

If the `--autoDownload` flag is set then imputation-bot waits until the job is finished and starts the download for you:

```sh
imputationbot impute --files chr20.small.vcf.gz --refpanel 1000g-phase-3-v5 --population eur --autoDownload
```
A new folder is created in the current directory and contains all the downloaded results.

### Auto download and decryption

You can can combine the `--autoDownload` flag with the `--password` parameter to set a user-defined password to encrypt the result files. In this case imputation-bot decrypts all files after download:

```sh
imputationbot impute --files chr20.small.vcf.gz --refpanel 1000g-phase-3-v5 --population eur --autoDownload --password my_strong_password
```
The output directory contains now also all `*.vcf.gz` and `*.info.gz` files.

### Submit all files from a folder

```sh
imputationbot impute --files folder/with/vcf-files --refpanel 1000g-phase-3-v5 --population eur
```

### Submit only vcf.gz files

```sh
imputationbot impute --files folder/*.vcf.gz --refpanel 1000g-phase-3-v5 --population eur
```

Learn more about the `impute` command [here](submit-jobs.md).

## Monitor jobs

We can also use Imputation Bot to get a list of all our jobs and their status:

```sh
imputationbot jobs
```

To get more details about our job, we can use the `jobs` command followed by the job ID:

```sh
imputationbot jobs job-XXXXXXXX-XXXXXX-XXX
```

Learn more about the `jobs` command [here](list-jobs.md).

## Download results

We can use the `download` command to download all imputed genotypes and the QC report at once for a given job ID:

```sh
imputationbot download job-XXXXXXXX-XXXXXX-XXX
```

If the job is still running, imputation-bot waits until the job is finished and starts automatically with the download.

You can provide imputation-bot the password we sent you via email and it decrypts all files for you:

```sh
imputationbot download job-XXXXXXXX-XXXXXX-XXX --password MYPASSWORD
```

Learn more about the `download` command [here](download-results.md).
