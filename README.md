# ADC Individual Project

  Individual project for the ADC-Project course.

  ## Tech
  - Java 21
  - Maven
  - Google App Engine
  - Google Cloud Datastore
  - Jersey REST

  ## Main Endpoints
  All endpoints use `POST` under `/rest/`.

  - `/createaccount`
  - `/login`
  - `/logout`
  - `/showusers`
  - `/showauthsessions`
  - `/showuserrole`
  - `/changeuserrole`
  - `/changeuserpwd`
  - `/modaccount`
  - `/deleteaccount`

  ## Run
  Compile:
  ```bash
  mvn clean compile

  Run locally:

  mvn appengine:run

  Deploy:

  mvn appengine:deploy

  ## Notes

  - Authentication is based on tokens stored in Datastore.
  - All application errors are returned in JSON with a status code and data message.
