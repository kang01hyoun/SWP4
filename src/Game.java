import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game{
    public enum GameState{
        RUNNING,
        CHECK,
        CHECKMATE,
        STALEMATE
    }

    private final Scanner scanner = new Scanner(System.in);

    private final Board board;
    private final Player playerWhite;
    private final Player playerBlack;
    private Player currentPlayer;
    private GameState state;

    //Game 객체 생성자
    public Game(){
        this.board = new Board();
        this.playerWhite = new Player(Color.WHITE);
        this.playerBlack = new Player(Color.BLACK);
        this.currentPlayer = playerWhite;
        this.state = GameState.RUNNING;
    }

    //메인 게임 루프
    public void start(){
        System.out.println("Welcome to the Game of Chess!");
        System.out.println("\nPRESS ENTER TO START");
        new Scanner(System.in).nextLine();

        //스테일메이트 체크메이트시 게임 종료
        while (state != GameState.STALEMATE && state != GameState.CHECKMATE){

            clearConsole();
            board.display();

            updateGameState();

            String playerColorStr = (currentPlayer.getColor() == Color.WHITE) ? "백" : "흑";
            if(state == GameState.CHECKMATE){
                System.out.println("게임 종료: "+playerColorStr+" 플레이어가 체크메이트 당했습니다\n");
                break;
            }
            if(state == GameState.STALEMATE){
                System.out.printf("게임 종료: 스테일메이트! 무승부입니다.\n", playerColorStr);
                break;
            }
            if(state == GameState.CHECK){
                System.out.printf("경고: 현재 %s 플레이어가 체크 상태입니다!\n",playerColorStr);
            }

            //player 1의 player 2 호출
            handleTurn();

            if(findKing(getOpponentColor(), this.board) == null){
                System.out.printf("%s 플레이어가 왕을 내어줍니다.\n",playerColorStr);
                System.out.println("게임을 종료합니다.");
                break;
            }

            switchPlayer();
        }

        board.display();
        System.out.println("게임이 종료되었습니다");
    }

    //현재 턴인 플레이어의 이동을 처리
    private void handleTurn(){
        boolean moveSuccessful = false;
        while(!moveSuccessful){

            Position from = currentPlayer.getMoveFromUser(board);
            Piece piece = board.getPieceAt(from);

            List<Move> legalMoves = getAllLegalMovesForPiece(piece);

            if(legalMoves.isEmpty()){
                System.out.println("이동 가능한 위치가 없습니다. 다른 기물을 선택해 주십시오,");
                continue;
            }

            System.out.print("이동 가능한 위치: ");
            for(int i = 0; i < legalMoves.size(); i++){
                Move move = legalMoves.get(i);
                System.out.println(move.to() + " ");
            }
            System.out.println();

            Move selectedMove = currentPlayer.getMoveToUser(from, legalMoves);

            executeMove(selectedMove,piece);
            moveSuccessful = true;

            handlePromotion(piece, selectedMove.to());
        }
    }


    private void updateGameState(){
        Color playerColor = currentPlayer.getColor();
        boolean inCheck = isKingInCheck(playerColor, board);
        boolean hasMoves = hasAnyLegalMoves(playerColor);

        if(inCheck && !hasMoves){
            state = GameState.CHECKMATE;
        }
        else if(!inCheck && !hasMoves){
            state = GameState.STALEMATE;
        }
        else if (inCheck){
            state = GameState.CHECK;
        }
        else{
            state = GameState.RUNNING;
        }
    }


    //특정 색상의 킹이 현재 체크 상태인지 확인
    // 가상의 보드를 만들어서 시뮬레이션
    private boolean isKingInCheck(Color kingColor, Board boardContext) {
        Position kingPos = findKing(kingColor, boardContext);
        if (kingPos == null) return true;

        Color opponentColor = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;

        List<Piece> opponentPieces = getPieces(opponentColor, boardContext);

        for (int i = 0; i < opponentPieces.size(); i++) {
            Piece opponentPiece = opponentPieces.get(i); // i번째 기물을 가져옴

            if (opponentPiece instanceof Pawn) {
                int direction = (opponentPiece.getColor() == Color.WHITE) ? -1 : 1;
                int[] attackCols = { opponentPiece.getPosition().getX() - 1, opponentPiece.getPosition().getX() + 1 };

                for (int j = 0; j < attackCols.length; j++) {
                    int x = attackCols[j]; // j번째 공격할 x좌표
                    Position attackPos = new Position(opponentPiece.getPosition().getY() + direction, x);
                    if (attackPos.equals(kingPos)) {
                        return true;
                    }
                }
            } else {
                List<Move> moves = opponentPiece.getValidMoves(boardContext);
                for (int k = 0; k < moves.size(); k++) {
                    Move move = moves.get(k); // k번째 이동 경로

                    if (move.to().equals(kingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    //킹을 위협에 빠트리지 않는 합법적인 이동만 필터링
    private List<Move> getStandardLegalMoves(Piece piece) {
        List<Move> legalMoves = new ArrayList<>();
        Color pieceColor = piece.getColor();

        List<Move> pseudoLegalMoves = piece.getValidMoves(board);

        for (int i = 0; i < pseudoLegalMoves.size(); i++) {
            Move move = pseudoLegalMoves.get(i);

            //가상 이동
            Board virtualBoard = board.copy(); // 보드 복사! (Deep Copy)
            virtualBoard.executeMove(move);    // 가상으로 이동 실행

            // 이동 후 *자신의* 킹이 여전히 안전한지(체크 상태가 아닌지) 확인
            if (!isKingInCheck(pieceColor, virtualBoard)) {
                legalMoves.add(move); // 안전하다면 합법적인 이동(Legal Move)으로 인정
            }
        }
        return legalMoves;
    }

    private boolean hasAnyLegalMoves(Color playerColor) {
        List<Piece> myPieces = getPieces(playerColor, board);
        for(int k = 0; k < myPieces.size(); k++){
            Piece piece = myPieces.get(k);
            // 한 기물이라도 합법적인 이동이 있으면 true반환
            if(!getAllLegalMovesForPiece(piece).isEmpty()){
                return true;
            }
        }
        return false;
    }

    private List<Move> getAllLegalMovesForPiece(Piece piece) {
        //킹을 위험에 빠트리지 않는 표준 이동
        List<Move> legalMoves = getStandardLegalMoves(piece);

        //캐슬링 추가
        if(piece instanceof King){
            legalMoves.addAll(getLegalCastlingMoves((King) piece));
        }

        if (piece instanceof Pawn){
            Move enPassantMove = getLegalEnPassantMove((Pawn) piece);

            if(enPassantMove != null){
                legalMoves.add(enPassantMove);
            }
        }
        return legalMoves;
    }

    private List<Move> getLegalCastlingMoves(King king) {
        List<Move> castlingMoves = new ArrayList<>();
        //킹이 움직인 적 없고, 현재 체크 상태가 아니여야 함
        if(king.hasMoved() || isKingInCheck(king.getColor(), board)){
            return castlingMoves;
        }

        int y =  king.getPosition().getY();
        Color color = king.getColor();

        // 룩 검사
        Piece leftRook = board.getPieceAt(new Position(y,0));
        if(leftRook instanceof Rook && !leftRook.hasMoved()){
            // 경로 검사 y 1 y 2 y 3이 비어있어야 함
            if(board.isEmpty(new Position(y,1)) && board.isEmpty(new Position(y,2)) && board.isEmpty(new Position(y,3))){
                // 경로 안전 감사 y 2 y 3이 공격받지 않아야 함.
                if(isSquareSafe(new Position(y,2), color) && isSquareSafe(new Position(y,3),color)){
                    castlingMoves.add(new Move(king.getPosition(),new Position(y,2)));
                }
            }
        }

        Piece rightRook = board.getPieceAt(new Position(y,7));
        if(rightRook instanceof Rook && !rightRook.hasMoved()){
            if(board.isEmpty(new Position(y,5)) && board.isEmpty(new Position(y,6))){
                if(isSquareSafe(new Position(y,5),color) && isSquareSafe(new Position(y,6),color)){
                    castlingMoves.add(new Move(king.getPosition(),new Position(y,6)));
                }
            }
        }
        return castlingMoves;
    }

    private Move getLegalEnPassantMove(Pawn pawn) {
        Move lastMove = board.getLastMove();
        if (lastMove == null) return null; // Optional.empty() 대신 null 반환

        Piece lastMovedPiece = board.getPieceAt(lastMove.to());

        // 1. 직전 턴에 상대가 폰을 2칸 전진시켰는지 확인
        if (!(lastMovedPiece instanceof Pawn) ||
                lastMovedPiece.getColor() == pawn.getColor() ||
                Math.abs(lastMove.to().getY() - lastMove.from().getY()) != 2) {
            return null;
        }

        // 2. 내 폰 바로 옆에 있는지 확인
        int y = pawn.getPosition().getY();
        int x = pawn.getPosition().getX();
        if (lastMove.to().getY() != y || Math.abs(lastMove.to().getX() - x) != 1) {
            return null;
        }

        // 3. 앙파상 이동 목적지 계산
        int targetY = (pawn.getColor() == Color.WHITE) ? y - 1 : y + 1;
        Position targetPos = new Position(targetY, lastMove.to().getX());

        Move enPassantMove = new Move(pawn.getPosition(), targetPos);

        // 4. 안전 검사 (시뮬레이션)
        Board virtualBoard = board.copy();
        virtualBoard.executeEnPassant(enPassantMove);

        if (!isKingInCheck(pawn.getColor(), virtualBoard)) {
            return enPassantMove; // Optional.of(...) 대신 객체 직접 반환
        }

        return null;
    }

    public void handlePromotion(Piece piece, Position to){
        if(!(piece instanceof Pawn)){
            return;
        }

        int targetY = (piece.getColor() == Color.WHITE) ? 0 : 7;
        if(to.getY() != targetY){
            return;
        }

        //프로모션 입력
        System.out.println("폰 프로모션! 어떤 기물로 승급하시겠습니까? (q: 퀸, r: 룩, b: 비숍, n: 나이트)");
        char choice = ' ';
        while (choice != 'q' && choice != 'r' && choice != 'b' && choice != 'n') {
            try {
                String input = scanner.next(); // 단어 하나 입력받기
                if (input.length() > 0) {
                    choice = input.charAt(0); // 첫 글자만 따오기
                }
            } catch (Exception e) {
                scanner.nextLine(); // 에러 나면 버퍼 비우기
            }
        }

        //체스 기물 교체
        Piece newPiece;
        Color color = piece.getColor();
        switch (choice){
            case 'q': newPiece = new Queen(color, to); break;
            case 'r': newPiece = new Rook(color, to); break;
            case 'b': newPiece = new Bishop(color, to); break;
            case 'n': newPiece = new Knight(color, to); break;
            default: newPiece = new Queen(color, to); break;
        }
        board.setPieceAt(to, newPiece);
    }

    private void executeMove(Move move, Piece piece){
        //캐슬링인지 확인
        if(piece instanceof King && Math.abs(move.to().getX() - move.from().getX()) == 2){
            board.executeCastling(move);
        }
        //앙파상인지 확인
        else if(piece instanceof Pawn && move.from().getX() != move.to().getX() && board.isEmpty(move.to())){
            board.executeEnPassant(move);
        }
        //그 외 모든 표준 이동
        else{
            board.executeMove(move);
        }
    }

    public void switchPlayer(){
        currentPlayer = (currentPlayer == playerWhite) ? playerBlack : playerWhite;
    }
    public Color getOpponentColor(){
        return (currentPlayer.getColor() == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Position findKing(Color color, Board boardContext){
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                Position pos = new Position(y,x);
                Piece piece = boardContext.getPieceAt(pos);
                if(piece instanceof King && piece.getColor() == color){
                    return pos;
                }
            }
        }
        return null;
    }


    //Board 클래스에 있어야 하지만 Game 로직을 위해 임시로 여기에 구현
    private List<Piece> getPieces(Color color, Board boardContext){
        List<Piece> pieces = new ArrayList<>();
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                Piece piece = boardContext.getPieceAt(new Position(y,x));
                if(piece != null && piece.getColor() == color){
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    private boolean isSquareSafe(Position pos, Color myColor){
        Color opponentColor = (myColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        List<Piece> opponentPieces = getPieces(opponentColor, board);

        for(int i = 0; i < opponentPieces.size(); i++){
            Piece opponent = opponentPieces.get(i);

            if(opponent instanceof Pawn){
                int dir = (opponent.getColor() == Color.WHITE) ? -1 : 1;
                // 폰이 공격할 수 있는 위치(대각선) 인지 계산
                if(pos.getY() == opponent.getPosition().getY() + dir && Math.abs(pos.getX() - opponent.getPosition().getX()) == 1){
                    return false; // 공격받는 위치
                }
            }
            else{
                List <Move> moves = opponent.getValidMoves(board);
                for(int j = 0; j < moves.size(); j++){
                    Move attack = moves.get(j);

                    if(attack.to().equals(pos)){
                        return false;
                    }
                }
            }
        }

        //모든 적을 다 검사했는데 아무도 공격하지 않음(안전함)
        return true;
    }

    private void clearConsole(){
        for(int y = 0; y < 50; y++){
            System.out.println();
        }
    }
}