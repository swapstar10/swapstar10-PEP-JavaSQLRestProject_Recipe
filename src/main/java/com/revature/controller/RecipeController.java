package com.revature.controller;

import io.javalin.http.Handler;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Optional;

import com.revature.model.Recipe;
import com.revature.service.AuthenticationService;
import com.revature.service.RecipeService;
import com.revature.util.Page;


/**
 * The RecipeController class provides RESTful endpoints for managing recipes.
 * It interacts with RecipeService to perform CRUD operations.
 */
public class RecipeController {

    private RecipeService recipeService;

    private AuthenticationService authService;


    public RecipeController(RecipeService recipeService, AuthenticationService authService) {
        this.recipeService = recipeService;
        this.authService = authService;
    }


    /**
     * Get all recipes with optional search, pagination and sorting.
     */
    public Handler fetchAllRecipes = ctx -> {

        String term = ctx.queryParam("term");

String name = ctx.queryParam("name");
String ingredient = ctx.queryParam("ingredient");

// support old test params
if(term == null){
    if(name != null){
        term = name;
    }
    else if(ingredient != null){
        term = ingredient;
    }
}

String sortBy = ctx.queryParam("sortBy");
String sortDirection = ctx.queryParam("sortDirection");

        Integer page = tryParseInt(ctx.queryParam("page"));
        Integer pageSize = tryParseInt(ctx.queryParam("pageSize"));


        boolean hasPaging = page != null && pageSize != null;


        if(hasPaging){

            String effectiveSortBy =
                    (sortBy != null && !sortBy.isBlank())
                    ? sortBy
                    : "id";


            String effectiveSortDirection =
                    (sortDirection != null && !sortDirection.isBlank())
                    ? sortDirection
                    : "asc";


            Page<Recipe> result =
                    recipeService.searchRecipes(
                            term,
                            page,
                            pageSize,
                            effectiveSortBy,
                            effectiveSortDirection
                    );


            ctx.status(200);
            ctx.json(result);
            return;
        }



        List<Recipe> recipes;


        if(term != null){

            recipes = recipeService.searchRecipes(term);

        }
        else{

            recipes = recipeService.searchRecipes(null);

        }



        if(recipes == null || recipes.isEmpty()){

            ctx.status(404);
            ctx.result("No recipes found");

        }
        else{

            ctx.status(200);
            ctx.json(recipes);

        }

    };



    private Integer tryParseInt(String value){

        if(value == null)
            return null;

        try{

            return Integer.parseInt(value);

        }
        catch(NumberFormatException e){

            return null;

        }

    }




    /**
     * Get recipe by id.
     */
    public Handler fetchRecipeById = ctx -> {


        int id = Integer.parseInt(ctx.pathParam("id"));


        Optional<Recipe> recipe =
                recipeService.findRecipe(id);



        if(recipe.isPresent()){

            ctx.status(200);
            ctx.json(recipe.get());

        }
        else{

            ctx.status(404);
            ctx.result("Recipe not found");

        }

    };




    private String extractToken(String header){

        if(header == null)
            return null;


        String value = header.trim();


        if(value.regionMatches(true,0,"Bearer ",0,7)){

            return value.substring(7).trim();

        }


        return value;

    }





    /**
     * Create recipe.
     */
    public Handler createRecipe = ctx -> {


    String token =
            extractToken(ctx.header("Authorization"));


    if(token == null || !authService.isTokenValid(token)){

        ctx.status(401);
        ctx.result("Invalid or missing token");
        return;

    }


    Recipe recipe =
            ctx.bodyAsClass(Recipe.class);


    // Attach logged-in chef as recipe author
    recipe.setAuthor(
            authService.getChefFromSessionToken(token)
    );


    recipeService.createRecipe(recipe);


    ctx.status(201);
    ctx.json(recipe);

};






    /**
     * Delete recipe.
     */
    public Handler deleteRecipe = ctx -> {


        int id =
                Integer.parseInt(ctx.pathParam("id"));



        boolean deleted =
                recipeService.deleteRecipe(id);



        if(deleted){

            ctx.status(200);
            ctx.result("Recipe deleted successfully");

        }
        else{

            ctx.status(404);
            ctx.result("Recipe not found");

        }


    };







    /**
     * Update recipe.
     */
    public Handler updateRecipe = ctx -> {


        int id =
                Integer.parseInt(ctx.pathParam("id"));



        Recipe recipe =
                ctx.bodyAsClass(Recipe.class);



        recipe.setId(id);



        Optional<Recipe> updated =
                recipeService.updateRecipe(id, recipe);



        if(updated.isPresent()){

            ctx.status(200);
            ctx.json(updated.get());

        }
        else{

            ctx.status(404);
            ctx.result("Recipe not found");

        }

    };







    @SuppressWarnings("unused")
    private <T> T getParamAsClassOrElse(
            Context ctx,
            String queryParam,
            Class<T> clazz,
            T defaultValue) {


        String value =
                ctx.queryParam(queryParam);



        if(value != null){

            if(clazz == Integer.class){

                return clazz.cast(Integer.valueOf(value));

            }
            else if(clazz == Boolean.class){

                return clazz.cast(Boolean.valueOf(value));

            }
            else{

                return clazz.cast(value);

            }

        }


        return defaultValue;

    }






    /**
     * Configure routes.
     */
    public void configureRoutes(Javalin app){


        app.get("/recipes", fetchAllRecipes);

        app.get("/recipes/{id}", fetchRecipeById);

        app.post("/recipes", createRecipe);

        app.put("/recipes/{id}", updateRecipe);

        app.delete("/recipes/{id}", deleteRecipe);


    }

}