package com.revature.dao;

import com.revature.util.ConnectionUtil;
import com.revature.util.Page;
import com.revature.util.PageOptions;
import com.revature.model.Chef;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * The ChefDAO class abstracts the CRUD operations for Chef objects.
 */
public class ChefDAO {

    private ConnectionUtil connectionUtil;


    public ChefDAO(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }


    /**
     * Retrieves all chefs from database.
     */
    public List<Chef> getAllChefs() {

        String sql = "SELECT * FROM CHEF ORDER BY id";

        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return mapRows(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    /**
     * Retrieves paginated chefs.
     */
    public Page<Chef> getAllChefs(PageOptions pageOptions) {

        String sql = "SELECT * FROM CHEF ORDER BY id";

        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return pageResults(rs, pageOptions);

        } catch (SQLException e) {
            e.printStackTrace();
            return new Page<>(0, 0, 0, 0, new ArrayList<>());
        }
    }



    /**
     * Retrieves chef by id.
     */
    public Chef getChefById(int id) {

        String sql = "SELECT * FROM chef WHERE id=?";


        try (Connection conn = connectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setInt(1, id);


            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapSingleRow(rs);
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }




    /**
     * Retrieves chef by username.
     */
    public Chef getChefByUsername(String username) {


        String sql = "SELECT * FROM chef WHERE username=?";


        try (Connection conn = connectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, username);


            try(ResultSet rs = ps.executeQuery()) {

                if(rs.next()) {
                    return mapSingleRow(rs);
                }

            }


        } catch(SQLException e) {
            e.printStackTrace();
        }


        return null;
    }





    /**
     * Creates chef.
     */
    public int createChef(Chef chef) {


        String sql =
                "INSERT INTO chef(username,email,password,is_admin) VALUES(?,?,?,?)";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps =
                    conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            ps.setString(1, chef.getUsername());
            ps.setString(2, chef.getEmail());
            ps.setString(3, chef.getPassword());
            ps.setBoolean(4, chef.isAdmin());


            ps.executeUpdate();


            try(ResultSet rs = ps.getGeneratedKeys()) {

                if(rs.next()) {

                    int id = rs.getInt(1);
                    chef.setId(id);
                    return id;

                }

            }


        } catch(SQLException e) {

            e.printStackTrace();

        }


        return 0;
    }





    /**
     * Updates chef.
     */
    public void updateChef(Chef chef) {


        String sql =
                "UPDATE chef SET username=?, email=?, password=?, is_admin=? WHERE id=?";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, chef.getUsername());
            ps.setString(2, chef.getEmail());
            ps.setString(3, chef.getPassword());
            ps.setBoolean(4, chef.isAdmin());
            ps.setInt(5, chef.getId());


            ps.executeUpdate();


        } catch(SQLException e) {

            e.printStackTrace();

        }

    }





    /**
     * Deletes chef.
     */
    public void deleteChef(Chef chef) {


        String sql = "DELETE FROM chef WHERE id=?";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setInt(1, chef.getId());

            ps.executeUpdate();


        } catch(SQLException e) {

            e.printStackTrace();

        }

    }





    /**
     * Search chefs.
     */
    public List<Chef> searchChefsByTerm(String term) {


        String sql =
                "SELECT * FROM chef WHERE LOWER(username) LIKE LOWER(?)";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, "%" + term + "%");


            try(ResultSet rs = ps.executeQuery()) {

                return mapRows(rs);

            }


        } catch(SQLException e) {

            e.printStackTrace();

        }


        return new ArrayList<>();

    }






    /**
     * Search chefs with pagination.
     */
    public Page<Chef> searchChefsByTerm(String term, PageOptions pageOptions) {


        String sql =
                "SELECT * FROM chef WHERE LOWER(username) LIKE LOWER(?)";


        try(Connection conn = connectionUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, "%" + term + "%");


            try(ResultSet rs = ps.executeQuery()) {

                return pageResults(rs, pageOptions);

            }


        } catch(SQLException e) {

            e.printStackTrace();

        }


        return new Page<>(0,0,0,0,new ArrayList<>());

    }





    private Chef mapSingleRow(ResultSet set) throws SQLException {


        int id = set.getInt("id");
        String username = set.getString("username");
        String email = set.getString("email");
        String password = set.getString("password");
        boolean isAdmin = set.getBoolean("is_admin");


        return new Chef(
                id,
                username,
                email,
                password,
                isAdmin
        );

    }





    private List<Chef> mapRows(ResultSet set) throws SQLException {


        List<Chef> chefs = new ArrayList<>();


        while(set.next()) {

            chefs.add(mapSingleRow(set));

        }


        return chefs;

    }





    private Page<Chef> pageResults(ResultSet set,
                                   PageOptions pageOptions)
            throws SQLException {


        List<Chef> chefs = mapRows(set);


        int offset =
                (pageOptions.getPageNumber() - 1)
                * pageOptions.getPageSize();


        int limit =
                offset + pageOptions.getPageSize();


        List<Chef> slicedList =
                sliceList(chefs, offset, limit);



        int totalPages =
                (int)Math.ceil(
                        (double)chefs.size()
                        / pageOptions.getPageSize()
                );



        return new Page<>(
                pageOptions.getPageNumber(),
                pageOptions.getPageSize(),
                totalPages,
                chefs.size(),
                slicedList
        );

    }





    private List<Chef> sliceList(List<Chef> list,
                                 int start,
                                 int end) {


        List<Chef> sliced = new ArrayList<>();


        for(int i=start; i<end && i<list.size(); i++) {

            sliced.add(list.get(i));

        }


        return sliced;

    }

}