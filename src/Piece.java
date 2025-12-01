import java.util.List;
import java.util.ArrayList;

abstract class Piece {
    protected Color color; //팀 색상
    protected Position position; // 현재 위치
    protected boolean hasMoved; // 한번이라도 움직였나? (폰 두칸 이동 및 캐슬링)

    public Piece(Color color, Position position){
        this.color = color;
        this.position = position;
        this.hasMoved = false;
    }

    //기물마다 움직임이 다르니 각자 구현해야함 추상클래스 사용
    public abstract List<Move> getValidMoves(Board board);

    public Color getColor(){
        return color;
    }
    public Position getPosition(){
        return position;
    }
    public boolean hasMoved(){
        return hasMoved;
    }
    public void setPosition(Position position){
        this.position = position;
    }
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    //이동 경로를 추가할때 사용
    protected Move createMove(Position to){
        return new Move(this.position, to);
    }
}

//나이트는 특정 좌표만 이동 가능하고 중간의 다른 기물이 있어도 넘어갈 수 있음
class Knight extends Piece{
    //나이트의 이동 가능 경로 8가지
    private static final int[][] MOVES = {
            {-2,-1}, {-2,1},{-1,-2},{-1,2},
            {1,-2},{1,2},{2,-1},{2,1}
    };

    public Knight(Color color, Position position) { super(color, position);}

    @Override
    public List<Move> getValidMoves(Board board){
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < 8; i++){
            int[] move = MOVES[i];

            Position to = new Position(position.getY() + move[0],position.getX() + move[1]);

            //목표가 보드 안인지, 같은 편 기물인지 확인
            if(to.isWithinBoard()){
                Piece target = board.getPieceAt(to);
                if(target == null || target.getColor() != this.color){
                    //이동 가능 목록에 추가
                    moves.add(createMove(to));
                }
            }
        }
        //이동 가능 목록 반환
        return moves;
    }
}

class Rook extends Piece{
    private static final int[][] DIRS = {
            {-1,0},{1,0},{0,-1},{0,1}
    };

    public Rook(Color color, Position position) { super(color,position);}

    @Override
    public List<Move> getValidMoves(Board board){
        List<Move> moves = new ArrayList<>();

        for(int i = 0; i < 4; i++){
            int[] move = DIRS[i];

            Position to = new Position(position.getY() + move[0], position.getX() + move[1]);

            while(to.isWithinBoard()){
                Piece target = board.getPieceAt(to);

                if(target == null) {
                    moves.add(createMove(to));
                    to = new Position(to.getY() + move[0], to.getX() + move[1]);
                }
                else if(target.getColor() != this.color){
                    moves.add(createMove(to));
                    break;
                }
                else{
                    break;
                }
            }
        }
        return moves;
    }
}

class Bishop extends Piece{
    private static final int[][] DIRS = {
            {-1,-1},{-1,1},{1,-1},{1,1}
    };

    public Bishop(Color color, Position position){
        super(color,position);
    }

    @Override
    public List<Move> getValidMoves(Board board){
        List<Move> moves = new ArrayList<>();

        for(int i = 0; i < 4; i++){
            int[] move = DIRS[i];
            Position to = new Position(position.getY() + move[0],position.getX() + move[1]);
            while(to.isWithinBoard()){
                Piece target = board.getPieceAt(to);
                if(target == null){
                    moves.add(createMove(to));
                    to = new Position(to.getY() + move[0], to.getX() + move[1]);
                }
                else if(target.getColor() != this.color){
                    moves.add(createMove(to));
                    break;
                }
                else{
                    break;
                }
            }
        }
        return moves;
    }
}

class Queen extends Piece{
    private static final int[][] DIRS = {
            {-1,0},{1,0},{0,-1},{0,1},
            {-1,-1},{-1,1},{1,-1},{1,1}
    };

    public Queen(Color color, Position position){
        super(color,position);
    }

    @Override
    public List<Move> getValidMoves(Board board){
        List<Move> moves = new ArrayList<>();

        for(int i = 0; i < 8; i++){
            int[] move = DIRS[i];
            Position to = new Position(position.getY() + move[0],position.getX() + move[1]);
            while(to.isWithinBoard()){
                Piece target = board.getPieceAt(to);
                if(target == null){
                    moves.add(createMove(to));
                    to = new Position(to.getY() + move[0], to.getX() + move[1]);
                }
                else if(target.getColor() != this.color){
                    moves.add(createMove(to));
                    break;
                }
                else{
                    break;
                }
            }
        }
        return moves;
    }
}

class King extends Piece{
    private static final int[][] DIRS = {
            {-1,0},{1,0},{0,-1},{0,1},
            {-1,-1},{-1,1},{1,-1},{1,1}
    };

    public King(Color color, Position position){
        super(color,position);
    }

    @Override
    public List<Move> getValidMoves(Board board){
        List<Move> moves = new ArrayList<>();

        for(int i = 0; i < 8; i++){
            int[] move = DIRS[i];
            Position to = new Position(position.getY() + move[0], position.getX() + move[1]);

            if(to.isWithinBoard()){
                Piece target = board.getPieceAt(to);
                if(target == null || target.getColor() != this.color){
                    moves.add(createMove(to));
                }
            }
        }
        return moves;
    }
}

class Pawn extends Piece{
    public Pawn(Color color, Position position){
        super(color,position);
    }

    @Override
    public List<Move> getValidMoves(Board board){
        List<Move> moves = new ArrayList<>();

        //흑이면 1 백이면 -1
        int direction = (this.color == Color.WHITE) ? -1 : 1;

        //한 칸 전진하기
        Position oneStep = new Position(position.getY() + direction, position.getX());
        if(oneStep.isWithinBoard() && board.isEmpty(oneStep)){
            moves.add(createMove(oneStep));

            if(!this.hasMoved()){
                Position twoStep = new Position(position.getY() + (2 * direction), position.getX());
                if(twoStep.isWithinBoard() && board.isEmpty(twoStep)){
                    moves.add(createMove(twoStep));
                }
            }
        }

        int[] captureCol = {position.getX() - 1, position.getX() + 1};
        for(int i = 0; i < 2; i++){
            int newX = captureCol[i];
            Position capturePos = new Position(position.getY() + direction, newX);

            if(capturePos.isWithinBoard() && board.isEnemy(capturePos, this.color)){
                moves.add(createMove(capturePos));
            }
        }
        return moves;
    }
}