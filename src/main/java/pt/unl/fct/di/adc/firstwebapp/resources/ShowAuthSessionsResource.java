package pt.unl.fct.di.adc.firstwebapp.resources;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.firstwebapp.util.SessionInfo;
import pt.unl.fct.di.adc.firstwebapp.util.auth.ErrorResponse;
import pt.unl.fct.di.adc.firstwebapp.util.showauthsessions.ShowAuthSessionsRequest;
import pt.unl.fct.di.adc.firstwebapp.util.showauthsessions.ShowAuthSessionsResponse;

@Path("/showauthsessions")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ShowAuthSessionsResource {

    private static final String MESSAGE_INVALID_TOKEN = "The operation is called with an invalid token (wrong format for example)";
    private static final String ERROR_INVALID_TOKEN = "9903";

    private static final String MESSAGE_TOKEN_EXPIRED = "The operation is called with a token that is expired";
    private static final String ERROR_TOKEN_EXPIRED = "9904";

    private static final String MESSAGE_UNAUTHORIZED = "The operation is not allowed for the user role";
    private static final String ERROR_UNAUTHORIZED = "9905";

    private static final String MESSAGE_INVALID_INPUT = "The call is using input data not following the correct specification";
    private static final String ERROR_INVALID_INPUT = "9906";

    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private final Gson g = new Gson();

    public ShowAuthSessionsResource() {}

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response showSessions(ShowAuthSessionsRequest request) {
        if (request == null || request.input == null || request.token == null ||
        request.token.tokenId == null || request.token.tokenId.isBlank() ||
        request.token.username == null || request.token.username.isBlank() ||
        request.token.role == null || request.token.role.isBlank()) {
            ErrorResponse error = new ErrorResponse(ERROR_INVALID_INPUT, MESSAGE_INVALID_INPUT);
            return Response.ok(g.toJson(error)).build();
        }

        Key tokenKey = datastore.newKeyFactory().setKind("AuthToken").newKey(request.token.tokenId);

        Entity tokenEntity = datastore.get(tokenKey);

        if (tokenEntity == null) {
            ErrorResponse error = new ErrorResponse(ERROR_INVALID_TOKEN, MESSAGE_INVALID_TOKEN);
            return Response.ok(g.toJson(error)).build();
        }

        long now = System.currentTimeMillis() / 1000;
        if (request.token.expiresAt < now) {
            ErrorResponse error = new ErrorResponse(ERROR_TOKEN_EXPIRED, MESSAGE_TOKEN_EXPIRED);
            return Response.ok(g.toJson(error)).build();
        }
        long expiresAt = tokenEntity.getLong("expiresAt");

        if (expiresAt < now) {
            ErrorResponse error = new ErrorResponse(ERROR_TOKEN_EXPIRED, MESSAGE_TOKEN_EXPIRED);
            return Response.ok(g.toJson(error)).build();
        }

        String tokenUsername = tokenEntity.getString("username");
        String tokenRole = tokenEntity.getString("role");
        long issuedAt = tokenEntity.getLong("issuedAt");

        if (!request.token.username.equals(tokenUsername) || !request.token.role.equals(tokenRole)
        || request.token.issuedAt != issuedAt || request.token.expiresAt != expiresAt) {
            ErrorResponse error = new ErrorResponse(ERROR_INVALID_TOKEN, MESSAGE_INVALID_TOKEN);
            return Response.ok(g.toJson(error)).build();
        }

        if (!tokenRole.equals("ADMIN")) {
            ErrorResponse error = new ErrorResponse(ERROR_UNAUTHORIZED, MESSAGE_UNAUTHORIZED);
            return Response.ok(g.toJson(error)).build();
        }

        Query<Entity> query = Query.newEntityQueryBuilder().setKind("AuthToken").build();

        QueryResults<Entity> results = datastore.run(query);
        List<SessionInfo> sessions = new ArrayList<>();

        while (results.hasNext()) {
            Entity session = results.next();
            SessionInfo info = new SessionInfo(
                session.getKey().getName(),
                session.getString("username"),
                session.getString("role"),
                session.getLong("expiresAt"));
            sessions.add(info);
        }

        ShowAuthSessionsResponse response = new ShowAuthSessionsResponse(sessions);
        return Response.ok(g.toJson(response)).build();
    }
}
