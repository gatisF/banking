# Banking

Your task is to develop a simple RESTful web service that would satisfy a set of functional
requirements, as well as a list of non-functional requirements. Please note these non-functional
requirements are given in order of importance; items appearing earlier in the list are more crucial for
assignment.<br />

Functional requirements:<br />
	● Service should expose an HTTP API providing the following functionality:<br />
		○ Given a client identifier, return a list of accounts (each client might have 0 or more
			accounts with different currencies)<br />
		○ Given an account identifier, return transaction history (last transactions come first)
			and support result paging using “offset” and “limit” parameters<br />
		○ Transfer funds between two accounts indicated by identifiers<br />
	● Balance must always be positive (>= 0).<br />
	● Currency conversion should take place when transferring funds between accounts with
		different currencies<br />
		○ For currency exchange rates, you can use any service of your choice, e.g.
			https://api.exchangerate.host/latest<br />
		○ You may limit the currencies supported by your implementation based on what the
			currency exchange rate service supports<br />
		○ The currency of funds in the transfer operation must match the receiver's account
			currency (e.g. system should return an error when requesting to transfer 30 GBP
			from a USD account to a EUR account, however transferring 30 GBP from USD to
			GBP is a valid operation - corresponding amount of USD is exchanged to GBP and
			credited to GBP account).<br />
			
Non-functional requirements:<br />
As mentioned previously, the following list is given in order of priority, you may implement only part of
the items (more is better, however).
1. Test coverage should be not less than 80%
2. Implemented web service should be resilient to 3rd party service unavailability
3. DB schema versioning should be implemented

## Development

During development it is recommended to use the profile `local`. In IntelliJ `-Dspring.profiles.active=local` can be
added in the VM options of the Run Configuration after enabling this property in "Modify options".

Lombok must be supported by your IDE. For IntelliJ install the Lombok plugin and enable annotation processing -
[learn more](https://bootify.io/next-steps/spring-boot-with-lombok.html).

After starting the application it is accessible under `http://localhost:8080/swagger-ui/index.html`.

## Build

The application can be built using the following command:

```
gradlew clean build
```

Start your application with the following command - here with the profile `local`:

```
java -Dspring.profiles.active=local -jar ./build/libs/banking-0.0.1-SNAPSHOT.jar
```

If required, a Docker image can be created with the Spring Boot plugin. Add `SPRING_PROFILES_ACTIVE=local` as
environment variable when running the container.

```
gradlew bootBuildImage --imageName=io.mintos/banking
```

## Further readings

* [Gradle user manual](https://docs.gradle.org/)  
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)  
* [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)  
