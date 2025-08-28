package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import com.revature.util.ConnectionUtil;
import com.revature.util.Page;
import com.revature.util.PageOptions;
import com.revature.model.Ingredient;




/**
 * The IngredientDAO class handles the CRUD operations for Ingredient objects. It provides methods for creating, retrieving, updating, and deleting Ingredient records from the database. 
 * 
 * This class relies on the ConnectionUtil class for database connectivity and also supports searching and paginating through Ingredient records.
 */

public class IngredientDAO {

    /** A utility class used for establishing connections to the database. */
    @SuppressWarnings("unused")
    private ConnectionUtil connectionUtil;

    /**
     * Constructs an IngredientDAO with the specified ConnectionUtil for database connectivity.
     * 
     * TODO: Finish the implementation so that this class's instance variables are initialized accordingly.
     * 
     * @param connectionUtil the utility used to connect to the database
     */
    public IngredientDAO(ConnectionUtil connectionUtil) {
      this.connectionUtil=connectionUtil;
    }

    /**
     * TODO: Retrieves an Ingredient record by its unique identifier.
     *
     * @param id the unique identifier of the Ingredient to retrieve.
     * @return the Ingredient object with the specified id.
     */
    public Ingredient getIngredientById(int id) {
        String sql= "SELECT * FROM ingredient WHERE id=?";
        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1,id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return mapSingleRow(rs);
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * TODO: Creates a new Ingredient record in the database.
     *
     * @param ingredient the Ingredient object to be created.
     * @return the unique identifier of the created Ingredient.
     */
    public int createIngredient(Ingredient ingredient) {
        String sql= "INSERT INTO ingredient(name) VALUES(?)";
        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1,ingredient.getName());
            ps.executeUpdate();
           
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()){
                    int id=rs.getInt(1);
                    ingredient.setId(id);
                    return id;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * TODO: Deletes an ingredient record from the database, including references in related tables.
     *
     * @param ingredient the Ingredient object to be deleted.
     */
    public void deleteIngredient(Ingredient ingredient) {
     String deleteRecipeIngredient ="DELETE FROM recipe_ingredient WHERE ingredient_id=?"   ;
      String deleteIngredient ="DELETE FROM ingredient WHERE id=?";
      try(Connection conn = connectionUtil.getConnection()){
        try(PreparedStatement ps1=conn.prepareStatement(deleteRecipeIngredient)){
            ps1.setInt(1,ingredient.getId());
            ps1.executeUpdate();
        }
        try(PreparedStatement ps2=conn.prepareStatement(deleteIngredient)){
            ps2.setInt(1,ingredient.getId());
            ps2.executeUpdate();        
      }
    }catch(SQLException e){
        e.printStackTrace();
    }

    }

    /**
     * TODO: Updates an existing Ingredient record in the database.
     *
     * @param ingredient the Ingredient object containing updated information.
     */
    public void updateIngredient(Ingredient ingredient) {
       String sql= "UPDATE ingredient SET name=? WHERE id=?";
        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1,ingredient.getName());
            ps.setInt(2,ingredient.getId());           
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }        
    }

    /**
     * TODO: Retrieves all ingredient records from the database.
     *
     * @return a list of all Ingredient objects.
     */
    public List<Ingredient> getAllIngredients() {
          String sql= "SELECT * FROM INGREDIENT ORDER BY id";
        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs=ps.executeQuery()) {
          return mapRows(rs);
        }catch(SQLException e){
            e.printStackTrace();
        } 
        return new ArrayList<>();
    }

    /**
     * TODO: Retrieves all ingredient records from the database with pagination options.
     *
     * @param pageOptions options for pagination and sorting.
     * @return a Page of Ingredient objects containing the retrieved ingredients.
     */
    public Page<Ingredient> getAllIngredients(PageOptions pageOptions) {
        String orderCol = resolveOrderBy(pageOptions.getSortBy());
        String dir = resolveDirection(pageOptions.getSortDirection());
        String sql = "SELECT id, name FROM INGREDIENT ORDER BY "+ orderCol +" "+ dir;
        try(var conn = connectionUtil.getConnection();
            var ps = conn.prepareStatement(sql);
            var rs = ps.executeQuery()) {

          return pageResults(rs, pageOptions);
        }catch(SQLException e){
            e.printStackTrace();
        } 
        return new Page<>(pageOptions.getPageNumber(), pageOptions.getPageSize(),  0,  0,  new ArrayList<>());
    }

    /**
     * TODO: Searches for Ingredient records by a search term in the name.
     *
     * @param term the search term to filter Ingredient names.
     * @return a list of Ingredient objects that match the search term.
     */
    public List<Ingredient> searchIngredients(String term) {
        String sql= "SELECT * FROM ingredient WHERE LOWER(name) LIKE LOWER(?) ORDER BY id";
        try(var conn = connectionUtil.getConnection();
            var ps = conn.prepareStatement(sql);) {
                ps.setString(1,"%"+term+"%");
                try(ResultSet rs=ps.executeQuery()){
                    return mapRows(rs);
                }
            }catch(SQLException e){
            e.printStackTrace();
        } 
        return new ArrayList<>();
    }

    /**
     * TODO: Searches for Ingredient records by a search term in the name with pagination options.
     *
     * @param term the search term to filter Ingredient names.
     * @param pageOptions options for pagination and sorting.
     * @return a Page of Ingredient objects containing the retrieved ingredients.
     */
    public Page<Ingredient> searchIngredients(String term, PageOptions pageOptions) {
        String orderCol = resolveOrderBy(pageOptions.getSortBy());
        String dir = resolveDirection(pageOptions.getSortDirection());
        String sql= "SELECT * FROM INGREDIENT "+" WHERE LOWER(name) LIKE LOWER(?) "+"ORDER BY "+ orderCol + " " + dir;
        try(var conn = connectionUtil.getConnection();
            var ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + term + "%");
                try(var rs = ps.executeQuery()){
                    return pageResults(rs, pageOptions);
                }
            }catch(SQLException e){
            e.printStackTrace();
        } 
        return new Page<>(pageOptions.getPageNumber(), pageOptions.getPageSize(),  0,  0, new ArrayList<>());
    }

    // below are helper methods for your convenience

    /**
     * Maps a single row from the ResultSet to an Ingredient object.
     *
     * @param resultSet the ResultSet containing Ingredient data.
     * @return an Ingredient object representing the row.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private Ingredient mapSingleRow(ResultSet rs) throws SQLException {
        return new Ingredient(rs.getInt("id"), rs.getString("name"));
    }

    /**
     * Maps multiple rows from the ResultSet to a list of Ingredient objects.
     *
     * @param resultSet the ResultSet containing Ingredient data.
     * @return a list of Ingredient objects.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private List<Ingredient> mapRows(ResultSet rs) throws SQLException {
        List<Ingredient> out = new ArrayList<>();
        while (rs.next()) {
            out.add(mapSingleRow(rs));
        }
        return out;
    }

    /**
     * Paginates the results of a ResultSet into a Page of Ingredient objects.
     *
     * @param resultSet the ResultSet containing Ingredient data.
     * @param pageOptions options for pagination and sorting.
     * @return a Page of Ingredient objects containing the paginated results.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private Page<Ingredient> pageResults(ResultSet rs, PageOptions pageOptions) throws SQLException {
        List<Ingredient> all = mapRows(rs);
        
        int page = Math.max(1, pageOptions.getPageNumber());
        int size = Math.max(1, pageOptions.getPageSize());

        int total = all.size();
        int start = Math.max(0, (page-1)* size);
        int end = Math.min(start + size, total);

        List<Ingredient> items = (start <= end) ? all.subList(start, end) : new ArrayList<>();
        int totalPages = (int) Math.ceil(total / (double)size);

        return new Page<>(page, size, totalPages, total, items);
    }

    private String resolveOrderBy(String sortBy){
        if(sortBy == null) return "id";
        String s = sortBy.trim();
        if(s.equalsIgnoreCase("name")) return "name";
        return "id";

    }

    private String resolveDirection(String sortDirection){
        return (sortDirection != null && sortDirection.equalsIgnoreCase("DESC")) ? " DESC " : " ASC ";
    }
}
