package si.fri.rso.samples.imagecatalog.api.v1.resources;

import com.kumuluz.ee.logs.LogManager;
//import com.kumuluz.ee.logs.Logger;
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
import si.fri.rso.samples.imagecatalog.models.models.TypingSessionInput;
import si.fri.rso.samples.imagecatalog.services.beans.TypingSessionBean;
import si.fri.rso.samples.imagecatalog.services.services.GeneratorClient;
import si.fri.rso.samples.imagecatalog.services.services.ReportClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.Duration;
import java.time.Instant;
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

    private Logger logger = Logger.getLogger(GameplayResources.class.getName());
//private static final Logger LOG = LogManager.getLogger(GameplayResources.class.getName());

    @Inject
    private TypingSessionBean typingSessionBean;


    @Context
    protected UriInfo uriInfo;

    // Create an instance of the GeneratorClient
    private GeneratorClient generatorClient = new GeneratorClient("http://20.240.34.248/generator");

    private ReportClient reportClient = new ReportClient("http://20.240.34.248/reports");

    @GET
    @Path("/get/{typingSessionId}")
    @Produces(MediaType.APPLICATION_JSON) // This annotation specifies that the response will be in JSON format.
    public Response getTypingSessionRecords(@PathParam("typingSessionId") long typingSessionId) {


        List<TypingSession> tss = typingSessionBean.getAllRecordsForTypingSession(typingSessionId);

        return typingSessionArrayResponse(tss);
    }

    @GET
    @Path("/new")
    @Operation(summary = "Create New Typing Session",
            description = "Starts a new typing session with specified parameters.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "New typing session created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TypingSession.class))
            )
    })
    @Produces(MediaType.APPLICATION_JSON) // This annotation specifies that the response will be in JSON format.
    public Response getNewTypingSession(@QueryParam("language") String language, @QueryParam("length") int length, @QueryParam("punctuation") boolean punctuation) {
        logger.fine("Well a new typing session was requested and this is a trace.");
        logger.warning("this is a warn");
        logger.info("this is a info");
        // Retrieve the random text to type
        String textToType = generatorClient.getTextToType(language, length, punctuation);

        // Create a new TypingSession object and set the text to type
        TypingSession ts = new TypingSession();
        ts.setLanguage(language);
        ts.setLength(length);
        ts.setPunctuation(punctuation);
        ts.setTextToType(textToType);

        ts = typingSessionBean.createTypingSession(ts);

        return typingSessionResponse(ts);
    }

    @POST
    @Operation(summary = "End Typing Session",
            description = "Ends a typing session and processes the results.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Typing session ended successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TypingSession.class))
            ),
            @APIResponse(responseCode = "400", description = "Bad request, session already ended or invalid"),
                @APIResponse(responseCode = "404", description = "Typing session not found")
    })
    @Path("/end/{typingSessionId}")
    public Response endTypingSession(@PathParam("typingSessionId") long typingSessionId, @RequestBody TypingSessionInput input) {
        TypingSession ts = typingSessionBean.getTypingSession(typingSessionId);

        System.out.println("Ending session with status " + ts.getStatus());
        if (ts.getStatus() != "in progress") {
            System.out.println("Not saving session, which was invalidated or cancelled");
            return Response.status(Response.Status.BAD_REQUEST).entity("Typing session was invalidated.").build();
        }

        if (ts == null) {
            // If the session doesn't exist, return a NOT FOUND response
            return Response.status(Response.Status.NOT_FOUND).entity("Typing session not found.").build();
        }

        TypingSession endedTs = typingSessionBean.endTypingSession(ts);

        reportClient.saveTypingSession(endedTs, input.getUserId());

        return typingSessionResponse(endedTs);

    }

    @POST
    @Path("/update/{typingSessionId}")
    @Operation(summary = "Update Typing Session",
            description = "Anti-hack detection system validating the session ant updating the WPM.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Typing session updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TypingSession.class))
            ),
            @APIResponse(responseCode = "400", description = "Typing session invalidated due to anti-hack"),
            @APIResponse(responseCode = "404", description = "Typing session not found")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTypingSession(@PathParam("typingSessionId") long typingSessionId, TypingSessionProgress progress) {
        // Retrieve the existing typing session
        TypingSession ts = typingSessionBean.getTypingSession(typingSessionId);


        if (ts == null) {
            // If the session doesn't exist, return a NOT FOUND response
            return Response.status(Response.Status.NOT_FOUND).entity("Typing session not found.").build();
        }

        // Perform validation checks
        boolean isValid = true;
        if (ts.getLastUpdateTime() != null) {
            isValid = validateProgress(ts, progress);
        }

        TypingSession updatedTs = typingSessionBean.updateTypingSession(ts, isValid, progress.getCurrentWpm(), progress.getAccuracy());

        if (!isValid) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Session invalidated due to irregular activity").build();
        }

        return typingSessionResponse(updatedTs);
    }

    private boolean validateProgress(TypingSession typingSession, TypingSessionProgress progress) {
        // Check if the last update was within the last 5 seconds
        Instant now = Instant.now();
        Duration timeSinceLastUpdate = Duration.between(typingSession.getLastUpdateTime(), now);
        Duration timeSinceStart = Duration.between(typingSession.getStartTime(), now);

        if (timeSinceStart.toMillis() < 5000) {
            return true;
        }
        if (timeSinceLastUpdate.toMillis() > 7500) {
            System.out.println("More time passed since last update than allowed, invalidating the session.");
            return false;
        }

        // Calculate the number of correctly typed characters
        String textToType = typingSession.getTextToType();
        String typedText = progress.getTypedText();
        int correctCharCount = 0;

        for (int i = 0; i < Math.min(textToType.length(), typedText.length()); i++) {
            if (textToType.charAt(i) == typedText.charAt(i)) {
                correctCharCount++;
            }
        }
        // Calculate WPM based on correctly typed characters
        double wordsPerCharacter = 1 / 4.7; // Assuming an average word length of 4.7 characters
        double numberOfWords = correctCharCount * wordsPerCharacter;
        double calculatedWpm = (numberOfWords / (timeSinceStart.toMillis() / 1000.0)) * 60; // Convert milliseconds to seconds for WPM calculation


        // Check if the reported WPM is within an acceptable range
        double allowedWpmVariance = 5.0; // Allow a variance of 5 WPM
        double minAllowedWpm = progress.getCurrentWpm() - allowedWpmVariance;
        double maxAllowedWpm = progress.getCurrentWpm() + allowedWpmVariance;

//        System.out.println("Time since start " + timeSinceStart.toMillis());
//        System.out.println("Characters typed " +  correctCharCount);
//        System.out.println(calculatedWpm);
//        System.out.println(minAllowedWpm);
//        System.out.println(maxAllowedWpm);

        if (calculatedWpm < minAllowedWpm || calculatedWpm > maxAllowedWpm) {
            return false;
        }

        return true;
    }


    @POST
    @Operation(summary = "Cancel Typing Session",
            description = "Cancels a typing session.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Typing session cancelled successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TypingSession.class))
            ),
            @APIResponse(responseCode = "404", description = "Typing session not found")
    })
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

    public Response typingSessionArrayResponse(List<TypingSession> tss) {
        Map<String, Object> responseMap = new HashMap<>();

        for (int i = 0; i < tss.size(); i++) {
            TypingSession ts = tss.get(i);
            Map<String, Object> tsMap = new HashMap<>();
            tsMap.put("typingSessionId", ts.getTypingSessionId());
            tsMap.put("textToType", ts.getTextToType());
            tsMap.put("startTime", ts.getStartTime());
            tsMap.put("endTime", ts.getEndTime());
            tsMap.put("status", ts.getStatus());
            responseMap.put(String.valueOf(i+1), ts);
        }
        // Create a map to hold the desired properties.


        // Build the response with the map.
        return Response
                .status(Response.Status.OK)
                .entity(responseMap)
                .build();
    }

}
