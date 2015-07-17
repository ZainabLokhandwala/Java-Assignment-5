/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Assignment4;

import com.mysql.jdbc.Connection;
import static com.sun.el.lang.ELArithmetic.add;
import java.io.StringReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonParser;
import javax.ws.rs.*;


/**
 *
 * @author Zainab
 */


@Path("/Products_Table")
public class productservlet {

    @GET
    @Produces("application/json")
    public String doGet() {
        return getResults("SELECT * FROM Products_Table");
    }

    @GET
    @Produces("application/json")
    @Path("{Pro_ID}")
    public String doGet(@PathParam("Pro_ID") String Pro_ID) {
        return getResults("SELECT * FROM Products_Table WHERE Pro_ID = ?", Pro_ID);
    }

    @POST
    @Consumes("application/json")
    public void doPost(String str) {
        JsonParser parser = Json.createParser(new StringReader(str));
        Map<String, String> mapKeyValue = new HashMap<>();
        String key = "", val;
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_STRING:
                    val = parser.getString();
                    mapKeyValue.put(key, val);
                    break;
                case VALUE_NUMBER:
                    val = Integer.toString(parser.getInt());
                    mapKeyValue.put(key, val);
                    break;
            }
        }
        System.out.println(mapKeyValue);
        doPostOrPutOrDelete("INSERT INTO Products_Table (Pro_name, Pro_Description, Pro_Quantity) VALUES ( ?, ?, ?)",
                mapKeyValue.get("Pro_name"), mapKeyValue.get("Pro_Description"), mapKeyValue.get("Pro_Quantity"));
    }

    @PUT
    @Path("{Pro_ID}")
    @Consumes("application/json")
    public void doPut(@PathParam("Pro_ID") String id, String str) {
        JsonParser parser = Json.createParser(new StringReader(str));
        Map<String, String> mapKayValue = new HashMap<>();
        String key = "", val;
        while (parser.hasNext()) {
            JsonParser.Event evt = parser.next();
            switch (evt) {
                case KEY_NAME:
                    key = parser.getString();
                    break;
                case VALUE_STRING:
                    val = parser.getString();
                    mapKayValue.put(key, val);
                    break;
                case VALUE_NUMBER:
                    val = parser.getString();
                    mapKayValue.put(key, val);
                    break;
            }
        }
        System.out.println(mapKayValue);
        doPostOrPutOrDelete("UPDATE Products_Table SET Pro_name = ?, Pro_Description= ?, Pro_Quantity= ? WHERE Pro_ID= ?",
                mapKayValue.get("Pro_name"), mapKayValue.get("Pro_Description"), mapKayValue.get("Pro_Quantity"), id);

    }

    @DELETE
    @Path("{Pro_ID}")
    public void doDelete(@PathParam("Pro_Id") String id, String str) {
        doPostOrPutOrDelete("DELETE FROM Products_Table WHERE Pro_ID = ?", id);
    }

    private void doPostOrPutOrDelete(String query, String... params) {
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        getResults("SELECT * FROM Products_Table");
    }

    private Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String jdbc = "jdbc:mysql://localhost/javaproducts";
            conn = (Connection) DriverManager.getConnection(jdbc, "root", "");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    private String getResults(String query, String... params) {
         JsonArrayBuilder productArr = Json.createArrayBuilder();
         String res = new String();
              try (Connection conn = getConnection()) {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
  
             while (rs.next()) {
              JsonObjectBuilder json = Json.createObjectBuilder()
               .add("Pro_ID", rs.getInt("Pro_ID"))
                        .add("Pro_name", rs.getString("Pro_name"))
                        .add("Pro_Description", rs.getString("Pro_Description"))
                        .add("Pro_Quantity", rs.getInt("Pro_Quantity"));
                 res = json.build().toString();
                productArr.add(json);
            }
  } catch (SQLException ex) {
            Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (params.length == 0) {
            res = productArr.build().toString();
        }
        return res;
    }
}
