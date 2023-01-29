package ija_project;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing parking zone
 * @author Marián Backa (xbacka01)
 * @author Vojtech Soukenka (xsouke01)
 */
public class ParkingZone {
    private List<Cart> cartList;
    private Coordinate coordinate;
    private Data data;

    /**
     * Constructor ParkingZone
     * @param coord Coordinate.
     * @param data for access to data
     */
    public ParkingZone(Coordinate coord, Data data){
        this.cartList = new ArrayList<>();
        this.coordinate = coord;
        this.data = data;
    }
    /**
     * Method for generating carts
     * @param coord Coordinate.
     * @param num number of carts to generate
     */
    public void CreateCarts(int num, Coordinate coord){
        int spacing = 0;
        for(int i =0; i < num; ++i){
            spacing = coord.getX() + 40*i;
            Coordinate coorSpaced = new Coordinate(spacing+5,coord.getY()+5,i); //+5 koli borderu
            Cart cart = new Cart(String.valueOf(i), coorSpaced, this.data);
            cartList.add(cart);
        }
    }

    public List<Cart> getCartList(){
        return this.cartList;
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }
}
