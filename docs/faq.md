# FAQ

### I did not receive a password for my imputation job
Michigan Imputation Server creates a random password for each imputation job. This password is not stored on server-side at any time. If you didn't receive a password, please check your Mail SPAM folder. Please note that we are not able to re-send you the password. However, you can also use the `password` parameter when submitting a job and set the password by hand.

### My token is expired. Why and what should I do?

Due to security reasons, tokens are valid for 30 days only. Please reopen the profile page of the web-service, revoke your token and create a new one.

### W get the following error message, when I try to download a job:

#### `File could not be downloaded. Max download limit exceeded.`

Due to security reasons, the number of downloads for each file is limited to 40.  In case you need an extension, please contact cfuchsb@umich.edu.

#### `File could not be downloaded. Job is retired.`

Due to security reasons, your data is available for *7 days*. After 7 days the job changes its state to `retired` and all data is deleted. In case you need an extension, please contact cfuchsb@umich.edu.
