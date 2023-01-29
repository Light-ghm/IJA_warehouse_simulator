package ija_project;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
 * Class main
 * @author Marián Backa (xbacka01)
 * @author Vojtech Soukenka (xsouke01)
 */
public class Main extends Application {

    private Timer timer = null;
    private Timeline timeline = null;
    private HashMap<Integer, Label> shelfLabels;
    private Controller cont;
    private int[] speed;

    @Override
    public void start(Stage primaryStage) throws Exception{
        shelfLabels = new HashMap<>();
        /* HIDE GUI WINDOW FOR NOW */
        Parent root = FXMLLoader.load(getClass().getResource("layouts/layout.fxml"));
        root.getStylesheets().add(getClass().getResource("layouts/style.css").toExternalForm());
        primaryStage.setTitle("Warehouse view");
        Scene scene = new Scene(root, 900, 900);
        Controller controller = new Controller();
        this.cont = controller;
        controller.setRoot(root);
        this.speed = new int[]{1};
        controller.setPlayspeed(speed);



        Data data = new Data();
        File items_shelfs = new File("data/items.xml");
        data.loadItems(items_shelfs);
        File ordersFile = new File("data/orders.xml");
        data.loadOrders(ordersFile);

//        System.out.println("LOAD AND SHOW ORDERS:\n##############################\n");
//        System.out.println(data.toString());
//        System.out.println("\nREQUEST FIRST ORDER AND SHOW ORDERS:\n##############################\n");
//        Order requested = data.requestOrder();
//        System.out.println(data.toString());
//        System.out.println("\nREQUEST SECOND ORDER, FINISH FIRST ORDER AND SHOW ORDERS:\n##############################\n");
//        Order requested2 = data.requestOrder();
//        data.finishOrder(requested);
//        System.out.println(data.toString());

//        System.out.println(data.writeItems());  //write loaded items
        data.fillShelfs();
//        System.out.println(data.writeShelfs());  //write loaded shelfs
//        System.out.println(data.writeItems());  //write loaded items after loading them to shelfs
//        System.out.println("Total number of items in shelfs: " + data.totalNumberOfItemsInShelfs());
//
//        // MAP
        File mapFile = new File("data/map.xml");
//        System.out.println("\nLOAD MAP AND SHOW ALL COORDINATES AND AISLES:\n##############################\n");
        data.loadMap(mapFile);
//        System.out.println(data.map.toString());
        // exit program; for testing
        // TODO remove when gui is added


        //cez label sa to asi najlahsie bude vykreslovat, ma pekne pristupne funkcie na urcenie polohy, pozadia, vysky, sirky aj na kliknute ci hover
        //okay takze ako treba tomu este dake finishing touche
        data.loadBigShelfs(mapFile);
        data.fillBigShelfs();
        controller.setData(data);

        Label label;
        for(BigShelf bigShelf : data.bigShelfList) {
            label = new Label();
            label.setLayoutX(bigShelf.getCoordinate().getX());
            label.setLayoutY(bigShelf.getCoordinate().getY());
            // System.out.println("\n\n entry.getValue().getCoordinate1().getX():" + entry.getValue().getCoordinate1().getX() +"  \nentry.getValue().getCoordinate1().getY()" + entry.getValue().getCoordinate1().getY());
            label.setBackground(new Background(new BackgroundFill(Color.rgb(0, 128, 255, 1), new CornerRadii(0), new Insets(0))));
            label.setPrefHeight(300);
            label.setPrefWidth(100);
            label.setStyle("-fx-border-width: 5; -fx-border-color: black; -fx-border-style: solid;");
            label.setText(bigShelf.getId());
            ((Pane) root).getChildren().add(label);
            int y = 0;
            for(Shelf shelf : bigShelf.getShelfsinBigshelfL()){
                label = new Label();
                label.setLayoutX(bigShelf.getCoordinate().getX());
                label.setLayoutY(bigShelf.getCoordinate().getY() + y *30);
                //here add coordinate to shelf
                shelf.setCoordinate(new Coordinate(bigShelf.getCoordinate().getX(),bigShelf.getCoordinate().getY() + y*30,Integer.parseInt(shelf.getId())));
                y++;
                label.setBackground(new Background(new BackgroundFill(Color.rgb(255, 153, 51, 1), new CornerRadii(0), new Insets(0))));
                label.setPrefHeight(30);
                label.setPrefWidth(40);
                label.setStyle("-fx-border-width: 5; -fx-border-color: black; -fx-border-style: solid;");
                String toolTip = shelf.toString();
                label.setTooltip(new Tooltip(toolTip));
                label.setText(shelf.getId());
                shelf.setLabel(label);
                shelfLabels.put(Integer.parseInt(shelf.getId()), label);
                ((Pane) root).getChildren().add(label);
            }
            y = 0;
            for(Shelf shelf : bigShelf.getShelfsinBigshelfR()){
                label = new Label();
                label.setLayoutX(bigShelf.getCoordinate().getX()+60);
                label.setLayoutY(bigShelf.getCoordinate().getY() + (y*30));
                //here add coordinate to shelf
                shelf.setCoordinate(new Coordinate(bigShelf.getCoordinate().getX()+60,bigShelf.getCoordinate().getY() + y*30,Integer.parseInt(shelf.getId())));
                y++;
                label.setBackground(new Background(new BackgroundFill(Color.rgb(255, 153, 51, 1), new CornerRadii(0), new Insets(0))));
                label.setPrefHeight(30);
                label.setPrefWidth(40);

                Tooltip tooltip = new Tooltip(shelf.toString());
                tooltip.setGraphicTextGap(0.5);
                label.setTooltip(tooltip);

                label.setStyle("-fx-border-width: 5; -fx-border-color: black; -fx-border-style: solid;");
                label.setText(shelf.getId());
                shelf.setLabel(label);
                shelfLabels.put(Integer.parseInt(shelf.getId()), label);
                ((Pane) root).getChildren().add(shelf.getLabel());
            }

        }
        //aisles(roads)
        Line line;
        for( Map.Entry<Integer, Aisle> aisle : data.map.getAisleHashMap().entrySet()) {
            //   System.out.println("x1: " + aisle.getValue().getCoordinate1().getX() + "\n y1: " + aisle.getValue().getCoordinate1().getY() + "\nx2: " + aisle.getValue().getCoordinate2().getX() + "\n y2: " + aisle.getValue().getCoordinate2().getY());
            line = new Line(aisle.getValue().getCoordinate1().getX(),aisle.getValue().getCoordinate1().getY(),aisle.getValue().getCoordinate2().getX(),aisle.getValue().getCoordinate2().getY());
            line.setStrokeWidth(40);
            line.setStroke(Color.rgb(98, 138, 182));
            line.setOpacity(1);
            aisle.getValue().setLine(line);
            ((Pane) root).getChildren().add(aisle.getValue().getLine());
        }

        //parking
        Label labelp = new Label();
        labelp.setLayoutX(data.map.getParking().getX());
        labelp.setLayoutY(data.map.getParking().getY());
        labelp.setBackground(new Background(new BackgroundFill(Color.rgb(128, 128, 128, 1), new CornerRadii(0), new Insets(0))));
        labelp.setPrefHeight(50);
        labelp.setPrefWidth(290);
        labelp.setText("");
        labelp.setStyle("-fx-border-width: 5; -fx-border-color: yellow; -fx-border-style: solid;");
        ((Pane) root).getChildren().add(labelp);

        //carts
        data.CreateCarts(7);
        for(Cart cart : data.getParkingZone().getCartList())
        {

            Label labelc = new Label();
            labelc.setLayoutX(cart.getCoordinate().getX());
            labelc.setLayoutY(cart.getCoordinate().getY());
            labelc.setBackground(new Background(new BackgroundFill(Color.rgb(120, 120, 180, 1), new CornerRadii(0), new Insets(0))));
            labelc.setPrefHeight(40);
            labelc.setPrefWidth(40);
            labelc.setText(cart.getId());
            String toolTip = cart.toString();
            labelc.setTooltip(new Tooltip(toolTip));
            labelc.setStyle("-fx-border-width: 5; -fx-border-color: black; -fx-border-style: solid;");
            cart.setLabel(labelc);
            ((Pane) root).getChildren().add(cart.getLabel());
        }

        //departure
        Label labeld = new Label();
        labeld.setLayoutX(data.map.getDeparture().getX());
        labeld.setLayoutY(data.map.getDeparture().getY());
        labeld.setBackground(new Background(new BackgroundFill(Color.rgb(224, 224, 224, 1), new CornerRadii(0), new Insets(0))));
        labeld.setPrefHeight(100);
        labeld.setPrefWidth(100);
        labeld.setText("Departure");
        labeld.setStyle("-fx-border-width: 5; -fx-border-color: black; -fx-border-style: solid;");
        ((Pane) root).getChildren().add(labeld);




        primaryStage.setScene(scene);
        primaryStage.show();

        // Testing cart, not safe yet
      //  showCoordinates(data, root);
        programLoop(data, root);

    }
    private void showCoordinates(Data data, Parent root) {
        List<Circle> circles = new ArrayList<>();
        for(Map.Entry<Integer, Coordinate> entry : data.map.getCoordinateHashmap().entrySet()) {
            Coordinate coord = entry.getValue();
            Circle newC = new Circle();
            newC.setCenterX(coord.getX());
            newC.setCenterY(coord.getY());
            newC.setRadius(3);
            newC.setFill(Color.BLACK);
            ((Pane) root).getChildren().add(newC);
            circles.add(newC);
        }
    }

    private void testCart(Data data, Parent root) {
//        data.shelfList.get(0).addItem(data.itemList.get(0));
//        data.shelfList.get(1).addItem(data.itemList.get(1));
//        data.shelfList.get(2).addItem(data.itemList.get(2));
//        data.shelfList.get(3).addItem(data.itemList.get(3));
//        System.out.println(data.shelfList.size());

        //Cart cart = new Cart("69", null, data);
        int i = 0;
        Cart cart = data.getParkingZone().getCartList().get(i);
        i++;
        cart.shelfLabels = shelfLabels;
        cart.setStart(data.map.getParking());
        Order order = data.requestOrder();
//        System.out.println(order);
        cart.assignOrder(order);
        cart.getLabel().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                cart.showPath(root);
            }
        });
        cart.getLabel().setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                cart.hidePath(root);
            }
        });
        Point point = cart.getPosition(1);
        /*Circle car = new Circle();
        car.setCenterX(point.getX());
        car.setCenterY(point.getY());
        car.setRadius(10);
        car.setFill(Color.BLUE);
        ((Pane) root).getChildren().add(car);*/
//        data.map.lockAisle(11);
        this.timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Point point = cart.getPosition(speed[0]);
                cart.getLabel().setLayoutX(point.getX()-20);
                cart.getLabel().setLayoutY((point.getY()-20));


                if(cart.currentWait > 0) {
                    cart.getLabel().setStyle("-fx-border-color: red;");
                } else {
                    cart.getLabel().setStyle("-fx-border-color: blue;");
                }
                if (cart.stoppedAt != null) {
                    cart.assignOrder(data.requestOrder());
                }
            }
        }, 2000, 1000);
//        while(cart.stoppedAt == null) {
//            System.out.println(cart.getPosition(100));
//
////            cart.getPosition(2);
//        }
//        System.out.println(cart.getPosition(1));
//        System.out.println(cart.getPosition(1));
    }

    private void programLoop(Data data, Parent root) {
        List<Cart> carts = data.getParkingZone().getCartList();
        // prepare carts
        for (Cart cart : carts) {
            cart.shelfLabels = shelfLabels;
            cart.setStart(data.map.getParking());
            try{
                cart.getLabel().setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        cart.showPath(root);
                    }
                });
                cart.getLabel().setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        cart.hidePath(root);
                    }
                });
            }catch(Exception e){

            }

        }

        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                e -> {

                    for (Cart  cart : carts) {
                        if (cart.stoppedAt != null) {
                            Order order = data.requestOrder();
                            if (order == null){
                                if(cart.stoppedAt.getId() == data.map.getDeparture().getId()) {
                                    cart.goHome = true;
                                } else {
                                    continue;
                                }
                            }
                            cart.assignOrder(order);
                        }
                        Point point = cart.getPosition(speed[0]);
                        cart.getLabel().setLayoutX(point.getX()-20);
                        cart.getLabel().setLayoutY((point.getY()-20));


                        if(cart.currentWait > 0) {
                            cart.getLabel().setStyle("-fx-border-color: red;");
                        } else {
                            cart.getLabel().setStyle("-fx-border-color: blue;");
                        }
                    }
                }
        ));
        cont.setTimeline(timeline);
//        System.out.println("set timeline");
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * End timer if running on window close.
     */
    @Override
    public void stop(){
        if (this.timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
