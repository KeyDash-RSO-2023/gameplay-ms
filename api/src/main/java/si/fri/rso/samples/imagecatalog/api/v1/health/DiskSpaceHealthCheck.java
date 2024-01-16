package si.fri.rso.samples.imagecatalog.api.v1.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;

@Liveness
@ApplicationScoped
public class DiskSpaceHealthCheck implements HealthCheck {

    private static final long THRESHOLD = 1024 * 1024 * 100; // 100 MB

    @Override
    public HealthCheckResponse call() {
        File disk = new File(".");
        long freeSpace = disk.getUsableSpace();

        if (freeSpace >= THRESHOLD) {
            return HealthCheckResponse.named("diskSpace").up().withData("freeSpaceInBytes", freeSpace).build();
        } else {
            return HealthCheckResponse.named("diskSpace").down().withData("freeSpaceInBytes", freeSpace).build();
        }
    }
}
