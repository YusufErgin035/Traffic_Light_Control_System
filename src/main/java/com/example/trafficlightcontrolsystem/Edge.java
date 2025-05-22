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

    public void incrementVehicle() {
        vehicleCount++;
    }

    public void decrementVehicle() {
        vehicleCount--;
    }

    @Override
    public String toString() {
        return (to + 1) + "(araç:" + vehicleCount + ")";
    }

    public int getCost() {
        return vehicleCount;
    }
}
