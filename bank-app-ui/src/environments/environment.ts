export const environment = {
  production: true,
  rooturl : process.env["ROOT_URL"],
  uiUrl : process.env["UI_URL"],
  keycloak: {
    config: {
      url: process.env["KEYCLOAK_URL"],
      realm: process.env["KEYCLOAK_REALM"] ?? '',
      clientId: process.env["KEYCLOAK_CLIENT_ID"] ?? ''
    }
  }
};
