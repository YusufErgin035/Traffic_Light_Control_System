package com.example.trafficlightcontrolsystem;

import java.util.*;

public class graph {
    private int numVertices;
    private LinkedList<Edge>[] adjList;

    public graph(int numVertices) {
        this.numVertices = numVertices;
        adjList = new LinkedList[numVertices];
        for (int i = 0; i < numVertices; i++) {
            adjList[i] = new LinkedList<>();
        }
    }

    public void addEdge(int v1, int v2,int vehicleMax,int roadTime, int fromX, int fromY,int toX,int toY) {
        adjList[v1].add(new Edge(v2,vehicleMax,roadTime,fromX,fromY,toX,toY));
        adjList[v2].add(new Edge(v1,vehicleMax,roadTime,toX,toY,fromX,fromY));
    }

    public void incrementVehicle(int from, int to) {
        for (Edge edge : adjList[from]) {
            if (edge.to == to) {
                edge.incrementVehicle();
                return;
            }
        }
        System.out.println("Kenar bulunamadı: " + from + " -> " + to);
    }

    public void decrementVehicle(int from, int to) {
        for (Edge edge : adjList[from]) {
            if (edge.to == to) {
                edge.decrementVehicle();
                return;
            }
        }
        System.out.println("Kenar bulunamadı: " + from + " -> " + to);
    }
    public int getMaxVehicle(int from,int to) {
        for (Edge edge : adjList[from]) {
            if (edge.to == to) {
                return edge.vehicleMax;
            }
        }
        return -1;
    }
    public int getRoadTime(int from,int to) {
        for (Edge edge : adjList[from]) {
            if (edge.to == to) {
                return edge.roadTime;
            }
        }
        return -1;
    }

    public void printGraph() {
        System.out.println("Komşuluk Listesi:");
        for (int i = 0; i < numVertices; i++) {
            System.out.print((i + 1) + ": ");
            for (Edge edge : adjList[i]) {
                System.out.print(edge + " ");
            }
            System.out.println();
        }
    }

    public List<Integer> dijkstra(int start, int end) {
        int[] distance = new int[numVertices];
        int[] previous = new int[numVertices];
        boolean[] visited = new boolean[numVertices];

        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(previous, -1);
        distance[start] = 0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(node -> distance[node]));
        pq.add(start);

        while (!pq.isEmpty()) {
            int current = pq.poll();

            if (visited[current]) continue;
            visited[current] = true;

            for (Edge edge : adjList[current]) {
                int neighbor = edge.to;
                int weight = edge.vehicleCount == 0 ? 1 : edge.vehicleCount; // araç yoksa bile geçilebilir

                if (!visited[neighbor]) {
                    int newDist = distance[current] + weight;
                    if (newDist < distance[neighbor]) {
                        distance[neighbor] = newDist;
                        previous[neighbor] = current;
                        pq.add(neighbor);
                    }
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = previous[at]) {
            path.add(at);
        }
        Collections.reverse(path);

        if (path.size() == 1 && path.get(0) != start) {
            return new ArrayList<>(); // yol bulunamamışsa
        }

        return path;
    }
}
