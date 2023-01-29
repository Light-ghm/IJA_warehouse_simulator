package ija_project;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class representing action Controller
 * @author Marián Backa (xbacka01)
 * @author Vojtech Soukenka (xsouke01)
 */
public class Controller {
    private static Data datka;
    private static int OrderId = 1000;
    private static Timeline timeline;
    private static Parent root;
    private static int[] speed;
    private List<Line> lines;
    private static Aisle closed = null;
    @FXML
    Pane pane;
    @FXML
    TextField idTextField;
    @FXML
    TextField quantityTextField;
    @FXML
    Button buttonl;

    private int orederID = 1000;

    public void setData(Data data) {
        datka = data;
    }

    public void setTimeline(Timeline timelinee) {
        timeline = timelinee;
    }

    public void setRoot(Parent rootee) {
        root = rootee;
    }

    public void setPlayspeed(int[] speedee) {
        speed = speedee;
    }

    public void SendRequest(ActionEvent event){

        if(datka != null){
            try {
                String itemID = idTextField.getText();
                idTextField.clear();
                String itemQuantity = quantityTextField.getText();
                quantityTextField.clear();
                try{
                    int quant = Integer.parseInt(itemQuantity);
                }catch(Exception e){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Order failed!");
                    alert.setContentText("Wrong Input Quantity Format!");
                    alert.showAndWait();
                    return;
                }

                Item item = datka.getItemById(itemID);
                if(item != null){
                    Item orderItem = new Item(item.getName(),item.getId(),itemQuantity);
                    datka.addOrder(orderItem,String.valueOf(OrderId));
                    OrderId++;
                    System.out.println(item.toString()); //printne item co naslo na  kontrolu len
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText("Order failed!");
                    alert.setContentText("Wrong Input!");
                    alert.showAndWait();

                }

            }catch(Exception e){
                System.out.println(e.toString());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Order failed!");
                alert.setContentText("Wrong Input!");
                alert.showAndWait();
            }
        }
    }

    public void closeStreet(ActionEvent event){
        if(timeline == null)
            return;
        if(root == null)
            return;
        timeline.pause();
        unlockStreet(event);
        this.lines = new ArrayList<>();
        HashMap<Integer, Aisle> aisles = datka.map.getAisleHashMap();
        for(HashMap.Entry<Integer, Aisle> entry : aisles.entrySet()) {
            Aisle aisle = entry.getValue();
            Coordinate point1 = aisle.getCoordinate1();
            Coordinate point2 = aisle.getCoordinate2();

            Line line = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
            line.setStroke(Color.GREEN);
            line.setStrokeWidth(10);
            line.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    for(Line l : lines) {
                        Pane group = (Pane) l.getParent();
                        group.getChildren().remove(l);
                    }
                    if(aisle.getCoordinate1().getId() == datka.map.getDeparture().getId() || aisle.getCoordinate2().getId() == datka.map.getDeparture().getId()) {
                        timeline.play();
                        return;
                    }
                    if(aisle.getCoordinate1().getId() == datka.map.getParking().getId() || aisle.getCoordinate2().getId() == datka.map.getParking().getId()) {
                        timeline.play();
                        return;
                    }
                    datka.map.lockAisle(aisle.getId());
                    aisle.getLine().setStroke(Color.RED);
                    closed = aisle;
                    timeline.play();
                }
            });
            lines.add(line);
            ((Pane) root).getChildren().add(line);
        }

    }
    public void unlockStreet(ActionEvent event){
        if(closed != null) {
            timeline.pause();
            datka.map.unlockAisle();
            closed.getLine().setStroke(Color.rgb(98, 138, 182));
            closed = null;
            timeline.play();
        }
    }

    public void increaseSpeed(ActionEvent event) {
        if(speed[0] > 4)
            return;
        speed[0]++;
    }
    public void decreaseSpeed(ActionEvent event) {
        if(speed[0] < 2)
            return;
        speed[0]--;
    }

}
