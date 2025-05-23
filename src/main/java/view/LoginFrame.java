package view;

import controller.Client;
import controller.LoginController;
import controller.StageController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.UserLib;
import controller.Game;

public class LoginFrame {
    MyLabel usernameLabel = new MyLabel("用户名:");
    TextField usernameField = new TextField();
    MyLabel passwordLabel = new MyLabel("密码:");
    TextField passwordField = new TextField();
    MyButton loginButton = new MyButton("登录");
    MyButton registerButton=new MyButton("注册");
    public static String username;
    public static String password;
    public static GridPane gridPane=new GridPane();
    private Client client;

    public LoginFrame(Stage stage, StageController stageController, Client client, LoginController login) {
        this.client=client;
        registerButton.setOnAction(e -> {
            stageController.showRegister();
        });
        loginButton.setOnAction(e -> {
            username = usernameField.getText();
            password = passwordField.getText();

            if(client.isMatch(username,password)){
            try {
                login.onLogin(username,stage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            }else{
                invalidateLogin(username,password);
            }
        });
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));

        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordLabel, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);
        gridPane.add(registerButton, 1, 3);
    }
    private boolean invalidateLogin(String username, String password) {
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "输入错误", "用户名不能为空");
            return false;
        }
        if (password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "输入错误", "密码不能为空");
            return false;
        }
        if (!client.isMatch(username,password)) {
            showAlert(Alert.AlertType.ERROR, "输入错误", "用户名或者密码输入错误");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public GridPane getGridPane() {
        return gridPane;
    }

}

