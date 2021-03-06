# imputation-bot

![Image1](assets/logo.png)

Your personal bot for [Michigan Imputation Server](https://imputationserver.sph.umich.edu) and [TOPMed Imputation Server](https://imputation.biodatacatalyst.nhlbi.nih.gov/).



## Requirements


You will need the following things properly installed on your computer.

* Java 8 or higher

imputation-bot works on Linux, macOS and Windows.

## Download and Install

### Linux or macOS

Download and install the latest version from our download page using the following commands:

```
curl -sL imputationbot.now.sh | bash
```

It will create the `imputationbot` executable file in the current directory. Optionally, move the `imputationbot` file to a directory accessible by your `$PATH` variable.

Test the installation with the following command:

```sh
imputationbot version
```

The documentation is available at [http://imputationbot.readthedocs.io](http://imputationbot.readthedocs.io).

All releases are also available on [Github](https://github.com/lukfor/imputationbot/releases).


### Windows

Download the [latest version](https://github.com/lukfor/imputationbot/releases/latest) of `imputationbot-X.X.X-windows.zip` from our download page.

- Open the zip file and extract it content to a folder of choice.

- Open the command-line, navigate to your folder where you extracted the exe file.

- Test the installation with following command:

```sh
imputationbot version
```

It will output something similar to the text shown below:

```sh
imputation-bot 1.0.0 🤖
https://imputationserver.sph.umich.edu
(c) 2019-2020 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger
Built by lukas on 2020-09-01T11:31:10Z
```

Now you are ready to [configure](instances.md) imputation-bot and impute your GWAS data. Read the [Getting started](getting-started.md) section to learn more about how to submit your first job.
