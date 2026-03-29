package pt.unl.fct.di.adc.firstwebapp.resources;

import org.apache.commons.codec.digest.DigestUtils;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.DatastoreOptions;

import pt.unl.fct.di.adc.firstwebapp.util.auth.ErrorResponse;
import pt.unl.fct.di.adc.firstwebapp.util.createaccount.CreateAccountRequest;
import pt.unl.fct.di.adc.firstwebapp.util.createaccount.CreateAccountResponse;

@Path("/createaccount")
public class CreateAccountResource {

	private static final String EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
		
	private static final String MESSAGE_USER_ALREADY_EXISTS = "Error in creating an account because the username already exists";
	private static final String ERROR_USER_ALREADY_EXISTS = "9901";
	
	private static final String MESSAGE_INVALID_INPUT = "The call is using input data not following the correct specification";
	private static final String ERROR_INVALID_INPUT = "9906";
	
	private static final String MESSAGE_FORBIDDEN = "The operation generated a forbidden error by other reason";
	private static final String ERROR_FORBIDDEN = "9907";

	private static final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	private final Gson g = new Gson();


	public CreateAccountResource() {}	// Default constructor, nothing to do

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(CreateAccountRequest request) {
	    	if (request == null || request.input == null ||
	    		request.input.username == null || request.input.username.isBlank() ||
	    		request.input.password == null || request.input.password.isBlank() ||
	    		request.input.confirmation == null || request.input.confirmation.isBlank() ||
	    		request.input.phone == null || request.input.phone.isBlank() ||
	    		request.input.address == null || request.input.address.isBlank() ||
	    		request.input.role == null || request.input.role.isBlank() ||
	    		!request.input.password.equals(request.input.confirmation)) {
	    		ErrorResponse error = new ErrorResponse(ERROR_INVALID_INPUT, MESSAGE_INVALID_INPUT);
	    		return Response.ok(g.toJson(error)).build();
	    	}
	    	if (!request.input.username.matches(EMAIL_REGEX)) {
	    		ErrorResponse error = new ErrorResponse(ERROR_INVALID_INPUT, MESSAGE_INVALID_INPUT);
	    		return Response.ok(g.toJson(error)).build();
	    	}
			if (!(request.input.role.equals("USER")
					|| request.input.role.equals("BOFFICER")
					|| request.input.role.equals("ADMIN"))) {
			ErrorResponse error = new ErrorResponse(ERROR_INVALID_INPUT, MESSAGE_INVALID_INPUT);
			return Response.ok(g.toJson(error)).build();
		}
        try {
            Transaction txn = datastore.newTransaction();
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(request.input.username);
            Entity user = txn.get(userKey);

            if(user != null) {
                txn.rollback();
                ErrorResponse error = new ErrorResponse(ERROR_USER_ALREADY_EXISTS, MESSAGE_USER_ALREADY_EXISTS);
                return Response.ok(g.toJson(error)).build();
            }            
            else {
            	user = Entity.newBuilder(userKey)
            	          .set("user_pwd", DigestUtils.sha512Hex(request.input.password))
            	          .set("user_phone", request.input.phone)
            	          .set("user_address", request.input.address)
            	          .set("user_role", request.input.role)
            	          .set("user_creation_time", Timestamp.now())
            	          .build();
                txn.put(user);
                txn.commit();
                CreateAccountResponse response = new CreateAccountResponse(request.input.username, request.input.role);
                return Response.ok(g.toJson(response)).build();
            }
        } catch (Exception e) {
        	ErrorResponse error = new ErrorResponse(ERROR_FORBIDDEN, MESSAGE_FORBIDDEN);
            return Response.ok(g.toJson(error)).build();
        }
        finally {
            // No need to rollback here, as we only have one transaction and it will be automatically rolled back if not committed.
        }
    }
}
