# mejsla-kotlin

Code from Mejsla's Kotlin Study-group.

This project implements a mashup service that loads health inspection
data for different food services in Stockholm, and combines it with
address data from [PAP/API](https://www.papapi.se).

After building and deploying the service you can call it like this:
http://localhost:8080/inspections?street=Kungsgatan.

The service uses
[Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)
and [Kotlin coroutines](https://github.com/Kotlin/kotlinx.coroutines)
to implement a reactive API. The
[spring-kotlin-coroutine](https://github.com/konrad-kaminski/spring-kotlin-coroutine)
library is used to bridge the gap between WebFlux and coroutines.
