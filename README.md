# CC911 Example App Service

This is an example Application Service for the CC911 product team to use as inspiration 
for how to approach some of the common problems in building Application Services.

This example is built to illustrate base concepts for building services, but also uses 
Java and the Spring framework to implement the patterns.

It is intended that the concepts demonstrated in this application can be used to 
implement similar concerns in other languages, and that the Java code here may be 
reused with other frameworks to sole those concerns where appropriate.

## Authorization

The Authorization code in this example illustrates how to perform a token verification 
against the Ping Identity servers for the CC product suite, and uses the CC Admin API 
for obtaining information about the successfully authenticated user's permissions.

The general purpose Java implementation of the authorization concerns is in the
`com.msi.cc911.auth` package, with the `AccessTokenVerifier` class providing the 
illustrative implementation of the primary concerns.

The integration with the Spring Framework is primarily realized in the 
`JWTAuthorizationFilter` class, and integrates CC privileges with the Spring security 
role enforcement mechanisms, as illustrated with the various routes in 
`GreetingController`.

Please update the application.yml with appropriate clientId and clientSecret values 
as well as with the URL locations for the appropriate identity provider for your 
use case and application so that it functions properly.