package com.example.trafficlightcontrolsystem;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

public class DestinationMaker {
    private graph g;
    private int start;
    private int end;
    private Car car;
    public DestinationMaker(graph g) {
        Random rand = new Random();
        this.g = g;
        this.start = rand.nextInt(0,7);
        do{
            this.end = rand.nextInt(0,7);
        }while(this.end == this.start);
        List<Integer> path = g.dijkstra(this.start,this.end);
        double red = rand.nextDouble();
        double green = rand.nextDouble();
        double blue = rand.nextDouble();
        car = new Car(0,0,Color.color(red,green,blue));

    }
    public void makeDestination(int from,int to) {
        int MaxVehicle = g.getMaxVehicle(from, to);
        int RoadTime = g.getRoadTime(from, to);

    }
}
