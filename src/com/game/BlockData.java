package com.game;

public class BlockData {

    public int row;
    public int col;
    public int x;
    public int y;
    public int data;
    public Direct direct;
    public int distance;

    public BlockData(){

    }

    public BlockData(int row, int col, int data, Direct direct, int distance){
        this.row = row;
        this.col = col;
        this.data = data;
        this.direct = direct;
        this.distance = distance;
    }
}
