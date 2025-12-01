import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;

//백 또는 흑 플레이어를 나타내며 사용자 입력을 처리
public class Player {
    //내 팀 색깔
    private final Color color;
    //스캐너
    private final Scanner scanner;

    public Player(Color color) {
        this.color = color;
        this.scanner = new Scanner(System.in);
    }

    public Color getColor() {
        return this.color;
    }

    // 유효한 아군 기물을 선택할 때 까지 사용자에게 입력을 받음
    public Position getMoveFromUser(Board board) {
        Position fromPos = null;
        boolean isValid = false;

        //유효한 입력을 받을 때 까지 반복
        while (!isValid) {
            String output = ((color == Color.WHITE) ? "백(W)" : "흑(B)")
                    + "팀의 기물 이동\n이동할 기물의 위치 (y x):";

            fromPos = getPositionInput(output);

            //유효하지 않은 좌표를 입력할때 루프 다시
            if (fromPos == null) {
                continue;
            }

            Piece piece = board.getPieceAt(fromPos);

            if (piece == null) {
                System.out.println("해당 위치에 기물이 없습니다. 좌표를 다시 입력해주세요");
            }
            else if( piece.getColor() != this.color) {
                System.out.println("상대 기물을 선택할 수 없으니 좌표를 다시 입력해 주세요.");
            }
            else{
                isValid = true;
            }

        }
        return fromPos;

    }

    //사용자가 어디로 갈지 고르는 과정
    public Move getMoveToUser(Position from, List<Move> legalMoves) {
        Move selectedMove = null;
        boolean isValid = false;

        while (!isValid) {
            Position toPos = getPositionInput("목표 위치 (y x):");

            if (toPos == null) {
                continue;
            }

            Move intendedMove = new Move(from , toPos);

            for(int i = 0; i < legalMoves.size(); i++){
                Move legalMove = legalMoves.get(i);

                if(legalMove.equals(intendedMove)){
                    selectedMove = legalMove;
                    isValid = true;
                    break;
                }
            }

            if(!isValid) {
                System.out.println("유효하지 않은 이동입니다. 다시 시도하세요.");
            }
        }
        return selectedMove;
    }


    private Position getPositionInput(String output) {
        System.out.print(output);
        try{
            // 정수 입력
            int y = scanner.nextInt();
            int x = scanner.nextInt();

            //1차 범위 검증
            Position pos = new Position(y, x);
            if(!pos.isWithinBoard()){
                System.out.println("체스판의 범위를 넘어섰습니다. 다시 입력해주세요. (0~7)");
                return null;
            }
            //성공 시 반환
            return pos;

        } catch (InputMismatchException e) {
            System.out.println("잘못된 입력입니다. '숫자y' (띄어쓰기) '숫자 x' 형태로 입력해주세요. ");
            scanner.nextLine();
            return null;
        }
    }
}
