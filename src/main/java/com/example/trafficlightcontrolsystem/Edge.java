package com.example.trafficlightcontrolsystem;

public class Edge {
    int to;
    int vehicleCount;
    int vehicleMax;
    int roadTime;
    int fromX,toX;
    int fromY,toY;

    public Edge(int to, int vehicleMax, int roadTime, int fromX, int fromY,int toX,int toY) {
        this.to = to;
        this.vehicleCount = 0;
        this.vehicleMax = vehicleMax;
        this.roadTime = roadTime;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public String calcLine() {
        int deltaX = Math.abs(toX - fromX);
        int deltaY = Math.abs(toY - fromY);

        return deltaX > deltaY ? "x" : "y";
    }

    public void incrementVehicle() {
        System.out.println("Vehicle arttırma:"+vehicleCount+"->");
        vehicleCount++;
        System.out.println(vehicleCount+" ");
    }

    public void decrementVehicle() {
        System.out.println("Vehicle azaltma:"+vehicleCount+"->");
        vehicleCount--;
        System.out.println(vehicleCount);
    }
}
