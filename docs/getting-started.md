# Getting started

## Configuration

To use Imputation-Bot you have to enable API access from your Profile page. The required API token can be obtained from the [Michigan Imputation Server](https://imputationserver.sph.umich.edu) website.

**Step 1:** Login and click on your **username** and then **profile**:

![Image1](assets/token1.png)

**Step 2:** Click on **Create API Token**

![Image1](assets/token2.png)

**Step 3:** Copy your API Token and paste it when `imputationbot add-instance` ask for it.

![Image1](assets/token3.png)

Api Tokens are valid for 30 days. You can check the status in the web interface or with `imputationbot instances`

![Image1](assets/token4.png)

**Step 4:** Next, configure imputationbot with the following command and enter the address and your API token:

```
imputationbot add-instance
```

```
Imputation Bot 0.1.0 🤖
https://imputationserver.sph.umich.edu
(c) 2019 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger
Built by lukas on 2019-10-09T15:54:07Z

Imputationserver Url [https://imputationserver.sph.umich.edu]:
API Token [None]: eyJjdHkiOiJ0ZXh0XC9wbGFpbiIsImFsZyI6IkhTMjU2In0.eyJtYWlsIjoibHVrYXMuZm9yZXJAaS1tZWQuYWMuYXQiLCJleHBpcmUiOjE1NzMyMjkwNTY3NTEsIm5hbWUiOiJMdWthcyBGb3JlciIsImFwaSI6dHJ1ZSwidXNlcm5hbWUiOiJsdWtmb3IifQ.qY7iEM6ul-gJ0EuHmEUHRnoS5hZs7kD1HC95NFaxE9w
```

## Run imputation

You can use the `impute` command to submit a job:

- The `--files` parameter defines the location of our VCF file. If we plan to impute more than one file we can enter the path to a folder or separate multiple filenames by `,`.
- We can use the `--refpanel` parameter to specify the reference panel. For the **1000 Geneoms Phase 3** panel we use `1000g-phase-3-v5`. If we are not sure what panels are provided by the server, we can use `imputationbot refpanels` to get a list of all reference panels and their supported populations.
- For `--population` we use `eur` which stands for **European**

The complete command looks like this:

```sh
imputationbot impute --files /path/to/your.vcf.gz --refpanel 1000g-phase-3-v5 --population eur
```

A test file is available [here](https://github.com/lukfor/imputationserver-ashg20/raw/main/files/chr20.R50.merged.1.330k.recode.small.vcf.gz).

After submission we get the URL where we can monitor the progress of our job. However, we can also use Imputation Bot to get a list all our jobs and their status:

```sh
imputationbot jobs
```

To get more details about our job, we can use the `jobs` command followed by the job ID:

```sh
imputationbot jobs job-XXXXXXXX-XXXXXX-XXX
```

We can use the `download` command to download all imputed genotypes and the QC report at once:

```sh
imputationbot download job-XXXXXXXX-XXXXXX-XXX
```

If the job is still running, Imputation Bot waits until the job is finished and starts automatically with the download.

You can provide Imputation Bot the password we sent you via email and it decrypts all files for you:

```sh
imputationbot download job-XXXXXXXX-XXXXXX-XXX --password MYPASSWORD
```
