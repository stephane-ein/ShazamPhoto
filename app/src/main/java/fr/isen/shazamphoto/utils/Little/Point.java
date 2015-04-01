package fr.isen.shazamphoto.utils.Little;

public class Point {
    protected long to;
    protected long from;
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

    public long getTo() {
        return to;
    }

    public long getFrom() {
        return from;
    }

    public String toString(){
        return "From : "+Long.valueOf(from) +" to :"+Long.valueOf(to).toString();
    }
}
