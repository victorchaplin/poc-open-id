# poc-open-id
This POC is for testing Open ID Connect protocol and uses Okta as identity provider

## Okta instance credentials
- org url: https://dev-790726.okta.com
- client id: 0oae049djFYWfkPp24x6
- client secret: 0X_JoWoQZhwolSA8D3x851Mhs9oU7CPcPChSoUpW

## Configuring Okta
- login to Okta and go to **Applications**
- go to **Add Application** and select the **Web** option
- enter a name for the app
- add http://localhost:8080/login/oauth2/code/okta to **Login Redirect URIs**
- click **Done** and capture the Client ID and the Client Secret values

## About the application and the Open ID Connect flow
- spring security used to provide OAuth and Open ID features
- spring security automatically creates a login page at http://localhost:8080/login
- the login page triggers the Open ID flow
- user is authenticated by Okta and can show user informations in OAuth2 standards or logout