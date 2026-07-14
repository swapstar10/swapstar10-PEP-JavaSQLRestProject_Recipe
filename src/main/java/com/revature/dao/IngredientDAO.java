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


public class IngredientDAO {


    private ConnectionUtil connectionUtil;


    public IngredientDAO(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }



    public Ingredient getIngredientById(int id) {

        String sql = "SELECT * FROM ingredient WHERE id=?";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setInt(1, id);


            try(ResultSet rs = ps.executeQuery()) {

                if(rs.next()) {
                    return mapSingleRow(rs);
                }

            }


        }catch(SQLException e) {

            e.printStackTrace();

        }


        return null;

    }




    public int createIngredient(Ingredient ingredient) {


        String sql = "INSERT INTO ingredient(name) VALUES(?)";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps =
                    conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            ps.setString(1, ingredient.getName());

            ps.executeUpdate();



            try(ResultSet rs = ps.getGeneratedKeys()) {


                if(rs.next()) {

                    int id = rs.getInt(1);

                    ingredient.setId(id);

                    return id;

                }

            }


        }catch(SQLException e) {

            e.printStackTrace();

        }


        return 0;

    }





    public void deleteIngredient(Ingredient ingredient) {


        String deleteRecipeIngredient =
                "DELETE FROM recipe_ingredient WHERE ingredient_id=?";


        String deleteIngredient =
                "DELETE FROM ingredient WHERE id=?";


        try(Connection conn = connectionUtil.getConnection()) {


            conn.setAutoCommit(false);


            try(PreparedStatement ps1 =
                    conn.prepareStatement(deleteRecipeIngredient)) {


                ps1.setInt(1, ingredient.getId());

                ps1.executeUpdate();

            }



            try(PreparedStatement ps2 =
                    conn.prepareStatement(deleteIngredient)) {


                ps2.setInt(1, ingredient.getId());

                ps2.executeUpdate();

            }


            conn.commit();



        }catch(SQLException e) {

            e.printStackTrace();

        }

    }





    public void updateIngredient(Ingredient ingredient) {


        String sql =
                "UPDATE ingredient SET name=? WHERE id=?";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, ingredient.getName());

            ps.setInt(2, ingredient.getId());


            ps.executeUpdate();


        }catch(SQLException e) {

            e.printStackTrace();

        }

    }





    public List<Ingredient> getAllIngredients() {


        String sql =
                "SELECT * FROM ingredient ORDER BY id";


        try(Connection conn = connectionUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {


            return mapRows(rs);


        }catch(SQLException e) {

            e.printStackTrace();

        }


        return new ArrayList<>();

    }





    public Page<Ingredient> getAllIngredients(PageOptions pageOptions) {


        String orderCol =
                resolveOrderBy(pageOptions.getSortBy());


        String direction =
                resolveDirection(pageOptions.getSortDirection());



        String sql =
                "SELECT * FROM ingredient ORDER BY "
                + orderCol + " " + direction;



        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {


            return pageResults(rs,pageOptions);


        }catch(SQLException e) {

            e.printStackTrace();

        }



        return new Page<>(
                pageOptions.getPageNumber(),
                pageOptions.getPageSize(),
                0,
                0,
                new ArrayList<>()
        );

    }





    public List<Ingredient> searchIngredients(String term) {


        String sql =
                "SELECT * FROM ingredient WHERE LOWER(name) LIKE LOWER(?) ORDER BY id";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1,"%"+term+"%");


            try(ResultSet rs = ps.executeQuery()) {

                return mapRows(rs);

            }


        }catch(SQLException e) {

            e.printStackTrace();

        }


        return new ArrayList<>();

    }





    public Page<Ingredient> searchIngredients(
            String term,
            PageOptions pageOptions) {


        String orderCol =
                resolveOrderBy(pageOptions.getSortBy());


        String direction =
                resolveDirection(pageOptions.getSortDirection());



        String sql =
                "SELECT * FROM ingredient "
                + "WHERE LOWER(name) LIKE LOWER(?) "
                + "ORDER BY "
                + orderCol + " " + direction;



        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1,"%"+term+"%");



            try(ResultSet rs = ps.executeQuery()) {

                return pageResults(rs,pageOptions);

            }


        }catch(SQLException e) {

            e.printStackTrace();

        }



        return new Page<>(
                pageOptions.getPageNumber(),
                pageOptions.getPageSize(),
                0,
                0,
                new ArrayList<>()
        );

    }





    private Ingredient mapSingleRow(ResultSet rs)
            throws SQLException {


        return new Ingredient(
                rs.getInt("id"),
                rs.getString("name")
        );

    }





    private List<Ingredient> mapRows(ResultSet rs)
            throws SQLException {


        List<Ingredient> ingredients =
                new ArrayList<>();


        while(rs.next()) {

            ingredients.add(mapSingleRow(rs));

        }


        return ingredients;

    }





    private Page<Ingredient> pageResults(
            ResultSet rs,
            PageOptions pageOptions)
            throws SQLException {



        List<Ingredient> all =
                mapRows(rs);



        int page =
                Math.max(1,pageOptions.getPageNumber());


        int size =
                Math.max(1,pageOptions.getPageSize());



        int total =
                all.size();



        int start =
                (page-1)*size;


        int end =
                Math.min(start+size,total);



        List<Ingredient> items =
                new ArrayList<>();


        if(start < total) {

            items =
                    all.subList(start,end);

        }



        int totalPages =
                (int)Math.ceil(
                        total/(double)size
                );



        return new Page<>(
                page,
                size,
                totalPages,
                total,
                items
        );

    }





    private String resolveOrderBy(String sortBy) {


        if(sortBy == null)
            return "id";


        if(sortBy.equalsIgnoreCase("name"))
            return "name";


        return "id";

    }





    private String resolveDirection(String sortDirection) {


        if(sortDirection != null &&
                sortDirection.equalsIgnoreCase("DESC")) {

            return "DESC";

        }


        return "ASC";

    }

}