import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

;

public class MyController {


    String data1, data2;
    DbConnection obj = new DbConnection();
    Connection conn = obj.connMethod();
    @FXML
    private TextField txt1;
    @FXML
    private TextField txt2;
    @FXML
    private GridPane gp;
    private ObservableList<ObservableList> data;
    @FXML
    private TableView tbl;
    public Button btn;

    private String s;

    //CONNECTION DATABASE
    public void buildData() {
        DbConnection obj1;
        Connection c;
        ResultSet rs;
        data = FXCollections.observableArrayList();
        try {

            tbl.setStyle("-fx-background-color:red; -fx-font-color:yellow ");
            obj1 = new DbConnection();
            c = obj1.connMethod();

            //int id = Integer.parseInt(data1);
            //SQL FOR SELECTING ALL OF CUSTOMER
            String SQL = "SELECT * from DEMO_ORDER";
            //ResultSet,Statement
            rs = c.createStatement().executeQuery(SQL);

            //ResultSet,PreparedStatement
        /*    String SQL = "SELECT * FROM emp WHERE empno=?";
            PreparedStatement p = conn.prepareStatement(SQL);
             rs = p.executeQuery();*/

            //ResultSet,CallableStatement
            /*CallableStatement cstmt = c.prepareCall("{call SELECTOR14(?,?)}");
            cstmt.setInt(1, id);
            cstmt.registerOutParameter(2,Types.TIMESTAMP);
            rs=cstmt.executeQuery();
            System.out.println(cstmt.getTimestamp(2));*/
            //rs = cstmt.executeQuery();


            //ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //for (int i = 1; i < rsmd.getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                //TableColumn col = new TableColumn(rsmd.getColumnLabel(i));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>,
                        ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));

                tbl.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");

            }


            while (rs.next()) {

                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row[1]added " + row);
                data.add(row);

            }


            tbl.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error ");
        }
    }




    public void handleButtonAction(ActionEvent event) {

        data1 = txt1.getText();
        data2 = txt2.getText();
        String query = "Insert into Profile(FirstName,LastName) VALUES('" + data1 + "','" + data2 + "')";
        try {

            Statement statement = conn.createStatement();
            //statement.execute(query);
            txt1.setText("");
            txt2.setText("");

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("successfuly inserted");
            a.showAndWait();
            // create a popup

            ProgressIndicator PI = new ProgressIndicator();
            //PI.setProgress(0.1);
            AnchorPane root = new AnchorPane();
            PI.setMinSize(300, 300);
            root.getChildren().add(PI);
            gp.add(root, 2, 4);


            buildData();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void create_excel(ActionEvent actionEvent) {
        String sql = "SELECT * from DEMO_ORDER";

        DbConnection obj = new DbConnection(); //create database class object
        Connection conn = obj.connMethod(); // create a connection
        try {
            System.out.println("9999"); // just test on try block
            int i =1;
            ResultSet rs = conn.createStatement().executeQuery(sql);
            System.out.println("000"); // if result set works
            // using Apache Poi.jars JARs like poi.ooxml , poi.oxml-scheamas, poi.jar .. to create our Excel
            XSSFWorkbook workbook = new XSSFWorkbook(); // create an Excel Workbook
            XSSFSheet exSheet = workbook.createSheet("profile details"); // create an Excel Sheet
            XSSFRow exHeader = exSheet.createRow(0); // our header for the Excel table
            ((XSSFRow) exHeader).createCell(0).setCellValue("ORDER_ID"); //first column
            ((XSSFRow) exHeader).createCell(1).setCellValue("CUSTOMER_ID");
            ((XSSFRow) exHeader).createCell(2).setCellValue("ORDER_TOTAL");
            ((XSSFRow) exHeader).createCell(3).setCellValue("ORDER_TIMESTAMP");
            ((XSSFRow) exHeader).createCell(4).setCellValue("USER_ID");
            while (rs.next()){
                XSSFRow theRow = exSheet.createRow(i);
                theRow.createCell(0).setCellValue(rs.getString("ORDER_ID"));
                theRow.createCell(1).setCellValue(rs.getString("CUSTOMER_ID"));
                theRow.createCell(2).setCellValue(rs.getString("ORDER_TOTAL"));
                theRow.createCell(3).setCellValue(rs.getString("ORDER_TIMESTAMP"));
                theRow.createCell(4).setCellValue(rs.getString("USER_ID"));
                // just for the first row here
                i += 1;
                // now to iterate it to all the rows
            }
            FileOutputStream outputFile = new FileOutputStream("DemoOrder.xlsx"); // create an object of fileoutputstream so we can output our table
            workbook.write(outputFile); //we write our workbook( excel table) to the output file
            outputFile.close(); //close the file stream

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText(" Excel table has been created successfully ");
            a.showAndWait();





        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }
}
