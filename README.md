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
imputationbot jobs
```

Get details about a job:

```
imputationbot jobs job-XXXXXXXX-XXXXXX-XXX 
```

## Submit Job

```
imputationbot impute --files path/to/my.vcf.gz --refpanel hapmap2 --population eur
```

```
imputationbot validate --files path/to/my.vcf.gz --refpanel hapmap2 --population eur
```


## Download all Results

```
imputationbot download job-XXXXXXXX-XXXXXX-XXX
```

