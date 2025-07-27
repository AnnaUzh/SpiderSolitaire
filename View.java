package com.example.spidy;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.beans.value.*;
import javafx.scene.control.ComboBox;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.ImagePattern;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.event.EventHandler;
import java.util.ArrayList;
import java.util.List;



public class View {
    private final int SCENEHEIGHT = 800;
    private static final int SCENEWIDTH = 1300;
    private static int CARDWIDTH = 100;
    private static int CARDHEIGHT = 140;
    private static Scene scene;
    private static HBox hboxDeck = new HBox(-99);
    private static HBox PilesTen = new HBox(30);
    private HBox DeckClick = new HBox(-200);
    private static BorderPane allCards;
    private static List<CardView> allCardViews = new ArrayList<>();
    public static Text scoretotal = new Text();
    // public static BorderPane res;

    public View(Stage stage){

        scene = CardsToScene();
        stage.setScene(scene);
        stage.setTitle("The Spider Solitaire game");
        stage.show();
    }

    public BorderPane showCards(){
        allCardViews.clear(); // Очищаем старые карты

        // Создаём карты колоды
        for (int i = 0; i < Game.Deck.size(); i++) {
            CardView card = new CardView(-1, i, "0", 0);
            allCardViews.add(card);
            hboxDeck.getChildren().add(card);
        }
        Rectangle Rdeck = new Rectangle(250, 150, Color.TRANSPARENT);
        Discard(Rdeck);
        DeckClick.getChildren().add(hboxDeck);
        DeckClick.getChildren().add(Rdeck);


        // Создаём карты в стопках
        for (int i = 0; i < 10; i++) {
            VBox pile = new VBox(-100);
            for (int j = 0; j < Game.Piles[i].size(); j++) {
                Card current = Game.Piles[i].get(j);
                current.face = 0;
                CardView card = new CardView(i, j, "0", current.cardNumber);
                if (j == Game.Piles[i].size() - 1){
                    card = new CardView(i, j, String.valueOf(current.kind), current.cardNumber);
                    current.face = 1;
                }
                allCardViews.add(card);
                pile.getChildren().add(card);
            }
            pile.getChildren().add(CreateEmptyRectanle(i));
            PilesTen.getChildren().add(pile);
        }
        Rectangle restart = new Rectangle(100, 50,Color.WHITE);
        restartButton(restart);
        StackPane stp= new StackPane();
        Text h = new Text("restart");
        h.setFill(Color.BLACK);
        stp.getChildren().addAll(restart,h);

        scoretotal.setText("score  " + Game.Total);
        scoretotal.setFill(Color.WHITE);
        scoretotal.setStyle("-fx-font: 24 arial;");


        BorderPane button = new BorderPane();
        button.setLeft(DeckClick);
        button.setRight(stp);
        button.setCenter(scoretotal);
        BorderPane res = new BorderPane();
        res.getChildren().add(PilesTen);
        res.setBottom(button);
        return res;
    }
    public Scene CardsToScene(){
        Scene s;
        allCards = showCards();
//        if (Game.CompleteGame()){
//            allCards.setCenter(TheEnd());
//        }
        s = new Scene(allCards, SCENEWIDTH, SCENEHEIGHT, Color.GREEN);
        return s;
    }

    public static void GoalToCard(Rectangle dropZone, int targetPileIndex){
        dropZone.setOnDragOver(event -> {
            if (event.getGestureSource() != dropZone && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // 2. Обработчик "броска карты в зону"
        dropZone.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String[] data = db.getString().split(",");
                int sourcePile = Integer.parseInt(data[0]);
                int sourceCard = Integer.parseInt(data[1]);
                if (Game.canMoveCard(sourcePile, sourceCard, targetPileIndex)){
                    // System.out.println("complete");
                    // Перемещаем карту через игровую логику
                    if (Game.moveCard(sourcePile, sourceCard, targetPileIndex)) {
                        Alert finish = new Alert(Alert.AlertType.INFORMATION);
                        finish.setTitle("Congratulations!");
                        finish.setContentText("You've won!!!");
                        finish.show();
                    }

                    // Обновляем только измененные стопки
                    // Platform.runLater(() -> {
                    updatePileView(sourcePile);
                    updatePileView(targetPileIndex);
                    // });

                    success = true;
                    allCards.setStyle("-fx-background-color: green;");
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }
    public static void updatePileView(int pileIndex) {
        VBox pileBox = (VBox)PilesTen.getChildren().get(pileIndex);
        pileBox.getChildren().clear();
        // System.out.println("meow");

        // Добавляем обновлённые карты для этой стопки
        for (int i = 0; i < Game.Piles[pileIndex].size(); i++) {
            Card card = Game.Piles[pileIndex].get(i);
            CardView cardView = new CardView(pileIndex, i,
                    "0", card.cardNumber);
            if (card.face == 1 || i == Game.Piles[pileIndex].size() - 1){
                card.face = 1;
                cardView = new CardView(pileIndex, i,
                        String.valueOf(card.kind), card.cardNumber);
            }
            pileBox.getChildren().add(cardView);
        }

        // Добавляем зону сброса
        Rectangle dropZone = CreateEmptyRectanle(pileIndex);
        pileBox.getChildren().add(dropZone);
    }
    private void Discard(Rectangle r){
        r.setOnMouseClicked(event -> {
            // System.out.println("Mouse Clicked!");
            Game.DroppingFromDeck();
            for (int i = 0; i < 10; i++){
                updatePileView(i);
            }
            updateDeck();
        });
    }
    private void restartButton(Rectangle r){
        r.setOnMouseClicked(event -> {
            Game.stop();
            allCards.setStyle("-fx-background-color: green;");
        });
    }
    public static void updateDeck(){
        hboxDeck.getChildren().clear();
        for (int i = 0; i < Game.Deck.size(); i++) {
            CardView card = new CardView(-1, i, "0", 0);
            allCardViews.add(card);
            hboxDeck.getChildren().add(card);
        }
    }
    private static Rectangle CreateEmptyRectanle(int i){
        Rectangle r1 = new Rectangle();
        r1.setWidth(CARDWIDTH);
        r1.setHeight(CARDHEIGHT);
        r1.setFill(Color.TRANSPARENT);
        GoalToCard(r1, i);
        return r1;
    }
    public static void updateTotal(){
        scoretotal.setText("score  " + Game.Total);
    }
}