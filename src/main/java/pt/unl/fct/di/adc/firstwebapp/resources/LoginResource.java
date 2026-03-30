package pt.unl.fct.di.adc.firstwebapp.resources;

import org.apache.commons.codec.digest.DigestUtils;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.unl.fct.di.adc.firstwebapp.util.auth.AuthToken;
import pt.unl.fct.di.adc.firstwebapp.util.auth.ErrorResponse;
import pt.unl.fct.di.adc.firstwebapp.util.login.LoginRequest;
import pt.unl.fct.di.adc.firstwebapp.util.login.LoginResponse;

import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.DatastoreOptions;

import com.google.gson.Gson;


@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

    private static final String MESSAGE_INVALID_CREDENTIALS = "The username-password pair is not valid";
    private static final String ERROR_INVALID_CREDENTIALS = "9900";

    private static final String MESSAGE_INVALID_INPUT = "The call is using input data not following the correct specification";
    private static final String ERROR_INVALID_INPUT = "9906";

    private static final String MESSAGE_USER_NOT_FOUND = "The username referred in the operation doesn’t exist in registered accounts";
    private static final String ERROR_USER_NOT_FOUND = "9902";

    private static final String USER_PWD = "user_pwd";

    private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private static final KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

    private final Gson g = new Gson();

    public LoginResource() {} // Nothing to be done here

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response doLogin(LoginRequest request) {
        if (request == null || request.input == null ||
        request.input.username == null || request.input.username.isBlank() ||
        request.input.password == null || request.input.password.isBlank()) {
            ErrorResponse error = new ErrorResponse(ERROR_INVALID_INPUT, MESSAGE_INVALID_INPUT);
            return Response.ok(g.toJson(error)).build();
        }

        Key userKey = userKeyFactory.newKey(request.input.username);
        Entity user = datastore.get(userKey);

        if(user != null) {
            String hashedPWD = user.getString(USER_PWD);
            if(hashedPWD.equals(DigestUtils.sha512Hex(request.input.password))) {
                String role = user.getString("user_role");
                AuthToken at = new AuthToken(request.input.username, role);
                Key tokenKey = datastore.newKeyFactory().setKind("AuthToken").newKey(at.tokenId);
                Entity tokenEntity = Entity.newBuilder(tokenKey)
                .set("username", at.username)
                .set("role", at.role)
                .set("issuedAt", at.issuedAt)
                .set("expiresAt", at.expiresAt)
                .build();
                datastore.put(tokenEntity);
                LoginResponse response = new LoginResponse(at);
                return Response.ok(g.toJson(response)).build();
            }
            else {
                ErrorResponse error = new ErrorResponse(ERROR_INVALID_CREDENTIALS, MESSAGE_INVALID_CREDENTIALS);
                return Response.ok(g.toJson(error)).build();
            }
        }
        else {
            ErrorResponse error = new ErrorResponse(ERROR_USER_NOT_FOUND, MESSAGE_USER_NOT_FOUND);
            return Response.ok(g.toJson(error)).build();
        }
    }
}
