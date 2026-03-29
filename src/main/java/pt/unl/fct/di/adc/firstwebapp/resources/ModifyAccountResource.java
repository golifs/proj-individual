package pt.unl.fct.di.adc.firstwebapp.resources;

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
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.adc.firstwebapp.util.auth.ErrorResponse;
import pt.unl.fct.di.adc.firstwebapp.util.modaccount.ModifyAccountRequest;
import pt.unl.fct.di.adc.firstwebapp.util.modaccount.ModifyAccountResponse;

@Path("/modaccount")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ModifyAccountResource {

    private static final String MESSAGE_USER_NOT_FOUND =
            "The username referred in the operation doesn’t exist in registered accounts";
    private static final String ERROR_USER_NOT_FOUND = "9902";

    private static final String MESSAGE_INVALID_TOKEN =
            "The operation is called with an invalid token (wrong format for example)";
    private static final String ERROR_INVALID_TOKEN = "9903";

    private static final String MESSAGE_TOKEN_EXPIRED =
            "The operation is called with a token that is expired";
    private static final String ERROR_TOKEN_EXPIRED = "9904";

    private static final String MESSAGE_UNAUTHORIZED =
            "The operation is not allowed for the user role";
    private static final String ERROR_UNAUTHORIZED = "9905";

    private static final String MESSAGE_INVALID_INPUT =
            "The call is using input data not following the correct specification";
    private static final String ERROR_INVALID_INPUT = "9906";

    private static final Datastore datastore =
DatastoreOptions.getDefaultInstance().getService();

    private final Gson g = new Gson();

    public ModifyAccountResource() { }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modifyAccount(ModifyAccountRequest request) {

        if (request == null || request.input == null || request.token == null ||
            request.input.username == null || request.input.username.isBlank() ||
            request.input.attributes == null ||
            request.token.tokenId == null || request.token.tokenId.isBlank() ||
            request.token.username == null || request.token.username.isBlank() ||
            request.token.role == null || request.token.role.isBlank() ||
            ((request.input.attributes.phone == null || request.input.attributes.phone.isBlank())
&&
             (request.input.attributes.address == null ||
request.input.attributes.address.isBlank()))) {
            ErrorResponse error = new ErrorResponse(ERROR_INVALID_INPUT, MESSAGE_INVALID_INPUT);
            return Response.ok(g.toJson(error)).build();
        }

        Key tokenKey = datastore.newKeyFactory()
                .setKind("AuthToken")
                .newKey(request.token.tokenId);

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

        if (!request.token.username.equals(tokenUsername)
                || !request.token.role.equals(tokenRole)
                || request.token.issuedAt != issuedAt
                || request.token.expiresAt != expiresAt) {
            ErrorResponse error = new ErrorResponse(ERROR_INVALID_TOKEN, MESSAGE_INVALID_TOKEN);
            return Response.ok(g.toJson(error)).build();
        }

        Key targetUserKey = datastore.newKeyFactory()
                .setKind("User")
                .newKey(request.input.username);

        Transaction txn = datastore.newTransaction();
        try {
            Entity targetUser = txn.get(targetUserKey);

            if (targetUser == null) {
                txn.rollback();
                ErrorResponse error = new ErrorResponse(ERROR_USER_NOT_FOUND,
MESSAGE_USER_NOT_FOUND);
                return Response.ok(g.toJson(error)).build();
            }

            String targetRole = targetUser.getString("user_role");

            boolean allowed = false;

            if (tokenRole.equals("ADMIN")) {
                allowed = true;
            } else if (tokenRole.equals("USER")) {
                allowed = request.input.username.equals(tokenUsername);
            } else if (tokenRole.equals("BOFFICER")) {
                allowed = request.input.username.equals(tokenUsername) ||
targetRole.equals("USER");
            }

            if (!allowed) {
                txn.rollback();
                ErrorResponse error = new ErrorResponse(ERROR_UNAUTHORIZED,
MESSAGE_UNAUTHORIZED);
                return Response.ok(g.toJson(error)).build();
            }

            Entity.Builder updatedUser = Entity.newBuilder(targetUser);

            if (request.input.attributes.phone != null && !
request.input.attributes.phone.isBlank()) {
                updatedUser.set("user_phone", request.input.attributes.phone);
            }

            if (request.input.attributes.address != null && !
request.input.attributes.address.isBlank()) {
                updatedUser.set("user_address", request.input.attributes.address);
            }

            txn.put(updatedUser.build());
            txn.commit();

            ModifyAccountResponse response = new ModifyAccountResponse();
            return Response.ok(g.toJson(response)).build();

        } catch (Exception e) {
            if (txn.isActive()) {
                txn.rollback();
            }
            ErrorResponse error = new ErrorResponse("9907",
                    "The operation generated a forbidden error by other reason");
            return Response.ok(g.toJson(error)).build();
        }
    }
}
