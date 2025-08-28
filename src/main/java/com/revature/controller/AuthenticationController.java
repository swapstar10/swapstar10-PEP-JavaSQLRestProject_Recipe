package com.revature.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import com.revature.model.Chef;
import com.revature.service.AuthenticationService;
import com.revature.service.ChefService;





/**
 * The AuthenticationController class handles user authentication-related operations. This includes login, logout, and registration.
 * 
 * It interacts with the ChefService and AuthenticationService for certain functionalities related to the user.
 */
public class AuthenticationController {

    /** A service that handles chef-related operations. */
   
    private ChefService chefService;

    /** A service that handles authentication-related operations. */
    
    private AuthenticationService authService;

    /**
     * Constructs an AuthenticationController with its parameters.
     * 
     * TODO: Finish the implementation so that this class's instance variables are initialized accordingly.
     * 
     * @param chefService the service used to manage chef-related operations
     * @param authService the service used to manage authentication-related operations
     */
    public AuthenticationController(ChefService chefService, AuthenticationService authService) {
      this.chefService = chefService;
      this.authService = authService;  
    }

    /**
     * TODO: Registers a new chef in the system.
     * 
     * If the username already exists, responds with a 409 Conflict status and a result of "Username already exists".
     * 
     * Otherwise, registers the chef and responds with a 201 Created status and the registered chef details.
     *
     * @param ctx the Javalin context containing the chef information in the request body
     */
    public void register(Context ctx) {
        Chef chef =ctx.bodyAsClass(Chef.class);

        if(chef == null || chef.getUsername() == null || chef.getUsername().isBlank()){
            ctx.status(400);
            ctx.result("Username is required");
            return;
        }
         if(chef.getPassword() == null || chef.getPassword().isBlank()){
            ctx.status(400);
            ctx.result("Password is required");
            return;
        }
         if(chefService.getChefByUsername(chef.getUsername()).isPresent()){
            ctx.status(409);
            ctx.result("Username already exists");
            return;
        }
        chefService.saveChef(chef);
        ctx.status(201);
        ctx.json(chef);
        
    }

    /**
     * TODO: Authenticates a chef and uses a generated authorization token if the credentials are valid. The token is used to check if login is successful. If so, this method responds with a 200 OK status, the token in the response body, and an "Authorization" header that sends the token in the response.
     * 
     * If login fails, responds with a 401 Unauthorized status and an error message of "Invalid username or password".
     *
     * @param ctx the Javalin context containing the chef login credentials in the request body
     */
    public void login(Context ctx) {
        Chef body = null;
        try{
            body = ctx.bodyAsClass(Chef.class);
        }catch(Exception ignore){

        }

        String username = null;
        String password = null;

        if(body != null){
            username = body.getUsername();
            password = body.getPassword();
        }

        if (username == null) username = ctx.formParam("username");
        if (password == null) password = ctx.formParam("password");
        if (username == null) username = ctx.queryParam("username");
        if (password == null) password = ctx.queryParam("password");

        if(username == null || username.isBlank() || password == null){
            ctx.status(401);
            ctx.result("Invalid username or password");
            return;
        }
        String token = authService.login(new Chef(username, password));
        if(token==null){
            ctx.status(401);
            ctx.result("Invalid username or password");
            return;
        }
        
          ctx.header("Authorization",token);
          ctx.status(200);
          ctx.result(token);

         
    }

    /**
     * TODO: Logs out the currently authenticated chef by invalidating their token. Responds with a 200 OK status and a result of "Logout successful".
     *
     * @param ctx the Javalin context, containing the Authorization token in the request header
     */
    public void logout(Context ctx) {
        String token =extractToken(ctx.header("Authorization"));

        if(token==null|| !authService.isTokenValid(token)){
            ctx.status(401);
            ctx.result("Invalid or missing token");
            return;
        }
        authService.logout(token);
        ctx.status(200);
        ctx.result("Logout successful")  ;  
        
    }
     private String extractToken(String header){
        if( header == null) return null;
        String h = header.trim();
        if(h.regionMatches(true, 0, "Bearer ", 0, 7)){
            return h.substring(7).trim();
        }
        return h;
    }

    /**
     * Configures the routes for authentication operations.
     * 
     * Sets up routes for registration, login, and logout.
     *
     * @param app the Javalin application to which routes are added
     */
    public void configureRoutes(Javalin app) {
        app.post("/register", this::register);
        app.post("/login", this::login);
        app.post("/logout", this::logout);
    }

}
