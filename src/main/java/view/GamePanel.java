package view;
import controller.Game;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.Direction;
import model.GameBoard;
import model.Piece;
import model.PieceAndPos;

public class GamePanel extends Pane {
    public Piece selectedPiece;
    public Runnable onPieceSelected;
    private GameBoard gameBoard;
    private final Game game;
    private final GameFrame gameFrame;
    public int unitSize = 100;
    private Timeline animationTimeline;
    private double animationLength = 0.15;
    private double xPressed, yPressed, xReleased, yReleased;
    private int selectedBoardX;
    private int selectedBoardY;
    private int selectedPieceW;
    private int selectedPieceH;
    Pane selector = new Pane();
    private Timeline selectorTimeline;
    private double selectorAnimationRate;
    private Direction selectorAnimationDirection;
    private Piece animatedSelectorPiece;


    public GamePanel(GameBoard gameBoard, Game game, GameFrame gameFrame) {
        this.gameBoard = gameBoard;
        this.game = game;
        this.gameFrame = gameFrame;

        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> adjustUnitSize();
        widthProperty().addListener(sizeListener);
        heightProperty().addListener(sizeListener);

        setFocusTraversable(true);
        setOnKeyPressed(this::handleKeyPress);

        if (!gameBoard.piecesAndPoses.isEmpty()) {
            PieceAndPos first = gameBoard.piecesAndPoses.get(0);
            selectedBoardX = first.w;
            selectedBoardY = first.h;
            selectedPiece = first.piece;
            selectedPieceW = first.piece.width;
            selectedPieceH = first.piece.height;
        }
        adjustUnitSize();
        fresh(null, null, 1.0);
    }

    private PieceAndPos findPieceAt(int boardX, int boardY) {
        for (PieceAndPos pap : gameBoard.piecesAndPoses) {
            if (boardX >= pap.w && boardX < pap.w + pap.piece.width &&
                    boardY >= pap.h && boardY < pap.h + pap.piece.height) {
                return pap;
            }
        }
        return null;
    }

    public void update(Piece animatedPiece, Direction direction) {
        if (animationTimeline != null) {
            animationTimeline.stop();
        }

        final long startTime = System.nanoTime();
        final double totalDuration = animationLength * 1_000_000_000.0; // 转换为纳秒

        animationTimeline = new Timeline(
                new KeyFrame(Duration.millis(5), e -> {
                    long elapsed = System.nanoTime() - startTime;
                    double animatedRate = Math.min(elapsed / totalDuration, 1.0);
                    fresh(animatedPiece, direction, animatedRate);

                    if (animatedRate >= 1.0) {
                        animationTimeline.stop();
                    }
                })
        );
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline.play();

        if (animatedPiece == selectedPiece) {
            startSelectorAnimation(direction);
        }
    }

    public void fresh(Piece animatedPiece, Direction direction, double animatedRate) {
        getChildren().clear();

        for (PieceAndPos pieceAndPos : gameBoard.piecesAndPoses) {
            Piece piece = pieceAndPos.piece;
            Pane pieceNode = new Pane();
            // 设置颜色和样式
            Color color = piece.color;
            pieceNode.setStyle("-fx-background-color: rgba("
                    + (int) (color.getRed() * 255) + ","
                    + (int) (color.getGreen() * 255) + ","
                    + (int) (color.getBlue() * 255) + ","
                    + color.getOpacity() + ");"
                    + "-fx-border-color: black;");

            // 设置位置和大小
            double x = pieceAndPos.w * unitSize;
            double y = pieceAndPos.h * unitSize;
            if (animatedPiece == piece) {
                x = (pieceAndPos.w - direction.w) * unitSize + direction.w * unitSize * animatedRate;
                y = (pieceAndPos.h - direction.h) * unitSize + direction.h * unitSize * animatedRate;
            }

            pieceNode.setLayoutX(x);
            pieceNode.setLayoutY(y);
            pieceNode.setPrefSize(piece.width * unitSize, piece.height * unitSize);

            pieceNode.setOnMouseClicked(event -> {
                selectedPiece = piece;
                selectedPieceW = pieceAndPos.piece.width;
                selectedPieceH = pieceAndPos.piece.height;
                selectedBoardX = pieceAndPos.w;
                selectedBoardY = pieceAndPos.h;
                updateSelector();
            });

            pieceNode.setOnMousePressed(event -> {
                if (event.isPrimaryButtonDown()) {
                    xPressed = event.getX();
                    yPressed = event.getY();
                }
            });

            pieceNode.setOnMouseReleased(event -> {
                xReleased = event.getX();
                yReleased = event.getY();

                double xDiff = xReleased - xPressed;
                double yDiff = yReleased - yPressed;

                if (xDiff < 0.25 && yDiff < 0.25) {
                    return;
                }
                if (Math.abs(xDiff) > Math.abs(yDiff)) {
                    if (xDiff > 0) {
                        game.step(piece, Direction.RIGHT);
                    } else {
                        game.step(piece, Direction.LEFT);
                    }
                } else {
                    if (yDiff > 0) {
                        game.step(piece, Direction.DOWN);
                    } else {
                        game.step(piece, Direction.UP);
                    }
                }
            });
            getChildren().add(pieceNode);
        }
        getChildren().add(selector);
        requestLayout();
    }

    private void adjustUnitSize() {
        double panelWidth = getWidth();
        double panelHeight = getHeight();

        if (panelWidth > 0 && panelHeight > 0) {
            int cols = gameBoard.occupancyMap[0].length;
            int rows = gameBoard.occupancyMap.length;

            unitSize = (int) Math.min(panelWidth / cols, panelHeight / rows);
            setPrefSize(unitSize * cols, unitSize * rows);
            fresh(null, null, 1.0);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        event.consume();
        if (code == KeyCode.SPACE) {
            selectedPiece = findPieceAt(selectedBoardX, selectedBoardY).piece;
            updateSelector();
                if (selectedPiece != null) {
                    for (PieceAndPos pap : gameBoard.piecesAndPoses) {
                        if (pap.piece == selectedPiece) {
                            selectedPieceW = pap.w;
                            selectedPieceH = pap.h;
                            break;
                        }
                    }
                }
        }
        if (code == KeyCode.BACK_SPACE) {
            selectedPiece = null;
        }
        int newX = selectedBoardX;
        int newY = selectedBoardY;
        Direction direction=null;
        if (selectedPiece == null) {
                switch (code) {
                    case UP -> {
                        newY--;
                    }
                    case DOWN -> {
                        newY++;
                    }
                    case LEFT -> {
                        newX--;
                    }
                    case RIGHT -> {
                        newX++;
                    }
                };
            }else{
                switch (code) {
                    case UP -> {
                        direction=Direction.UP;
                        try {
                            if(gameBoard.ableToMove(selectedPiece,direction)){
                                newY--;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case DOWN -> {
                        direction=Direction.DOWN;
                        try {
                            if(gameBoard.ableToMove(selectedPiece,direction)){
                                newY++;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case LEFT -> {
                        direction=Direction.LEFT;
                        try {
                            if(gameBoard.ableToMove(selectedPiece,direction)){
                                newX--;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case RIGHT -> {
                        direction=Direction.RIGHT;
                        try {
                            if(gameBoard.ableToMove(selectedPiece,direction)){
                                newX++;
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            if (direction != null) {
                try {
                    game.step(selectedPiece, direction);
                } catch (Exception e) {
                    System.err.println("移动失败: " + e.getMessage());
                }
            }
            int cols = gameBoard.occupancyMap[0].length;
            int rows = gameBoard.occupancyMap.length;
            if (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
                selectedBoardX = newX;
                selectedBoardY = newY;
                updateSelector();
            }

    }

    private void startSelectorAnimation(Direction direction) {
        if (selectorTimeline != null) {
            selectorTimeline.stop();
        }

        final long selectorStartTime = System.nanoTime();
        final double totalDuration = animationLength * 1_000_000_000.0;

        selectorAnimationDirection = direction;
        animatedSelectorPiece = selectedPiece;

        selectorTimeline = new Timeline(
                new KeyFrame(Duration.millis(5), e -> {
                    long elapsed = System.nanoTime() - selectorStartTime;
                    selectorAnimationRate = Math.min(elapsed / totalDuration, 1.0);
                    updateSelectorPosition();

                    if (selectorAnimationRate >= 1.0) {
                        selectorTimeline.stop();
                        // 动画结束后更新最终位置
                        updateStaticSelectorPosition();
                    }
                })
        );
        selectorTimeline.setCycleCount(Timeline.INDEFINITE);
        selectorTimeline.play();
    }

    // 更新选择框位置（动画中）
    private void updateSelectorPosition() {
        PieceAndPos pap = findPieceAt(selectedBoardX, selectedBoardY);
        if (pap != null && pap.piece == animatedSelectorPiece) {
            double x = (pap.w - selectorAnimationDirection.w) * unitSize
                    + selectorAnimationDirection.w * unitSize * selectorAnimationRate;
            double y = (pap.h - selectorAnimationDirection.h) * unitSize
                    + selectorAnimationDirection.h * unitSize * selectorAnimationRate;

            selector.setLayoutX(x);
            selector.setLayoutY(y);
        }
    }

    // 更新选择框位置（静态）
    private void updateStaticSelectorPosition() {
        PieceAndPos pap = findPieceAt(selectedBoardX, selectedBoardY);
        if (pap != null) {
            selector.setLayoutX(pap.w * unitSize);
            selector.setLayoutY(pap.h * unitSize);
            selector.setPrefSize(pap.piece.width * unitSize, pap.piece.height * unitSize);
        }
    }

    // 修改后的updateSelector方法
    private void updateSelector() {
        getChildren().remove(selector);
        selector = new Pane();
        selector.setStyle("-fx-border-color: rgba(0,255,0,0.7); -fx-border-width: 3px;");

        if (selectedPiece != null) {
            PieceAndPos pap = findPieceAt(selectedBoardX, selectedBoardY);
            if (pap != null) {
                selector.setPrefSize(pap.piece.width * unitSize, pap.piece.height * unitSize);
                // 如果当前没有动画才设置静态位置
                if (selectorTimeline == null || !selectorTimeline.getStatus().equals(Animation.Status.RUNNING)) {
                    selector.setLayoutX(pap.w * unitSize);
                    selector.setLayoutY(pap.h * unitSize);
                }
            }
        } else {
            selector.setPrefSize(unitSize, unitSize);
            selector.setLayoutX(selectedBoardX * unitSize);
            selector.setLayoutY(selectedBoardY * unitSize);
            selector.setStyle("-fx-border-color: rgba(0,255,0,0.5); -fx-border-width: 2px;");
        }
        getChildren().add(selector);
    }

}