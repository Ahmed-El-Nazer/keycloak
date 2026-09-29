# Keycloak setup for Identity Service

This setup is for local learning and development. Keycloak runs on port `8080`, while the Identity Service runs on port `8081`.

## 1. Run Keycloak

Create a Docker named volume once:

```bash
docker volume create keycloak_data
```

Then run Keycloak with its data directory mounted to that volume:

```bash
docker run --name keycloak \
  -p 8080:8080 \
  -v keycloak_data:/opt/keycloak/data \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest \
  start-dev
```

Stop and restart the same container without losing data:

```bash
docker stop keycloak
docker start keycloak
```

You can also remove and recreate the container with the same `keycloak_data` volume. The realms, clients, and users remain in the named volume. Do not run `docker volume rm keycloak_data` unless you intentionally want to delete all Keycloak data.

Open the admin console at <http://localhost:8080/admin> and sign in with:

- Username: `admin`
- Password: `admin`

`start-dev`, Keycloak's embedded development database, the default admin password, and the `latest` image tag are only suitable for local development. The named volume prevents accidental data loss during this learning phase. We will move Keycloak to PostgreSQL and pin a Keycloak version in a later step.

## 2. Create the application realm

Do not create application users in the built-in `master` realm. The `master` realm is for administering Keycloak.

1. Open the realm selector in the upper-left corner.
2. Select **Create realm**.
3. Set the realm name to `hands-on`.
4. Make sure the realm is enabled and create it.

The Identity Service accepts tokens only when their issuer is:

```text
http://localhost:8080/realms/hands-on
```

## 3. Create a client for curl testing

Inside the `hands-on` realm:

1. Open **Clients** and select **Create client**.
2. Choose **OpenID Connect**.
3. Set **Client ID** to `identity-service-client`.
4. Keep **Client authentication** off. This makes it a public client, so there is no client secret.
5. Turn **Standard flow** off for this temporary curl exercise.
6. Turn **Direct access grants** on.
7. Save the client.

Direct Access Grants let curl exchange a username and password for tokens. This is useful for this first exercise, but it should not be used by a browser or production application. We will disable it when we implement Authorization Code with PKCE.

## 4. Create a Keycloak test user

Inside the `hands-on` realm:

1. Open **Users** and select **Create new user**.
2. Set **Username** to `testuser`.
3. Set an email, first name, and last name if desired.
4. Create the user.
5. Open the user's **Credentials** tab.
6. Set the password to `password123`.
7. Turn **Temporary** off and save.

Important: `POST /api/users/register` currently creates only a record in the Identity Service PostgreSQL database. It does not create a Keycloak user yet. Therefore, use the Keycloak admin console to create the user for this security exercise. Registration will be integrated with Keycloak in a later step.

## 5. How the Identity Service validates tokens

The application is an OAuth 2.0 **resource server**. It does not process usernames and passwords and it does not call a local login endpoint.

The JWT settings are in `src/main/resources/application.properties`:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/hands-on
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/hands-on/protocol/openid-connect/certs
```

For every protected request, Spring Security:

1. Reads the bearer token from the `Authorization` header.
2. Reads the JWT `kid` to select a public key from Keycloak's JWKS endpoint.
3. Verifies the JWT signature.
4. Verifies the issuer and token lifetime.
5. Creates an authenticated Spring Security principal.

No Keycloak client secret is required for this validation. The API verifies tokens using Keycloak's public keys.

Current endpoint rules:

- `GET /api/business/public` — public.
- `POST /api/users/register` — public for now.
- `GET /api/business/secure` — requires a valid Keycloak access token.
- `PUT /api/users/{id}/profile` — requires a valid Keycloak access token.
- Any other endpoint — authenticated by default.

Roles and audience validation are not configured yet. At this stage, any valid access token issued by the `hands-on` realm can call authenticated endpoints.

## 6. Get an access token

Request a token from Keycloak:

```bash
curl -s -X POST \
  http://localhost:8080/realms/hands-on/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=identity-service-client' \
  -d 'username=testuser' \
  -d 'password=password123'
```

The response contains `access_token`, `refresh_token`, `expires_in`, and other OpenID Connect fields.

If `jq` is installed, save the access token:

```bash
TOKEN=$(curl -s -X POST \
  http://localhost:8080/realms/hands-on/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=identity-service-client' \
  -d 'username=testuser' \
  -d 'password=password123' | jq -r '.access_token')
```

## 7. Test the APIs

The public endpoint works without a token:

```bash
curl -i http://localhost:8081/api/business/public
```

The secure endpoint returns `401 Unauthorized` without a token:

```bash
curl -i http://localhost:8081/api/business/secure
```

The secure endpoint returns `200 OK` with the access token:

```bash
curl -i http://localhost:8081/api/business/secure \
  -H "Authorization: Bearer $TOKEN"
```

An invalid or expired token returns `401 Unauthorized`. A valid token without a required role will return `403 Forbidden` after role-based authorization is added.

