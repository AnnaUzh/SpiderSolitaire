package com.example.spidy;

import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;

public class CardView extends Rectangle {

    public CardView(int pileIndex, int cardIndex, String type, int number) {
        super(100, 140);
        updateCard(type, number);
        setupDragHandlers(pileIndex, cardIndex);
    }

    public void updateCard(String type, int number) {
        String url = type.equals("0")
                ? "file:src/main/java/com/example/spidy/images/back.png"
                : "file:src/main/java/com/example/spidy/images/" + type + '/' + number + ".png";
        this.setFill(new ImagePattern(new Image(url)));
    }
    private void setupDragHandlers(int pileIndex, int cardIndex) {
        this.setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putString(pileIndex + "," + cardIndex);
            // System.out.println(Integer.toString(pileIndex) + "," + Integer.toString(cardIndex));
            db.setContent(content);

            event.consume();
        });

        this.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                // Карта перемещена - можно обновить представление
            }
            event.consume();
        });
    }
}