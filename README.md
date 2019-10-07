# Imputationbot :robot:
Your personal bot for Imputationserver

## Install

```
curl -sL imputationbot.now.sh | bash
```

## Configure

```
imputationbot configure
```

your access token is stored in `~/.imputationbot`.


## List Jobs

Prints all jobs to stdout:

```
imputationbot list
```

Write all jobs to a json file:

```
imputationbot list --json my-jobs.json
```

## Submit Job

```
imputationbot run --files path/to/my.vcf.gz --refpanel hapmap2 --population eur
```

## Download all Results

```
imputationbot download --job job-XXXXXXXX-XXXXXX-XXX
```

