import java.util.Objects;

enum Color{
    WHITE, BLACK;
}

//기물의 이동(출발지, 목적지)를 나타내는 데이터 클래스
public final class Move {
    private final Position from;
    private final Position to;

    public Move(Position from, Position to){
        this.from = from;
        this.to = to;
    }

    //출발지 위치 반환
    public Position from(){
        return this.from;
    }
    //목적지 위치 반환
    public Position to(){
        return this.to;
    }

    //출발지와 목적지가 같은지 확인하는 메서드
    @Override
    public boolean equals(Object obj){
        //주소 비교
        if(this == obj){
            return true;
        }
        //타입 확인
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        //형변환
        Move move = (Move) obj;
        return Objects.equals(from, move.from) && Objects.equals(to, move.to);
    }

    @Override
    public String toString(){
        return "Move[from=" + from + ", to=" + to + "]";
    }
}
