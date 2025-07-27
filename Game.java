package com.example.spidy;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import java.util.*;

import java.util.Vector;

public class Game {
    public static Vector<Card> Deck = new Vector<Card>();
    public static Vector<Card>[] Piles = new Vector[10];
    public static int Total;
    public Game(){
        for (int i = 0; i < 4; i ++){
            for (int n = 1; n < 14; n ++){
                for (int k = 1; k < 3;k++){
                Card c = new Card(n,k);
                c.face = 0;
                Deck.add(c);
                }
            }
        }
        Shuffle();
        Total = 500;
    }
    private static void Shuffle(){
        Collections.shuffle(Deck);
        for (int i = 0;i < 10; i++){
            Piles[i] = new Vector<Card>();
        }
        for (int k = 0, i = 0; k < 54; k++, i = (i + 1) % 10) {
            Piles[i].add(Deck.getFirst());
            Deck.removeFirst();
        }
    }
    public static boolean canMoveCard(int fromPile, int cardIndex, int toPile) {
        if (Piles[fromPile].get(cardIndex).face == 0){
            return false;
        }
        if (Piles[toPile].isEmpty()) {
            for (int i = cardIndex + 1; i < Piles[fromPile].size(); i++) {
                if ((Piles[fromPile].get(i - 1).cardNumber - Piles[fromPile].get(i).cardNumber != 1 || Piles[fromPile].get(i - 1).kind != Piles[fromPile].get(i).kind))
                    return false;
            }
            return true;
        }
        int number1 = Piles[fromPile].get(cardIndex).cardNumber;
        int number2 = Piles[toPile].getLast().cardNumber;
        if (cardIndex == Piles[fromPile].size() - 1 ){
            if (number2 - number1 == 1) return true;
            else return false;
        }
        for (int i = cardIndex + 1; i < Piles[fromPile].size(); i++) {
            if ((number2 - number1 != 1) || (Piles[fromPile].get(i - 1).cardNumber - Piles[fromPile].get(i).cardNumber != 1 || Piles[fromPile].get(i - 1).kind != Piles[fromPile].get(i).kind))
                return false;
        }
        return true;
    }

    public static boolean moveCard(int fromPile, int cardIndex, int toPile) {
        Card cardToMove = Piles[fromPile].get(cardIndex);
        Piles[fromPile].remove(cardIndex);
        Piles[toPile].add(cardToMove);

        // Перемещаем карту и все карты выше нее
        Vector<Card> cardsToMove = new Vector<>(
                Piles[fromPile].subList(cardIndex, Piles[fromPile].size()));

        Piles[fromPile].removeAll(cardsToMove);
        Piles[toPile].addAll(cardsToMove);

        boolean result = false;
        if (Piles[toPile].size() >= 13){
            result = RemoveComplete(toPile);
        }

        // Открываем верхнюю карту в исходной стопке
        if (!Piles[fromPile].isEmpty()) {
            Piles[fromPile].lastElement().face = 1;
        }
        Total -= 1;
        View.updateTotal();
        return result;
    }
    private static boolean RemoveComplete(int pileIndex){
        boolean complete = true;
        for (int i = Piles[pileIndex].size() - 1; i > Piles[pileIndex].size() - 13;i--){
            if (Piles[pileIndex].get(i-1).cardNumber - Piles[pileIndex].get(i).cardNumber != 1 || Piles[pileIndex].get(i-1).kind != Piles[pileIndex].get(i).kind){
                complete = false;
                break;
            }
        }
        if (complete){
            Total += 100;
            for (int i = 0; i < 13; i ++){
                Piles[pileIndex].removeLast();
            }
        }
        return CompleteGame();
    }
    public static boolean CompleteGame(){
        for (int i = 0; i < 10; i ++){
            if (!Piles[i].isEmpty()){
                return false;
            }
        }
        if (Deck.isEmpty()){
            return true;
        }
        return false;
    }
    public static void DroppingFromDeck(){
        for (int i = 0; i < 10; i++){
            Piles[i].add(Deck.getFirst());
            Deck.removeFirst();
        }
    }
    public static void stop(){
        Deck.clear();
        for (int i = 0;i < 10; i++){
            Piles[i].clear();
        }
        for (int i = 0; i < 4; i ++){
            for (int n = 1; n < 14; n ++){
                for (int k = 1; k < 3;k++){
                    Card c = new Card(n,k);
                    c.face = 0;
                    Deck.add(c);
                }
            }
        }
        Shuffle();
        for (int i = 0;i < 10; i++){
            View.updatePileView(i);
        }
        View.updateDeck();
        Total = 500;
        View.updateTotal();

    }
}
