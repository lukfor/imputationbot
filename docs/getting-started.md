# Getting started


## Run imputation

You can use the `impute` command to submit a job:

- The `--files` parameter defines the location of our VCF file. If we plan to impute more than one file we can enter the path to a folder or separate multiple filenames by `,`.
- We can use the `--refpanel` parameter to specify the reference panel. For the **1000 Geneoms Phase 3** panel we use `1000g-phase-3-v5`. If we are not sure what panels are provided by the server, we can use `imputationbot refpanels` to get a list of all reference panels and their supported populations.
- For `--population` we use `eur` which stands for **European**

The complete command looks like this:

```sh
imputationbot impute --files /path/to/your.vcf.gz --refpanel 1000g-phase-3-v5 --population eur
```

After submission we get the URL where we can monitor the progress of our job. However, we can also use Imputation Bot to get a list all our jobs and their status:

```
imputationbot jobs
```

To get more details about our job, we can use the `jobs` command followed by the job ID:

```
imputationbot jobs job-XXXXXXXX-XXXXXX-XXX
```

We can use the `download` command to download all imputed genotypes and the QC report at once:

```
imputationbot download job-XXXXXXXX-XXXXXX-XXX
```

If the job is still running, Imputation Bot waits until the job is finished and starts automatically with the download.

You can provide Imputation Bot the password we sent you via email and it decrypts all files for you:

```
imputationbot download job-XXXXXXXX-XXXXXX-XXX --password MYPASSWORD
```
