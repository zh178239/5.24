package view;

import controller.*;
import controller.StageController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class StartFrame {

    MyTitle label=new MyTitle("华容道");
    MyButton login=new MyButton("用户登录");
    MyButton register=new MyButton("注册");
    MyButton guest=new MyButton("游客登录");
    public static GridPane grid=new GridPane();
    public StartFrame(Stage stage, StageController stageController, Client client,LoginController loginController) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(label,0,0);
        grid.add(login,0,1);
        grid.add(register,0,3);
        grid.add(guest,0,2);
        register.setOnAction(e->{
            stageController.showRegister();
        });
        login.setOnAction(e->{
            stageController.showLogin();
        });
        guest.setOnAction(e->{
            try {
                loginController.onGuest(stage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    public GridPane getGridPane() {
        return grid;
    }
}
