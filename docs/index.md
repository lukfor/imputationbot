# Imputation-Bot

Your personal bot for [Michigian Imputation Server](https://imputationserver.sph.umich.edu).

## Requirements


You will need the following things properly installed on your computer.

* [Java 8 or higher](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)


## Download and Installation

Download and install the latest version from our download page using the following commands:

```
curl -sL imputationbot.now.sh | bash
```


Test the installation with the following command:

```sh
impuationbot version
```


## Get your API Token

Enable API access from your Profile page.

1. Login and click on your **username** and then **profile**:

![Image1](assets/token1.png)

2. Click on **Create API Token**

![Image1](assets/token2.png)

3. Copy your API Token and paste it when `imputationbot configure` ask for it.

![Image1](assets/token3.png)

Api Tokens are valid for 30 days. You can check the status in the web interface or with `imputationbot info`

![Image1](assets/token4.png)

4. Next, configure imputationbot with the following command:

```
imputationbot configure
```

```
Imputation Bot 0.1.0 🤖
https://imputationserver.sph.umich.edu
(c) 2019 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger
Built by lukas on 2019-10-09T15:54:07Z

Imputationserver Url [https://imputationserver.sph.umich.edu]:
API Token [None]: eyJjdHkiOiJ0ZXh0XC9wbGFpbiIsImFsZyI6IkhTMjU2In0.eyJtYWlsIjoibHVrYXMuZm9yZXJAaS1tZWQuYWMuYXQiLCJleHBpcmUiOjE1NzMyMjkwNTY3NTEsIm5hbWUiOiJMdWthcyBGb3JlciIsImFwaSI6dHJ1ZSwidXNlcm5hbWUiOiJsdWtmb3IifQ.qY7iEM6ul-gJ0EuHmEUHRnoS5hZs7kD1HC95NFaxE9w
```

## Manual installation

All releases are also available on [Github](https://github.com/lukfor/imputationbot/releases).
