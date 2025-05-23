package view;

import controller.Client;
import controller.Game;
import controller.LoginController;
import controller.StageController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserLib;

import view.MyLabel;
import java.util.ArrayList;

import static view.LoginFrame.username;

public class UserFrame extends UserLib{
    private Game game;
    private Stage stage;
    Label User=new Label("用户"+username);
    Button startgame=new Button("开始新游戏");
    Button file=new Button("读取存档");
    Button line=new Button("排行榜");

    public UserFrame(Stage stage, Client client, StageController stageController, LoginController loginController) {
        VBox vBox=new VBox();
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(User,startgame,file,line);
        startgame.setOnAction(e->{

        });
    }

    private void displayLine() {

    }



}
