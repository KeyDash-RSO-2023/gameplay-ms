package si.fri.rso.samples.imagecatalog.api.v1.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import si.fri.rso.samples.imagecatalog.services.config.RestProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class CustomHealthCheck implements HealthCheck {

    @Inject
    private RestProperties restProperties;

    @Override
    public HealthCheckResponse call() {
        if (restProperties.getBroken()) {
            System.out.println("Custom liveness healthcheck passed.");
            return HealthCheckResponse.down(CustomHealthCheck.class.getSimpleName());
        }
        else {
            System.out.println("Custom liveness healthcheck failed.");
            return HealthCheckResponse.up(CustomHealthCheck.class.getSimpleName());
        }
    }
}
