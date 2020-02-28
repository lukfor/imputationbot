# Imputation-Bot

![Image1](assets/logo.png)

Your personal bot for [Michigian Imputation Server](https://imputationserver.sph.umich.edu).



## Requirements


You will need the following things properly installed on your computer.

* [Java 8 or higher](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

ImputationBot works on Linux and MacOS.

## Download and Installation

Download and install the latest version from our download page using the following commands:

```sh
curl -sL imputationbot.now.sh | bash
```

The scripts create the `imputationbot` executable file in the current directory. If you move the `imputationbot` file to a directory accessible by your `$PATH` variable, you can can execute it without entering the full path to `imputationbot`.


Test the installation with the following command:

```sh
imputationbot version
```

It will output something similar to the text shown below:


```sh
Imputation Bot 0.2.0 🤖
https://imputationserver.sph.umich.edu
(c) 2019 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger
Built by lukas on 2019-12-02
```

Now you are ready to configure Imputation-Bot and impute your GWAS data. Read the [Getting started](getting-started) section to learn more about how to submit your first job.


## Manual installation

All releases are also available on [Github](https://github.com/lukfor/imputationbot/releases).
