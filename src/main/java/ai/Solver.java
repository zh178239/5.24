package ai;
import model.*;
import java.util.*;

public class Solver {
    private final Setting setting;
    private GameBoard board;
    public Solver(Setting setting,GameBoard board) {
        this.setting = setting;
        this.board = board;
    }
    //返回最短的通关路径
    public List<Move> solve() throws Exception {
        State start =new State(board.piecesAndPoses);
        Queue<State> queue=new ArrayDeque<>();
        Set<String> visited=new HashSet<>();

        queue.add(start);
        visited.add(start.encode());

        while(!queue.isEmpty()) {
            State current=queue.poll();
            if(current.isWin(setting.winCondition)){
                return current.reconstructPath();
            }
            for(Move move: current.possibleMoves()){
                State next=current.move(move);
                String key= next.encode();
                //加入并返回是否存在key
                if(visited.add(key)){
                    next.setParent(current);
                    next.setVia(move);
                    queue.add(next);
                }
            }
        }
        return Collections.emptyList();
    }
}
