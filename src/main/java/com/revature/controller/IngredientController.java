package com.revature.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import com.revature.service.IngredientService;
import com.revature.model.Ingredient;
import com.revature.util.Page;

import java.util.List;
import java.util.Optional;



/**
 * The IngredientController class handles operations related to ingredients. It allows for creating, retrieving, updating, and deleting individual ingredients, as well as retrieving a list of all ingredients. 
 * 
 * The class interacts with the IngredientService to perform these operations.
 */

public class IngredientController {

    /**
     * A service that manages ingredient-related operations.
     */

    
    private IngredientService ingredientService;

    /**
     * Constructs an IngredientController with the specified IngredientService.
     *
     * TODO: Finish the implementation so that this class's instance variables are initialized accordingly.
     * 
     * @param ingredientService the service used to manage ingredient-related operations
     */

    public IngredientController(IngredientService ingredientService) {
      this.ingredientService = ingredientService;  
    }

    /**
     * TODO: Retrieves a single ingredient by its ID.
     * 
     * If the ingredient exists, responds with a 200 OK status and the ingredient data. If not found, responds with a 404 Not Found status.
     *
     * @param ctx the Javalin context containing the request path parameter for the ingredient ID
     */
    public void getIngredient(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Optional<Ingredient> ingredientOpt = ingredientService.findIngredient(id);

        if(ingredientOpt.isPresent()){
            ctx.status(200);
            ctx.json(ingredientOpt.get());
        }else{
            ctx.status(404);
            ctx.result("Ingredient not found");
        }
        
    }

    /**
     * TODO: Deletes an ingredient by its ID.
     * 
     * Responds with a 204 No Content status.
     *
     * @param ctx the Javalin context containing the request path parameter for the ingredient id
     */
    public void deleteIngredient(Context ctx) {
       int id = Integer.parseInt(ctx.pathParam("id"));
       ingredientService.deleteIngredient(id); 
       ctx.status(204);
    }

    /**
     * TODO: Updates an existing ingredient by its ID.
     * 
     * If the ingredient exists, updates it and responds with a 204 No Content status. If not found, responds with a 404 Not Found status.
     *
     * @param ctx the Javalin context containing the request path parameter and updated ingredient data in the request body
     */
    public void updateIngredient(Context ctx) {
       int id= Integer.parseInt(ctx.pathParam("id"));
        Optional<Ingredient> existing = ingredientService.findIngredient(id);
        if(existing.isEmpty()){
            ctx.status(404);
            return;
        }
        Ingredient updated = ctx.bodyAsClass(Ingredient.class);
        updated.setId(id);
        ingredientService.saveIngredient(updated);
        ctx.status(204); 
    }

    /**
     * TODO: Creates a new ingredient.
     * 
     * Saves the ingredient and responds with a 201 Created status.
     *
     * @param ctx the Javalin context containing the ingredient data in the request body
     */
    public void createIngredient(Context ctx) {

        Ingredient ingredient = ctx.bodyAsClass(Ingredient.class);
        ingredientService.saveIngredient(ingredient);
        
            ctx.status(201);
            ctx.json(ingredient);
       
    }

    /**
     * TODO: Retrieves a paginated list of ingredients, or all ingredients if no pagination parameters are provided.
     * 
     * If pagination parameters are included, returns ingredients based on page, page size, sorting, and filter term.
     *
     * @param ctx the Javalin context containing query parameters for pagination, sorting, and filtering
     */
    public void getIngredients(Context ctx) {

        String term = ctx.queryParam("term");
        String sortBy = ctx.queryParam("sortBy");
        String sortDirection = ctx.queryParam("sortDirection");
        Integer page = tryParseInt(ctx.queryParam("page"));
        Integer pageSize = tryParseInt(ctx.queryParam("pageSize"));

        boolean hasPaging =(page != null && pageSize != null );

        if (hasPaging){
            String effectiveSortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
            String effectiveSortDir = (sortDirection != null && !sortDirection.isBlank()) ? sortDirection : "asc";
            
            Page<Ingredient> ingredientPage = ingredientService.searchIngredients(term, page, pageSize, effectiveSortBy, effectiveSortDir);

            ctx.status(200);
            ctx.json(ingredientPage);
            return;
        }
        List<Ingredient> list =ingredientService.searchIngredients(term);
        ctx.status(200);
        ctx.json(list);
    }
    private Integer tryParseInt(String s){
        if(s == null) return null;
        try{
            return Integer.valueOf(s);
        }catch(NumberFormatException ex){
            return null;
        }
    }
 

    /**
     * A helper method to retrieve a query parameter from the context as a specific class type, or return a default value if the query parameter is not present.
     *
     * @param <T> the type of the query parameter
     * @param ctx the Javalin context containing query parameters
     * @param queryParam the name of the query parameter to retrieve
     * @param clazz the class type of the parameter
     * @param defaultValue the default value to return if the parameter is absent
     * @return the query parameter value as the specified type, or the default value if absent
     */
    @SuppressWarnings("unused")
    private <T> T getParamAsClassOrElse(Context ctx, String queryParam, Class<T> clazz, T defaultValue) {
        if(ctx.queryParam(queryParam) != null) {
            return ctx.queryParamAsClass(queryParam, clazz).get();
        } else {
            return defaultValue;
        }
    }
    /**
     * Configure the routes for ingredient operations.
     *
     * @param app the Javalin application
     */
    public void configureRoutes(Javalin app) {
        app.get("/ingredients", this::getIngredients);
        app.get("/ingredients/{id}", this::getIngredient);
        app.post("/ingredients", this::createIngredient);
        app.put("/ingredients/{id}", this::updateIngredient);
        app.delete("/ingredients/{id}", this::deleteIngredient);
    }
}

