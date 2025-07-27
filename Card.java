package com.example.spidy;

public class Card {
    public int cardNumber;
    public int  kind; // 1 - spades; 2 - hearts
    public int face; //1 - face; 0 - back
    // boolean WasFace;
    protected Card(int num, int kind){
        this.cardNumber = num;
        this.kind = kind;
    }
}
