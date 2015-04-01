package fr.isen.shazamphoto.utils.Little;

import java.util.ArrayList;
import java.util.List;

public class Little {
    protected int nbCity;
    protected int[][] mtx;
    protected List<Point> shortPath;

    public Little(int nbCity, int[][] mtxInit ){
        this.nbCity=nbCity;
        this.mtx=new int[nbCity+2][nbCity+2];
        this.mtx=mtxInit;
        shortPath=new ArrayList<>();
    }

    public List<Point> getShortPath() {
        return shortPath;
    }

    public void doLittle(){
        boolean check;
        if(nbCity>2){
            do{
                this.minLine();
                this.minCol();
                this.EvalDefault();
                check=this.CalRegret();
            }while(check!=true);
            for(int i =1;i<=nbCity;i++){
                for(int j=1;j<=nbCity;j++){
                    if(mtx[i][j]!=-1){
                        Point p=new Point(mtx[i][0],mtx[0][j]);
                        shortPath.add(p);
                        pathSort();
                    }
                }
            }
        }
    }

    public void minLine(){
        int min = Integer.MAX_VALUE;
        for(int i =1; i <=nbCity; i++){
            for(int j=1; j<=nbCity; j++){
                if(mtx[i][j]!=-1){
                    if(mtx[i][j]<min){
                        min = mtx[i][j];
                    }
                }
            }
            mtx[i][nbCity+1]= min;
            for(int j=1; j<=nbCity;j++){
                if(mtx[i][j]!=-1){
                    mtx[i][j]-=min;
                }
            }
            min = Integer.MAX_VALUE;
        }
    }

    public void minCol(){
        int min = Integer.MAX_VALUE;
        for(int j =1; j <=nbCity; j++){
            for(int i=1; i<=nbCity; i++){
                if(mtx[i][j]!=-1){
                    if(mtx[i][j]<min){
                        min = mtx[i][j];
                    }
                }
            }
            mtx[nbCity+1][j]= min;
            for(int i=1; i<=nbCity;i++){
                if(mtx[i][j]!=-1){
                    mtx[i][j]-=min;
                }
            }
            min = Integer.MAX_VALUE;
        }
    }

    public void EvalDefault(){
        int def=0;
        for(int i=1; i<=nbCity; i++){
            def += mtx[i][nbCity+1]+mtx[nbCity+1][i];
        }
        mtx[nbCity+1][nbCity+1]+= def;
    }

    public boolean CalRegret(){
        int minI = Integer.MAX_VALUE;
        int minJ = Integer.MAX_VALUE;
        int[][] regret=new int[nbCity+2][nbCity+2];
        for(int i =0; i<=nbCity+1; i++) {
            for (int j = 0; j <= nbCity+1; j++) {
                if(i==0||j==0){
                    regret[i][j]=mtx[i][j];
                }else{
                    regret[i][j]=0;
                }
            }
        }
        for(int i =1; i<=nbCity; i++){
            for(int j=1; j<=nbCity; j++){
                if(mtx[i][j]==0){
                    for(int k=1; k<=nbCity; k++){
                        if(mtx[i][k]!=-1){
                            if(k !=j){
                                if(minI>mtx[i][k]){
                                    minI=mtx[i][k];
                                }
                            }
                        }
                        if(mtx[k][j]!=-1){
                            if(k!=i){
                                if(minJ>mtx[k][j]){
                                    minJ=mtx[k][j];
                                }
                            }
                        }

                    }
                    regret[i][j]= minI+minJ;
                    minI = Integer.MAX_VALUE;
                    minJ = Integer.MAX_VALUE;
                }
            }
        }
        int max = Integer.MIN_VALUE;
        int iR=0;
        int jR=0;
        for(int i =1; i<=nbCity; i++) {
            for (int j = 1; j <= nbCity; j++) {
                if(max<regret[i][j]){
                    max = regret[i][j];
                    iR=mtx[i][0];
                    jR=mtx[0][j];
                }
            }
        }
        if(max>0){
            Point p=new Point(iR,jR);
            shortPath.add(p);
            deleteRout(iR,jR);
            return false;
        }else{
            return true;
        }

    }

    public void pathSort(){
        int size = shortPath.size();
        Point p1;
        Point p2;

        for(int x =0;x<size;x++){
            p1=shortPath.get(x);
            if(p1.getPrev()==null||p1.getNext()==null){
                for(int y=0;y<size;y++){
                    if(x!=y){
                        p2=shortPath.get(y);
                        if(p2.getNext()==null){
                            if (p1.from==p2.to){
                                p1.setPrev(p2);
                                p2.setNext(p1);
                            }
                        }else if(p2.getPrev()==null){
                            if(p1.to==p2.from){
                                p1.setNext(p2);
                                p2.setPrev(p1);
                            }
                        }
                    }
                }
            }
        }
    }

    public void deleteCurly(){
        int size = shortPath.size();
        Point p1;
        long i;
        long j;
        this.pathSort();
        for(int x =0;x<size;x++){
            p1=shortPath.get(x);
            if(p1.getNext()==null && p1.getPrev()!=null){
                i=p1.to;
                do{
                    p1=p1.getPrev();
                }while (p1.getPrev()!=null);
                j=p1.from;
                for(int p =1; p<=nbCity;p++){
                    if(mtx[p][0]==i){
                        for(int q=1;q<=nbCity;q++){
                            if(mtx[0][q]==j){
                                mtx[p][q]=-1;
                            }
                        }
                    }
                }
            }
        }

    }

    public void deleteRout(int i, int j){

        //suppr sous trajet
        int X=0;
        int Y=0;
        for(int x =1;x<=nbCity;x++){
            if(mtx[x][0]==i){
                for( int y =1; y<=nbCity;y++){
                    if(mtx[0][y]==j){
                        X=x;
                        Y=y;
                    }
                }
            }
        }
        for(int x =1;x<=nbCity;x++){
            if(mtx[0][x]==i){
                for( int y =1; y<=nbCity;y++){
                    if(mtx[y][0]==j){
                        mtx[y][x]=-1;

                    }
                }
            }
        }
        deleteCurly();
        nbCity-=1;
        int[][]newMtx = new int[nbCity+2][nbCity+2];
        for(int k=0;k<=nbCity+1;k++){
            for(int l=0; l<=nbCity+1;l++){
                if(k < X && l<Y ){
                    newMtx[k][l]=mtx[k][l];
                }else if(k>=X && l<Y){
                    newMtx[k][l]=mtx[k+1][l];
                }else if(k<X && l>=Y){
                    newMtx[k][l]=mtx[k][l+1];
                }else if(k>=X && l>=Y){
                    newMtx[k][l]=mtx[k+1][l+1];
                }
            }
        }
        mtx=new int[nbCity+2][nbCity+2];
        mtx=newMtx;
    }
}
