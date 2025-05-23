package controller;

import ai.*;
import model.*;
import view.GameFrame;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import view.LoginFrame;
import view.RegisterFrame;

import javax.sound.sampled.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    public GameBoard gameBoard;
    public Setting setting;
    private Stage stage;
    private GameFrame frame;
    public ArrayList<Piece> piecesMoved= new ArrayList<>();
    public ArrayList<Direction> directionsMoved = new ArrayList<>();
    public Music music=new Music();
    

    public Game(){

    }

    public Game(Setting setting,GameBoard gameBoard)
    {
        this.gameBoard = gameBoard;
        this.setting = setting;
    }

    public void initialize(Setting setting) throws Exception {
        this.setting = setting;
        gameBoard.clear();
        piecesMoved.clear();
        directionsMoved.clear();

        for (PieceAndPos pieceAndPos : setting.piecesAndPoses) {
            gameBoard.put(pieceAndPos.piece, pieceAndPos.h, pieceAndPos.w);
        }
    }



    public GameFrame start(Stage stage,Client client) throws Exception {
        frame=new GameFrame(stage , gameBoard, this);
        return frame;
    }


    public void step(Piece piece, Direction direction) {
        try {
            if (gameBoard.ableToMove(piece, direction)) {
                gameBoard.move(piece, direction);
                piecesMoved.add(piece);
                directionsMoved.add(direction);

                // 更新 JavaFX 控件中的状态
                Platform.runLater(() -> {
                    music.moveSound();
                    frame.gamePanel.update(piece, direction);
                    // 检查是否达成胜利条件
                    PieceAndPos winCondition = setting.winCondition;
                    if (gameBoard.pieceAtPos(winCondition.piece, winCondition.h, winCondition.w)) {
                        end();
                    }
                });
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void undo() throws Exception {
        int n =piecesMoved.size()-1;
        gameBoard.move(piecesMoved.get(n),directionsMoved.get(n).reverse());
        piecesMoved.remove(n);
        directionsMoved.remove(n);
    }


    public void end() {
        ;
    }
    //存档用户进度
    public void saveProgress(String username)
    {
        List<String> names = new ArrayList<>();
        for (Piece p : piecesMoved) {
            names.add(p.getId());
        }
        GameProgress progress = new GameProgress(names, new ArrayList<>(directionsMoved));

        //复制集合元素
        File dir=new File("saves");
        System.out.println("Directory exists: " + dir.exists()); // 检查目录是否存在
        if(!dir.exists()){
            System.out.println("Creating directory...");
            boolean created = dir.mkdirs();
            System.out.println("Directory created: " + created);

        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(dir, username + ".sav")))) {
            System.out.println("Writing object...");
            out.writeObject(progress);
            System.out.println("Object written successfully.");
            System.out.println("存档成功: " + dir.getAbsolutePath() + "/" + username + ".sav");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //从文件读回进度，没有存档就返回null
    public static GameProgress loadProgress(String username) throws IOException {
        File f =new File("saves",username+".sav");
        if(!f.exists()) return null;
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(f))){
            GameProgress progress = (GameProgress) in.readObject();
            System.out.println("存档读取成功");

            return progress;

        }catch(Exception e){
            e.printStackTrace();
            throw new IOException("存档损坏",e);
        }
    }
    public void aiPrint(){
        Solver solver = new Solver(setting,gameBoard);
        try {
            List<Move> bestMoves=solver.solve();
            System.out.println("最少的步数为"+bestMoves.size());
            for(Move m : bestMoves){
                System.out.println(m);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void aiSolve() throws Exception {
        Solver solver = new Solver(setting,gameBoard);
        List<Move> bestMoves=solver.solve();
        for(Move m : bestMoves){
            step(m.piece, m.direction);
        }

    }

}