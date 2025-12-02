import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int BOARD_SIZE = 8;
    private final Piece[][] grid;
    private Move lastMove;

    public Board(){
        this.grid = new Piece[BOARD_SIZE][BOARD_SIZE];
        this.lastMove = null;
        initialize();
    }

    private Board(boolean isEmpty){
        this.grid = new Piece[BOARD_SIZE][BOARD_SIZE];
        this.lastMove = null;
    }

    private void initialize(){
        //흑팀 기물 배치
        grid[0][0] = new Rook(Color.BLACK, new Position(0,0));
        grid[0][1] = new Knight(Color.BLACK, new Position(0,1));
        grid[0][2] = new Bishop(Color.BLACK, new Position(0,2));
        grid[0][3] = new Queen(Color.BLACK, new Position(0,3));
        grid[0][4] = new King(Color.BLACK, new Position(0,4));
        grid[0][5] = new Bishop(Color.BLACK, new Position(0,5));
        grid[0][6] = new Knight(Color.BLACK, new Position(0,6));
        grid[0][7] = new Rook(Color.BLACK, new Position(0,7));
        for(int k = 0; k < 8; k++){
            grid[1][k] = new Pawn(Color.BLACK, new Position(1,k));
        }

        //백팀 기물 배치
        grid[7][0] = new Rook(Color.WHITE, new Position(7,0));
        grid[7][1] = new Knight(Color.WHITE, new Position(7,1));
        grid[7][2] = new Bishop(Color.WHITE, new Position(7,2));
        grid[7][3] = new Queen(Color.WHITE, new Position(7,3));
        grid[7][4] = new King(Color.WHITE, new Position(7,4));
        grid[7][5] = new Bishop(Color.WHITE, new Position(7,5));
        grid[7][6] = new Knight(Color.WHITE, new Position(7,6));
        grid[7][7] = new Rook(Color.WHITE, new Position(7,7));
        for(int k = 0; k < 8; k++){
            grid[6][k] = new Pawn(Color.WHITE, new Position(6,k));
        }

        //나머지 칸은 생성자에 의해 null로 초기화
    }

    // 지정된 위치의 Piece 객체 반환
    // parameter: 확인할 위치
    // return 해당 위치의 Piece 객체 없으면 null반환
    public Piece getPieceAt(Position pos){
        if(!pos.isWithinBoard()){
            return null;
        }
        return grid[pos.getY()][pos.getX()];
    }

    //지정된 위치에 기물을 설정
    void setPieceAt(Position pos, Piece piece){
        if(pos.isWithinBoard()){
            grid[pos.getY()][pos.getX()] = piece;
        }
    }

    //해당 위치가 비어있는지 확인
    public boolean isEmpty(Position pos){
        return getPieceAt(pos) == null;
    }

    //해당 위치에 적 기물이 있는지 확인
    public boolean isEnemy(Position pos, Color myColor){
        Piece piece = getPieceAt(pos);
        return piece != null && piece.getColor() != myColor;
    }

    //앙파상 검사를 위해 마지막으로 수행된 이동 반환
    public Move getLastMove(){
        return this.lastMove;
    }

    public void display(){
        System.out.print("   ");
        for(int x = 0; x < BOARD_SIZE; x++){
            System.out.print("  " + x + " ");
        }
        System.out.println();
        printDivider();

        for(int y = 0; y < BOARD_SIZE; y++){
            System.out.print(" " + y + " ");
            for(int x = 0; x < BOARD_SIZE; x++){
                Piece piece = grid[y][x];
                char pieceChar = getPieceChar(piece);
                System.out.print("| " + pieceChar + " ");
            }
            System.out.println("|");
            printDivider();
        }
    }

    //보드판의 가로줄을 그림
    private void printDivider(){
        System.out.print("   ");
        for(int x = 0; x < BOARD_SIZE; x++){
            System.out.print("----");
        }
        System.out.println("-");
    }

    //Piece 객체를 보드판 출력용 문자로 변환
    private char getPieceChar(Piece piece){
        if(piece == null){
            return '.';
        }

        char c;
        if(piece instanceof Pawn){
            c = 'p';
        }
        else if(piece instanceof Knight){
            c = 'n';
        }
        else if(piece instanceof Rook){
            c = 'r';
        }
        else if(piece instanceof Bishop){
            c = 'b';
        }
        else if(piece instanceof Queen){
            c = 'q';
        }
        else if(piece instanceof King){
            c = 'k';
        }
        else{
            //알수없는 기물
            c = '?';
        }
        if(piece.getColor() == Color.WHITE){
            return Character.toUpperCase(c);
        }
        return c;
    }

    //기물 이동 수행 로직(이동 및 잡기) (move: from to 이동 정보 가지고 있음)
    //체스 판의 데이터를 Board에서 가지고 있으므로 이동은 Board 클래스에서 수행
    public Piece executeMove(Move move){
        Position from = move.from();
        Position to = move.to();

        Piece pieceToMove = getPieceAt(from);
        if(pieceToMove == null){
            return null;//이동할 기물X
        }

        Piece capturedPiece = getPieceAt(to);//잡힐 기물
        //기물 이동 기물을 위치로 옮기고 원래 자리는 null로
        setPieceAt(to,pieceToMove);
        setPieceAt(from,null);

        //기물 객체 내부 좌표 업데이트
        pieceToMove.setPosition(to);
        pieceToMove.setHasMoved(true);

        this.lastMove = move;

        return capturedPiece;
    }

    //앙파상 이동 처리
    public void executeEnPassant(Move pawnMove){
        Piece pawn = getPieceAt(pawnMove.from());
        //폰을 목적지로 이동
        executeMove(pawnMove);

        //잡힌 상태 폰을 제거(앙파상은 목적지에 기물이 없음)
        int capturedPawnY = (pawn.getColor() == Color.WHITE) ? pawnMove.to().getY() + 1: pawnMove.to().getY() - 1;
        Position capturedPawnPos = new Position(capturedPawnY,pawnMove.to().getX());
        setPieceAt(capturedPawnPos,null);
    }

    //캐슬링 이동 처리
    public void executeCastling(Move KingMove){
        Position from = KingMove.from();
        Position to = KingMove.to();

        //킹 먼저 이동
        executeMove(KingMove);

        //킹의 이동 방향에 따라 룩을 이동
        Position rookFrom, rookTo;
        if(to.getX() > from.getX()){
            rookFrom = new Position(from.getY(),7); // Y 5
            rookTo = new Position(from.getY(),5); // Y 7
        }
        else{
            rookFrom = new Position(to.getY(),0); // Y 0
            rookTo = new Position(to.getY(),3); // Y 3
        }

        Piece rook = getPieceAt(rookFrom);
        if(rook != null){
            setPieceAt(rookTo,rook);
            setPieceAt(rookFrom,null);
            rook.setPosition(rookTo);
            rook.setHasMoved(true);
        }
    }

    public Board copy(){
        Board newBoard = new Board(true);

        for(int y = 0; y < BOARD_SIZE; y++){
            for(int x = 0; x < BOARD_SIZE; x++){
                Piece p = this.grid[y][x];
                if(p != null){
                    newBoard.grid[y][x] = createPieceCopy(p);
                }
            }
        }
        newBoard.lastMove = this.lastMove;
        return newBoard;
    }

    // Piece 객체를 깊은 복사
    private Piece createPieceCopy(Piece p){
        Piece newPiece;

        if(p instanceof Pawn){
            newPiece = new Pawn(p.getColor(), p.getPosition());
        }
        else if(p instanceof Rook){
            newPiece = new Rook(p.getColor(), p.getPosition());
        }
        else if(p instanceof Knight){
            newPiece = new Knight(p.getColor(), p.getPosition());
        }
        else if(p instanceof Bishop){
            newPiece = new Bishop(p.getColor(), p.getPosition());
        }
        else if(p instanceof Queen){
            newPiece = new Queen(p.getColor(), p.getPosition());
        }
        else if(p instanceof King){
            newPiece = new King(p.getColor(), p.getPosition());
        }
        else return null;

        // hasMoved 도 복사
        newPiece.setHasMoved(p.hasMoved());
        return newPiece;
    }
}
