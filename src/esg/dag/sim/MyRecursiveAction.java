package esg.dag.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class MyRecursiveAction extends RecursiveAction {
    List<Node> nodeList;
    List<Transaction> txList;
    float currTime;
    Report report;
    Simulation s;

    public MyRecursiveAction(Simulation sim, List<Node> nodeList, List<Transaction> txList, float currTime) {
        this.nodeList = nodeList;
        this.txList = txList;
        this.currTime = currTime;
        report = new Report(sim);
        s = sim;
    }

    @Override
    protected void compute() {

        //if work is above threshold, break tasks up into smaller tasks
        if(this.nodeList.size() > 5 || this.txList.size() > 5) {
//            System.out.println("Splitting workLoad : " + this.nodeList.size() + ", " + this.txList.size());
            List<MyRecursiveAction> subtasks = new ArrayList<>();
            subtasks.addAll(createSubtasks());

            for(RecursiveAction subtask : subtasks){
                subtask.fork();
            }

        } else {
//            System.out.println("Doing workLoad myself: " + this.nodeList.size() + ", " + this.txList.size());
            for(Node n : nodeList){
                for(Transaction tx : txList){
                    Report.counter++;
                    report.getConfRate(n, tx, currTime);
                }
             }
        }
    }

    private List<MyRecursiveAction> createSubtasks() {
        List<MyRecursiveAction> subtasks =
                new ArrayList<>();

        MyRecursiveAction subtask1 = new MyRecursiveAction(s, nodeList.subList(0, nodeList.size()/2), txList.subList(0, txList.size()/2), currTime);
        MyRecursiveAction subtask2 = new MyRecursiveAction(s, nodeList.subList(0, nodeList.size()/2), txList.subList(txList.size()/2, txList.size()), currTime);
        MyRecursiveAction subtask3 = new MyRecursiveAction(s, nodeList.subList(nodeList.size()/2, nodeList.size()), txList.subList(0, txList.size()/2), currTime);
        MyRecursiveAction subtask4 = new MyRecursiveAction(s, nodeList.subList(nodeList.size()/2, nodeList.size()), txList.subList(txList.size()/2, txList.size()), currTime);

        subtasks.add(subtask1);
        subtasks.add(subtask2);
        subtasks.add(subtask3);
        subtasks.add(subtask4);

        return subtasks;
    }

//    public static void main(String[] args){
//        ForkJoinPool forkJoinPool = new ForkJoinPool(4); //4 CPUs or threads
//        MyRecursiveAction myRecursiveAction = new MyRecursiveAction(24);
//        forkJoinPool.invoke(myRecursiveAction);
//    }
}