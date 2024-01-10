package si.fri.rso.samples.imagecatalog.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.imagecatalog.lib.TypingSession;
import si.fri.rso.samples.imagecatalog.lib.TypingSessionProgress;
import si.fri.rso.samples.imagecatalog.services.beans.TypingSessionBean;
import si.fri.rso.samples.imagecatalog.services.services.GeneratorClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@Log
@ApplicationScoped
@Path("/gameplay")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GameplayResources {

    private Logger log = Logger.getLogger(GameplayResources.class.getName());

    @Inject
    private TypingSessionBean typingSessionBean;


    @Context
    protected UriInfo uriInfo;

    // Create an instance of the GeneratorClient
    private GeneratorClient generatorClient = new GeneratorClient("http://localhost:807");

    private int MAX_ALLOWED_WPM = 80;


    @GET
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON) // This annotation specifies that the response will be in JSON format.
    public Response getNewTypingSession(@QueryParam("language") String language, @QueryParam("length") int length, @QueryParam("punctuation") boolean punctuation) {

        // Retrieve the random text to type
        String textToType = generatorClient.getTextToType(language, length, punctuation);

        // Create a new TypingSession object and set the text to type
        TypingSession ts = new TypingSession();
        ts.setTextToType(textToType);

        ts = typingSessionBean.createTypingSession(ts);

        return typingSessionResponse(ts);
    }

    @POST
    @Path("/end/{typingSessionId}")
    public Response endTypingSession(@PathParam("typingSessionId") long typingSessionId, @RequestBody String typedText) {
        TypingSession ts = typingSessionBean.getTypingSession(typingSessionId);

        if (ts == null) {
            // If the session doesn't exist, return a NOT FOUND response
            return Response.status(Response.Status.NOT_FOUND).entity("Typing session not found.").build();
        }

        TypingSession endedTs = typingSessionBean.endTypingSession(ts);
        return typingSessionResponse(endedTs);

    }

    @POST
    @Path("/update/{typingSessionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTypingSession(@PathParam("typingSessionId") long typingSessionId, TypingSessionProgress progress) {
        // Retrieve the existing typing session from the database

        TypingSession ts = typingSessionBean.getTypingSession(typingSessionId);

        if (ts == null) {
            // If the session doesn't exist, return a NOT FOUND response
            return Response.status(Response.Status.NOT_FOUND).entity("Typing session not found.").build();
        }

        // Perform validation checks
        boolean isValid = validateProgress(ts, progress);

        TypingSession updatedTs = typingSessionBean.updateTypingSession(ts, isValid, progress.getCurrentWpm(), progress.getAccuracy());

        if (!isValid) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Session invalidated due to irregular activity").build();
        }

        return typingSessionResponse(updatedTs);
    }

    private boolean validateProgress(TypingSession typingSession, TypingSessionProgress progress) {
        if (progress.getCurrentWpm() > MAX_ALLOWED_WPM) {
            return false;
        }
        return true;
    }

    @POST
    @Path("/cancel/{typingSessionId}")
    public Response cancelTypingSession(@PathParam("typingSessionId") long typingSessionId) {

        TypingSession ts = typingSessionBean.getTypingSession(typingSessionId);

        if (ts == null) {
            // If the session doesn't exist, return a NOT FOUND response
            return Response.status(Response.Status.NOT_FOUND).entity("Typing session not found.").build();
        }

        TypingSession cancelledTs = typingSessionBean.cancelTypingSession(ts);
        return typingSessionResponse(cancelledTs);
    }

    public Response typingSessionResponse(TypingSession ts) {
        // Create a map to hold the desired properties.
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("typingSessionId", ts.getTypingSessionId());
        responseMap.put("textToType", ts.getTextToType());
        responseMap.put("startTime", ts.getStartTime());
        responseMap.put("endTime", ts.getEndTime());
        responseMap.put("status", ts.getStatus());

        // Build the response with the map.
        return Response
                .status(Response.Status.OK)
                .entity(responseMap)
                .build();
    }

}
