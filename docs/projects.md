# Manage Projects

Imputation-Bot helps you to manage projects consisting of multiple jobs. By defining projects, all jobs of a project can be listed at once as well as downloading results with a single command. All projects are stored locally in ` ~/.imputationbot/imputationbot.projects`.

## List Projects

All projects can be listed with the `projects` command:

```sh
imputationbot projects
```


## Multiple GWAS data

Submit job for study 1 and create project "my-consortia":

```sh
imputationbot impute --files /path/to/study_1.vcf.gz --refpanel 1000g-phase-3-v5 --population eur --name study1 --project my-consortia
```

Job-Name: my-consortia_study1_1000g-phase-3-v5

Submit job for study 2 and add it to project "my-consortia":

```sh
imputationbot impute --files /path/to/study_2.vcf.gz --refpanel 1000g-phase-3-v5 --population eur --name study2 --project my-consortia
```

Job-Name: my-consortia_study2_1000g-phase-3-v5

Check all studies that are part of this project:

```sh
imputationbot jobs my-consortia
```

Download all results of jobs that are part of this project:

```sh
imputationbot download my-consortia
```

## Remove Project

```sh
imputationbot remove-project my-consortia
```
