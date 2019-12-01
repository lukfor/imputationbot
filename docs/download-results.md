# Download Results

The `download` command allows to download all imputed genotypes and the QC report at once:

```
imputationbot download job-XXXXXXXX-XXXXXX-XXX
```

If the job is still running, Imputation Bot waits until the job is finished and starts automatically with the download.

You can provide Imputation Bot the password we sent you via email and it decrypts all files for you:

```
imputationbot download job-XXXXXXXX-XXXXXX-XXX --password MYPASSWORD
```

To download all jobs of a project, we can use the `download` command followed by the project name:

```
imputationbot download my-project
```
