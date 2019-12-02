# Configure Instances

Instead of storing usernames and passwords, Imputation-Bot is using API tokens to communicate with Michigan Imputation Server or any other clone. This approach has several advantages compared to user credentials: (1) API tokens can be revoked by the user at any time (e.g. if token is compromised), (2) API tokens have only access to a limited number of API endpoints (e.g. job handling) and it is therefore not possible to change user credentials via the API and (3) tokens are valid for 30 days but can be easily renewed using Imputation-Bot. The complete data exchange between Imputation-Bot and MIS is encrypted using Secure Sockets Layer (SSL).

## Obtain API Token

**Step 1:** Login and click on your **username** and then **profile**:

![Image1](assets/token1.png)

**Step 2:** Click on **Create API Token**

![Image1](assets/token2.png)

**Step 3:** Copy your API Token and paste it when `imputationbot configure-instance` ask for it.

![Image1](assets/token3.png)

Api Tokens are valid for 30 days. You can check the status in the web interface or with `imputationbot instances`

## Configure instance

```sh
imputationbot add-instance
```

Output:

```sh
Imputation Bot 0.1.0 🤖
https://imputationserver.sph.umich.edu
(c) 2019 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger
Built by lukas on 2019-10-09T15:54:07Z

Imputationserver Url [https://imputationserver.sph.umich.edu]:
API Token [None]: eyJjdHkiOiJ0ZXh0XC9wbGFpbiIsImFsZyI6IkhTMjU2In0.eyJtYWlsIjoibHVrYXMuZm9yZXJAaS1tZWQuYWMuYXQiLCJleHBpcmUiOjE1NzMyMjkwNTY3NTEsIm5hbWUiOiJMdWthcyBGb3JlciIsImFwaSI6dHJ1ZSwidXNlcm5hbWUiOiJsdWtmb3IifQ.qY7iEM6ul-gJ0EuHmEUHRnoS5hZs7kD1HC95NFaxE9w
```

## List instances

```sh
imputationbot instances
```

Output:

```sh
Imputation Bot 0.1.3 🤖
https://imputationserver.sph.umich.edu
(c) 2019 Lukas Forer, Sebastian Schoenherr and Christian Fuchsberger
Built by lukas on 2019-10-09T15:54:07Z

╔════╤════════════════════════════╤════════════════════════════════════════╤══════════╤═════════╤══════════════════════════════╗
║ ID │ Name                       │ Hostname                               │ Username │ Version │ Token expires on             ║
╠════╪════════════════════════════╪════════════════════════════════════════╪══════════╪═════════╪══════════════════════════════╣
║ 1  │ Michigan Imputation Server │ https://imputationserver.sph.umich.edu │ lukfor   │ 1.2.4   │ Fri Dec 27 13:14:48 CET 2019 ║
╚════╧════════════════════════════╧════════════════════════════════════════╧══════════╧═════════╧══════════════════════════════╝
```



## Remove instance

```sh
imputationbot remove-instance <ID>
```
