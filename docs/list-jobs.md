# List Jobs

The `jobs` command can be used to get a list all jobs and their status:

```
imputationbot jobs
```

This command lists the latest 10 jobs. To get a list of all your jobs, the `--all` can be used. 

```
imputationbot jobs --all
```

To get more details about a single job, you can use the `jobs` command followed by the job ID:

```
imputationbot jobs job-XXXXXXXX-XXXXXX-XXX
```

To get more details about all jobs of a project, you can use the `jobs` command followed by the project name:

```
imputationbot jobs my-project
```
