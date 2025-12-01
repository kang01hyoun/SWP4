public final class Position {
    private final int y;
    private final int x;

    //생성자 메서드
    public Position(int y, int x){
        this.y = y;
        this.x = x;
    }

    // Y 좌표 Get
    public int getY() {
        return y;
    }

    // X 좌표 Get
    public int getX(){
        return x;
    }

    //기물이 체스판 안에 있는지 검사
    public boolean isWithinBoard(){
        return y >= 0 && y < 8 && x >= 0 && x < 8;
    }

    //값 비교 메서드 같으면 True
    @Override
    public boolean equals(Object o){
        //주소값 검사
        if(this == o){
            return true;
        }
        //타입 검사
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        // 타입캐스팅 후 x y 값 비교
        Position position = (Position) o;
        return y == position.y && x == position.x;
    }

    @Override
    public String toString(){
        return "Position[y=" + y + ", x=" + x + "]";
    }
}
