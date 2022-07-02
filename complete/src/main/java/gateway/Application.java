package gateway;

import org.springframework.util.Base64Utils;
import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// tag::code[]
@SpringBootApplication
@EnableConfigurationProperties(UriConfiguration.class)
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // tag::route-locator[]
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
//        String httpUri = uriConfiguration.getHttpbin();
//        String finerAcrUrl = uriConfiguration.getHttpfin();
        RouteLocator routerLocator = builder.routes()
                .route(p -> p
                        .path("/**")
                        .filters(f ->
                                f.addRequestHeader("Fineract-Platform-TenantId", "default")
                                        .addRequestHeader("Authorization", String.format("%s", getCredentials("mifos", "password")))
                                        .addRequestHeader("Content-Type", "application/json; charset=utf8"))
                        .uri("https://api.lnddo.loan"))
//				.route(p -> p
//						.path("/**")
//						.filters(f -> f.addRequestHeader("X-RapidAPI-Key", "8114e06e6amsh1703b16c92d7c73p19b91bjsnfbd5b17004ab")
//								.addRequestHeader("X-RapidAPI-Host", "weatherbit-v1-mashape.p.rapidapi.com"))
//						.uri("https://weatherbit-v1-mashape.p.rapidapi.com/"))
                .build();
        return routerLocator;
    }

    private String getCredentials(String username, String password) {
        byte[] encodedBytes = Base64Utils.encode((username + ":" + password).getBytes());
        return "Basic " + new String(encodedBytes);
    }
    // end::route-locator[]

    // tag::fallback[]
    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("fallback");
    }
    // end::fallback[]
}

// tag::uri-configuration[]
@ConfigurationProperties
class UriConfiguration {

    private String httpbin = "https://dashboard-qa.htagdev.info/";
    private String finerAcrUrl = "https://api.lnddo.loan/";

    public String getHttpbin() {
        return httpbin;
    }

    public String getHttpfin() {
        return finerAcrUrl;
    }

    public void setHttpbin(String httpbin) {
        this.httpbin = httpbin;
    }
}
// end::uri-configuration[]
// end::code[]
