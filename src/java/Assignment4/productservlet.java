/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Assignment4;

import com.mysql.jdbc.Connection;
import java.io.StringReader;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonParser;
//import javax.persistence.*;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
import javax.ws.rs.*;
import org.json.simple.JSONArray;


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
        String result = new String();
        try (Connection conn = getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();
            JSONArray productArr = new JSONArray();
            while (rs.next()) {
                Map productMap = new LinkedHashMap();
                productMap.put("Pro_ID", rs.getInt("Pro_ID"));
                productMap.put("Pro_name", rs.getString("Pro_name"));
                productMap.put("Pro_Description", rs.getString("Pro_Description"));
                productMap.put("Pro_Quantity", rs.getInt("Pro_Quantity"));
                productArr.add(productMap);
            }
            result = productArr.toString();
        } catch (SQLException ex) {
            Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result.replace("},", "},\n");
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
//    @GET
//    @Produces("application/json")
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
//        try (PrintWriter out = response.getWriter()) {
//            if (!request.getParameterNames().hasMoreElements()) {
//                out.println(getResults("SELECT * FROM Products_Table"));
//            } else {
//                int Pro_ID = Integer.parseInt(request.getParameter("Pro_ID"));
//                out.println(getResults("SELECT * FROM product WHERE productID = ?", String.valueOf(productID)));
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(ProductServlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
//    @POST
//    @Consumes("application/json")
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        Set<String> keySet = request.getParameterMap().keySet();
//        try (PrintWriter out = response.getWriter()) {
//            Connection conn = getConnection();
//            if (keySet.contains("Pro_name") && keySet.contains("Pro_Description") && keySet.contains("Pro_Quantity")) {
//                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `Products_Table`(`Pro_ID`, `Pro_name`, `Pro_Description', `Pro_Quantity`) "
//                        + "VALUES (null, '" + request.getParameter("Pro_Name") + "', '"
//                        + request.getParameter("Pro_Description") + "', "
//                        + request.getParameter("Pro_Quantity") + ");"
//                );
//                try {
//                    pstmt.executeUpdate();
//                    request.getParameter("Pro_ID");
//                    doGet(request, response);
//                } catch (SQLException ex) {
//                    Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
//                    out.println("Data inserted Error while retriving data.");
//                }
//            } else {
//                out.println("Error: Not enough data to input");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Override
//    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        Set<String> keySet = request.getParameterMap().keySet();
//        try (PrintWriter out = response.getWriter()) {
//            Connection conn = getConnection();
//            if (keySet.contains("Pro_ID") && keySet.contains("Pro_name") && keySet.contains("Pro_Description") && keySet.contains("Pro_Quantity")) {
//                PreparedStatement pstmt = conn.prepareStatement("UPDATE `Products_Table` SET `Pro_Name`='"
//                        + request.getParameter("Pro_name") + "',`Pro_Description`='"
//                        + request.getParameter("Pro_Description")
//                        + "',`Pro_Quantity`=" + request.getParameter("Pro_Quantity")
//                        + " WHERE `Pro_ID`=" + request.getParameter("Pro_ID"));
//                try {
//                    pstmt.executeUpdate();
//                    doGet(request, response); //shows updated row
//                } catch (SQLException ex) {
//                    Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
//                    out.println("Error putting values.");
//                }
//            } else {
//                out.println("Error: Not enough data to update");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Override
//    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Set<String> keySet = request.getParameterMap().keySet();
//        try (PrintWriter out = response.getWriter()) {
//            Connection conn = getConnection();
//            if (keySet.contains("Pro_ID")) {
//                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `Products_Table` WHERE `Pro_ID`=" + request.getParameter("Pro_ID"));
//                try {
//                    pstmt.executeUpdate();
//                } catch (SQLException ex) {
//                    Logger.getLogger(product_servlet.class.getName()).log(Level.SEVERE, null, ex);
//                    out.println("Error in deleting the product.");
//                }
//            } else {
//                out.println("Error: in data to delete");
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(productservlet.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
}
