package com.smallplayz.hackathon2023e;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

import java.net.URL;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SceneController extends Thread implements Initializable{
    @FXML
    private FlowPane flowPane;
    @FXML
    private Button newButton;
    @FXML
    private Button editButton;

    private boolean editing = false;

    ArrayList<Item> items = new ArrayList<>();

    //***

    private static Socket clientSocket = null;
    public static PrintStream os = null;
    private static DataInputStream is = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;

    private int portNumber = 10334; //port
    private String host = "26.96.12.184";

    @FXML
    protected void newItem() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(null);

        System.out.println("new Item!");
        BorderPane borderPane = new BorderPane();

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(2);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));

        borderPane.setEffect(dropShadow);

        borderPane.setOnMouseEntered(event -> {
            borderPane.setEffect(dropShadow);
            borderPane.setScaleX(1.1);
            borderPane.setScaleY(1.1);
        });

        borderPane.setOnMouseExited(event -> {
            borderPane.setEffect(null);
            borderPane.setScaleX(1.0);
            borderPane.setScaleY(1.0);
        });

        borderPane.setStyle("-fx-background-color: grey; -fx-background-radius: 10;");

        borderPane.setPrefSize(100, 100);
        borderPane.setMaxSize(100, 100);
        if (selectedFile != null) {

            //***
            String sourceFilePath = selectedFile.getAbsolutePath();


                // Read the binary data of the source file into a byte array
                Path sourcePath = Paths.get(sourceFilePath);
                byte[] sourceData = Files.readAllBytes(sourcePath);

                // Convert the binary data into a binary string
                StringBuilder binaryStringBuilder = new StringBuilder();
                for (byte b : sourceData) {
                    String binaryString = Integer.toBinaryString(b & 0xFF);
                    while (binaryString.length() < 8) {
                        binaryString = "0" + binaryString;
                    }
                    binaryStringBuilder.append(binaryString);
                }
                String binaryString = binaryStringBuilder.toString();
                System.out.println(binaryString.length());

            //***

            String fileName = selectedFile.getName().replace(" ", "");

            Text text = new Text(fileName.toUpperCase());
            text.setStyle("-fx-fill: white; -fx-font-size: 14; -fx-font-family: 'SansSerif';");

            BorderPane.setMargin(text, new Insets(10, 10, 10, 10));
            BorderPane.setAlignment(text, javafx.geometry.Pos.CENTER);
            borderPane.setBottom(text);

            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".tiff") || fileName.endsWith(".CR2")) {
                FileInputStream inputstream = new FileInputStream("src/main/resources/com/smallplayz/hackathon2023e/pngpic.png");
                Image image = new Image(inputstream);
                ImageView img1 = new ImageView(image);

                img1.setFitWidth(60);
                img1.setFitHeight(60);
                borderPane.setCenter(img1);
            }

            borderPane.setOnMouseClicked(event -> {
                System.out.println("Download(" + fileName + ")");
                if (clientSocket != null && os != null && is != null) {
                        os.println("Download(" + fileName + ")");

                }
            });

            flowPane.getChildren().add(borderPane);
            FlowPane.setMargin(borderPane, new Insets(15));

            insertItem(fileName);

            if (clientSocket != null && os != null && is != null) {

                os.println("Upload("+binaryString+") ["+fileName+"]");

            }

        }
    }

    @FXML
    protected void editItems() {
        if(!editing) {
            System.out.println("Editing Items!");
            editing = true;
            editButton.getStyleClass().setAll("btn","btn-warning");
        }
        else {
            System.out.println("Not Editing Items!");
            editing = false;
            editButton.getStyleClass().setAll("btn","btn-info");
        }
        if(editing) {
            // Loop through the child nodes of the FlowPane
            for (Node node : flowPane.getChildren()) {
                // Check if the node is a BorderPane
                if (node instanceof BorderPane) {
                    // Create a new button with an "X" label
                    Button closeButton = new Button("X");
                    BorderPane.setAlignment(closeButton, Pos.TOP_RIGHT);
                    BorderPane.setMargin(closeButton, new Insets(5, 5, 0, 0));

                    // Set the button's size and position as needed
                    closeButton.setPrefWidth(20);
                    closeButton.setPrefHeight(20);
                    closeButton.setLayoutX(((BorderPane) node).getWidth() - closeButton.getPrefWidth());
                    closeButton.setLayoutY(0);
                    closeButton.setStyle("-fx-text-fill: red;");

                    // Add an event handler to the button to handle clicks
                    closeButton.setOnAction(event -> {
                        String text = "";
                        // Remove the BorderPane from the FlowPane when the button is clicked
                        Node bottomNode = ((BorderPane) node).getBottom();
                        if (bottomNode instanceof Text) {
                            text = ((Text) bottomNode).getText();
                            deleteItem(text);
                        }
                        flowPane.getChildren().remove(node);
                    });

                    // Add the button to the BorderPane's top region
                    ((BorderPane) node).setTop(closeButton);
                }
            }
        }
        else {
            // Loop through the child nodes of the FlowPane
            for (Node node : flowPane.getChildren()) {
                // Check if the node is a BorderPane
                if (node instanceof BorderPane) {
                    // Remove the button from the BorderPane's top region
                    ((BorderPane) node).setTop(null);
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            initializeDatabase();
            initializeStyles();
            initializeItems();
        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Now using host = " + host + ", portNumber = " + portNumber);
        try {
            clientSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (clientSocket != null && os != null && is != null) {
            new Thread(new SceneController()).start();

            os.println("hey there!");

        }
    }

    private void initializeItems() throws SQLException, FileNotFoundException {
        updateItems();
        for(Item item : items) {
            System.out.println("new Item: " + item.getName());
            BorderPane borderPane = new BorderPane();

            DropShadow dropShadow = new DropShadow();
            dropShadow.setOffsetX(0);
            dropShadow.setOffsetY(2);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.5));

            borderPane.setEffect(dropShadow);

            borderPane.setOnMouseEntered(event -> {
                borderPane.setEffect(dropShadow);
                borderPane.setScaleX(1.1);
                borderPane.setScaleY(1.1);
            });

            borderPane.setOnMouseExited(event -> {
                borderPane.setEffect(null);
                borderPane.setScaleX(1.0);
                borderPane.setScaleY(1.0);
            });

            borderPane.setStyle("-fx-background-color: grey; -fx-background-radius: 10;");

            borderPane.setPrefSize(100, 100);
            borderPane.setMaxSize(100, 100);

            Text text = new Text(item.getName().toUpperCase());
            text.setStyle("-fx-fill: white; -fx-font-size: 14; -fx-font-family: 'SansSerif';");

            BorderPane.setMargin(text, new Insets(0, 0, 10, 0));
            BorderPane.setAlignment(text, javafx.geometry.Pos.CENTER);
            borderPane.setBottom(text);

            String fileName = item.getName();

            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".tiff") || fileName.endsWith(".CR2")) {
                FileInputStream inputstream = new FileInputStream("src/main/resources/com/smallplayz/hackathon2023e/pngpic.png");
                Image image = new Image(inputstream);
                ImageView img1 = new ImageView(image);;
                img1.setFitWidth(60);
                img1.setFitHeight(60);
                borderPane.setCenter(img1);
            }

            borderPane.setOnMouseClicked(event -> {
                System.out.println("Download(" + fileName + ")");
                if (clientSocket != null && os != null && is != null) {
                    os.println("Download(" + fileName + ")");
                }
            });

            flowPane.getChildren().add(borderPane);
            FlowPane.setMargin(borderPane, new Insets(15));
        }
    }

    private void initializeStyles() {
        newButton.getStyleClass().setAll("btn","btn-primary");
        editButton.getStyleClass().setAll("btn","btn-info");
    }

    public void initializeDatabase() throws SQLException {
        // Open a connection
        Connection conn = DriverManager.getConnection("jdbc:sqlite:items.db");

        // Check if the "items" table exists
        Statement stmt = conn.createStatement();
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='items'";
        ResultSet rs = stmt.executeQuery(sql);
        boolean tableExists = rs.next();

        if (tableExists) {
            System.out.println("Table already exists");
        } else {
            // Execute a query to create the "items" table
            sql = "CREATE TABLE items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)";
            stmt.executeUpdate(sql);
            System.out.println("Table created successfully");
        }
        String sql2 = "SELECT * FROM items";
        ResultSet rs2 = stmt.executeQuery(sql2);
        while (rs2.next()) {
            int id = rs2.getInt("id");
            String name = rs2.getString("name");
            Item item = new Item(id, name);
            items.add(item);
        }
        System.out.println(items);

        conn.close();
    }

    private void updateItems() throws SQLException {

        items.clear();

        Connection conn = DriverManager.getConnection("jdbc:sqlite:items.db");

        // Check if the "items" table exists
        Statement stmt = conn.createStatement();

        String sql2 = "SELECT * FROM items";
        ResultSet rs2 = stmt.executeQuery(sql2);

        while (rs2.next()) {
            int id = rs2.getInt("id");
            String name = rs2.getString("name");
            Item item = new Item(id, name);
            items.add(item);
        }

        conn.close();
    }

    public void insertItem(String name) {

        name = name.replace(" ", "");

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // Register JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Open a connection
            conn = DriverManager.getConnection("jdbc:sqlite:items.db");

            // Prepare a statement for inserting a new row into the "items" table
            String sql = "INSERT INTO items(name) VALUES(?)";
            stmt = conn.prepareStatement(sql);

            // Set the value of the "name" column in the new row
            stmt.setString(1, name);

            // Execute the insert statement
            stmt.executeUpdate();

            System.out.println("Item inserted successfully");


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void deleteItem(String name) {

        name = name.replace(" ", "");

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // Register JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Open a connection
            conn = DriverManager.getConnection("jdbc:sqlite:items.db");

            // Prepare a parameterized statement for deleting a row from the "items" table by name
            String sql = "DELETE FROM items WHERE name = ?";
            stmt = conn.prepareStatement(sql);

            // Set the value of the "name" parameter
            stmt.setString(1, name);

            // Execute the delete statement
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted == 0) {
                System.out.println("Item with name " + name + " not found in database.");
            } else {
                System.out.println("Item deleted successfully");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void run() {
        String responseLine;
        try {
            while ((responseLine = is.readLine()) != null) {
                if(responseLine.startsWith("Download(")) {
                    String binaryString = responseLine.substring(9);
                    System.out.println("Binary download!!");

                    FileOutputStream outputStream = new FileOutputStream("src/main/java/com/smallplayz/hackathon2023e/test.png");
                    for (int i = 0; i < binaryString.length(); i += 8) {
                        if(i+9 <=binaryString.length()) {
                            String binaryByte = binaryString.substring(i, i + 8);
                            int decimalByte = Integer.parseInt(binaryByte, 2);
                            outputStream.write(decimalByte);
                        }
                    }
                    outputStream.close();

                    System.out.println("File copied successfully.");

                }
                System.out.println(responseLine);
            }
            closed = true;
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }
}