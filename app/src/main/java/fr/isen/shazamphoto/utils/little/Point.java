package fr.isen.shazamphoto.utils.little;

public class Point {
    protected int to;
    protected int from;
    protected Point next=null;
    protected Point prev=null;
    public Point(int i, int j){
        from=i;
        to=j;
    }

    public Point getNext() {
        return next;
    }

    public void setNext(Point next) {
        this.next = next;
    }

    public Point getPrev() {
        return prev;
    }

    public void setPrev(Point prev) {
        this.prev = prev;
    }
}
