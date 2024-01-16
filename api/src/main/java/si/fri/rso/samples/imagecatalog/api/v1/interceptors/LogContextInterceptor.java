package si.fri.rso.samples.imagecatalog.api.v1.interceptors;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.runtime.EeRuntime;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.logs.cdi.Log;
import org.apache.logging.log4j.CloseableThreadContext;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.HashMap;
import java.util.UUID;

@Log
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
public class LogContextInterceptor {

    @AroundInvoke
    public Object logMethodEntryAndExit(InvocationContext context) throws Exception {

        // Debug: Print information about the intercepted method
        System.out.println("Intercepting method: " + context.getMethod().getName());

        String methodName = context.getMethod().getName();
        ConfigurationUtil configurationUtil = ConfigurationUtil.getInstance();

        HashMap<String, String> settings = new HashMap<>();

        settings.put("environmentType", EeConfig.getInstance().getEnv().getName());
        settings.put("applicationName", EeConfig.getInstance().getName());
        settings.put("applicationVersion", EeConfig.getInstance().getVersion());
        settings.put("uniqueInstanceId", EeRuntime.getInstance().getInstanceId());
        settings.put("uniqueRequestId", UUID.randomUUID().toString());

        // Debug: Print the settings to be added to the logging context
        System.out.println("Logging context settings: " + settings);

        try (final CloseableThreadContext.Instance ctc = CloseableThreadContext.putAll(settings)) {
            // Debug: Print a message before proceeding with the method invocation
            System.out.println("Before proceeding with the intercepted method.");

            Object result = context.proceed();

            // Debug: Print a message after the method invocation
            System.out.println("After proceeding with the intercepted method.");

            return result;
        } catch (Exception e) {
            // Debug: Print the exception if something goes wrong
            System.out.println("Exception in intercepted method: " + e.getMessage());
            throw e;
        } finally {
            // Debug: Print a message when the logging context is closed
            System.out.println("Logging context closed.");
        }
    }
}
