package task15;

public class PrereqGraphDemo {
    public static void main(String[] args) {
        PrereqGraph<String> g = new PrereqGraph<>();
        g.addEdge("Algorithms", "DataStructures");
        g.addEdge("DataStructures", "Programming");
        g.addEdge("ML", "Algorithms");
        
        System.out.println("Порядок изучения: " + g.topologicalOrder());

        PrereqGraph<String> g2 = new PrereqGraph<>();
        g2.addEdge("A", "B");
        g2.addEdge("B", "C");
        g2.addEdge("C", "A");
        
        try {
            g2.topologicalOrder();
        } catch (PrereqGraph.CycleException e) {
            System.out.println("Обнаружен цикл: " + e.cycle);
        }
    }
}
