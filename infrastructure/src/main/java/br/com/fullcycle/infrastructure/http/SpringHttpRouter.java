package br.com.fullcycle.infrastructure.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Optional;

public class SpringHttpRouter implements HttpRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringHttpRouter.class);

    private final RouterFunctions.Builder router;

    public SpringHttpRouter() {
        this.router = RouterFunctions.route();
    }

    public RouterFunctions.Builder getRouter() {
        return router;
    }

    @Override
    public <T> HttpRouter POST(String pattern, HttpHandler<T> handler) {
        this.router.POST(pattern, wrapHandler(pattern, handler));

        return this;
    }

    @Override
    public <T> HttpRouter GET(String pattern, HttpHandler<T> handler) {
        this.router.GET(pattern, wrapHandler(pattern, handler));

        return this;
    }

    private static <T> HandlerFunction<ServerResponse> wrapHandler(String pattern, HttpHandler<T> handler) {
        return req -> {
            try {
                var res = handler.handle(new SpringHttpRequest(req));
                return ServerResponse.status(res.statusCode())
                        .headers(headers -> res.headers().forEach(headers::add))
                        .body(res.body());
            } catch (Throwable error) {
                LOGGER.error("Unexpected error at %s".formatted(pattern), error);
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
            }
        };
    }

    public record SpringHttpRequest(ServerRequest request) implements HttpRequest {

        @Override
        public <T> T body(final Class<T> tClass) {
            try {
                return request.body(tClass);
            } catch (Throwable error) {
                throw new RuntimeException(error);
            }
        }

        @Override
        public String pathParams(String name) {
            return request.pathVariable(name);
        }

        @Override
        public Optional<String> queryParams(String name) {
            return request.param(name);
        }
    }
}
