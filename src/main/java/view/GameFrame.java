package view;
import controller.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Direction;
import model.GameBoard;
import model.*;

import static view.LoginFrame.username;

public class GameFrame extends VBox{
    public GamePanel gamePanel;
    private Stage stage;
    private GameBoard gameBoard;
    private Game game;
    private int number;
    private Label Time;
    private Label Leftnumber;
    private Label Step;
    private ControlPanel controlPanel;
    private Piece piece;

    public GameFrame(Stage stage,GameBoard gameBoard, Game game) throws Exception {
        this.game = game;
        long seconds =System.currentTimeMillis();
        int steps = game.piecesMoved.size();
        number=3;
        stage.setTitle("Klotski");
        Time = new Label("时间:"+seconds);
        Step = new Label("步数:"+ steps);
        Button reserve = new Button("撤销");
        Leftnumber = new Label("次数"+ number);
        Label UserName=new Label("用户："+ username);
        Button restart=new Button("重置");
        Button save=new Button("存档");
        Button ai=new Button("AI");
        if(username==null){
            save.setDisable(true);
        }else{
            save.setDisable(false);
        }
        save.setOnAction(e->{
            game.saveProgress(username);
        });
        stage.setOnCloseRequest(windowEvent -> {
            game.saveProgress(username);
        });
        gamePanel = new GamePanel(gameBoard, game, this);

        ai.setOnAction(e->{
            try {
                game.aiSolve();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        reserve.setOnAction(e -> {
            try {
                int n=game.piecesMoved.size()-1;
                if(n>0){
                    Piece pieceN = game.piecesMoved.get(n);
                    Direction direction = game.directionsMoved.get(n).reverse();
                    game.undo();
                    gamePanel.update(pieceN,direction);
                    number--;
                }
                if(number==0){
                    reserve.setDisable(true);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        this.setSpacing(10); // 设置子节点间距
        this.setPadding(new Insets(10)); // 设置内边距
        restart.setOnAction(e -> {
            try {
                game.initialize(game.setting);
                Platform.runLater(() -> {
                    gamePanel.fresh(null, null, 1.0);
                });
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        piece =gamePanel.selectedPiece;
        controlPanel=new ControlPanel(game,gameBoard,piece);

        HBox topBar = new HBox(20);
        topBar.getChildren().addAll(UserName,Time, Step);
        HBox re=new HBox(reserve,Leftnumber);
        VBox buttonBar = new VBox(50);
        buttonBar.getChildren().addAll(save,ai,reserve,restart,re,controlPanel);
        HBox P = new HBox(40);
        P.getChildren().addAll(gamePanel,buttonBar);
        this.getChildren().addAll(topBar,P);
    }

    public void startTimer() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> updateLabels()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateLabels() {
        long seconds = System.currentTimeMillis() / 1000;
        Time.setText("时间: " + seconds);
        Step.setText("步数: " + game.piecesMoved.size());
        Leftnumber.setText("次数: " + number);
    }

}