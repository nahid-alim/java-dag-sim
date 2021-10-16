package esg.dag.sim;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import java.util.*;


public class Tangle {
    private Node node;
    private DirectedAcyclicGraph DAG;
    private ArrayList<Site> tips;
    public static ArrayList<Site> tipsForConfRateCalc;
    private ArrayList<Transaction> orphans;
    private ArrayList<Transaction> removeOrphansList;

    public Tangle(Node n, List<Transaction> txList){
        node = n;
        DAG = addGenesisandFounderstoDAG(txList);
        orphans = new ArrayList<Transaction>();
        removeOrphansList = new ArrayList<Transaction>();
    }

    public double calcConfidenceRate(Site txSite){
        tipsForConfRateCalc = new ArrayList<>();
        double conf = 0, result;
        ArrayList<Site> tips;
        if(txSite != null) {
            for (int i = 0; i < Parameters.CalcConfRounds; i++) {
                tips = selectKTips_ConfCalc(this.getDAG());
                boolean approved = isSiteApprovedBytheTip(txSite, tips.get(0));
                tipsForConfRateCalc.add(tips.get(0));
//                System.out.println("tx " + tips.get(0).getTx().getID() + " approves " + approved + " tx " + txSite.getTx().getID());
                if (approved)
                    conf++;
            }
        }
        result = (double)(conf / Parameters.CalcConfRounds);
        return result;
    }

    private DirectedAcyclicGraph addGenesisandFounderstoDAG(List<Transaction> txList) {
        DirectedAcyclicGraph dag = new DirectedAcyclicGraph<>(DefaultEdge.class);
        List<Site> siteList = new ArrayList<>();
        for(int i = 0; i< txList.size(); i++) {
            Site s = new Site(node, txList.get(i));
            txList.get(i).getSiteList().add(s);
            siteList.add(s);
        }
        Site genesis = siteList.get(0);
        dag.addVertex(genesis);
        genesis.setNode(node);
        for(int i = 1; i< siteList.size(); i++){
            dag.addVertex(siteList.get(i));
            siteList.get(i).setNode(node);
            dag.addEdge(siteList.get(i), siteList.get(0));
            genesis.setCumulativeWeight(genesis.getCumulativeWeight() + siteList.get(i).getOwnWeight());
        }
        return dag;
    }

    public Site findSiteWithTx(Transaction tx) {
        if(tx != null) {
            ArrayList<Site> siteList = tx.getSiteList();
            for (Site s : siteList) {
                if (s.getNode().getID() == node.getID())
                    return s;
            }
        }
        return null;
    }

    private void processOrphanTxs() {
        boolean moreOrphans;
        do {
            moreOrphans = false;
            removeOrphansList = new ArrayList<>();
            for (Transaction t :orphans){
                moreOrphans = (moreOrphans || attemptFixOrphan(t, removeOrphansList));
                // If true, implies that some orphans have been fixed and the earlier orphans might be fixed in the next iteration of the do-while loop
            }
            for (Transaction t :removeOrphansList){
                orphans.remove(t);
            }
            removeOrphansList = null;
        } while (moreOrphans);
    }

    public void addToTangle(Transaction t){
        boolean isAdded = addToDAG(t);
        if(isAdded){
            processOrphanTxs();
        }
        else{
            orphans.add(t);
        }
    }

    private boolean attemptFixOrphan(Transaction t, ArrayList removeOrphansList) {
        boolean isAdded = addToDAG(t);
        if(isAdded) {
            removeOrphansList.add(t);
        }
        return isAdded;
    }

    public void updateCumulativeWeight(DirectedAcyclicGraph dag, Site site, int txWeight, ArrayList<Site> alreadyUpdatedCumulativeWeight) {
        try {
            Set<DefaultEdge> edges = (Set<DefaultEdge>) dag.outgoingEdgesOf(site);
            if (edges.size() != 0) {
                for (DefaultEdge e : edges) {
                    Site target = (Site) dag.getEdgeTarget(e);
                    if (!alreadyUpdatedCumulativeWeight.contains(target)) {
                        target.setCumulativeWeight(target.getCumulativeWeight() + txWeight);
                        alreadyUpdatedCumulativeWeight.add(target);
                        updateCumulativeWeight(dag, target, txWeight, alreadyUpdatedCumulativeWeight);
                    }
                }
            }
        }catch (Exception e){
            System.out.println("node id: " + node.getID());
            System.out.println("tx id: " + site.getTx().getID());
        }
    }

    private ArrayList<Transaction> selectKTips(DirectedAcyclicGraph dag) {
        ArrayList<Transaction> result = new ArrayList<>();
        ArrayList<Site> cutSet = selectCutSet(dag);
        Queue<Site> randomParticles = selectNRandomSites(cutSet, Parameters.NumofParticles);
        ArrayList<Site> selectedTips = runMonteCarloAlgorithm(dag, randomParticles);
        if(selectedTips.size() > 0) {
            for (int i = 0; i < selectedTips.size(); i++)
                if(selectedTips.get(i) != null)
                    result.add(selectedTips.get(i).getTx());
        }
        return result;
    }

    private ArrayList<Site> selectKTips_ConfCalc(DirectedAcyclicGraph dag) {
        ArrayList<Site> cutSet = selectCutSet(dag);
        Queue<Site> randomParticles = selectNRandomSites(cutSet, Parameters.NumofParticles);
        ArrayList<Site> selectedTips = runMonteCarloAlgorithm(dag, randomParticles);
        return selectedTips;
    }

    private ArrayList<Site> runMonteCarloAlgorithm(DirectedAcyclicGraph dag, Queue<Site> randomParticles) {
        tips = new ArrayList<>();

        while (randomParticles.size() > 0){
            Site s = randomParticles.remove();

//            long begin = System.currentTimeMillis();
//            System.out.println("moveTowardTips " + begin);
            Site nextS = moveTowardTips(dag, s, tips);
//            long end = System.currentTimeMillis();
//            System.out.println("moveTowardTips " + end);
            if(!isTip(dag, nextS))
                randomParticles.add(nextS);
            if (tips.size() == Parameters.K)
                break;
        }
        return tips;
    }

    private Queue<Site> selectNRandomSites(ArrayList<Site> cutSet, int n) {
        int rand = 0;
        Queue<Site> randomSet = new LinkedList<>();

        if(cutSet.size() <= n){
            for(Site s : cutSet){
                randomSet.add(s);
            }
        }
        else {
            while (randomSet.size() < n) {
                try {
                    rand = node.getSim().getSampler().getRandomNum(0, cutSet.size() - 1);
                } catch (IllegalArgumentException e) {
//                    System.out.println("Node ID " + node.getID() + " | cutSet size " + cutSet.size());
                }
                if (!randomSet.contains(cutSet.get(rand)))
                    randomSet.add(cutSet.get(rand));
            }
        }
        return randomSet;
    }

    private ArrayList<Site> selectCutSet(DirectedAcyclicGraph dag) {
        float Wmin = Float.POSITIVE_INFINITY;
        float Wmax = 0;
        float interval = 0;
//        int cw = 0;
        ArrayList<Site> cutSet = new ArrayList();
//
//        if(dag.vertexSet().size() > Parameters.bigEnough)
//            cw = Parameters.CW;
        // the first loop is for determining minimum and maximum cumulative weight in the dag
        for (Iterator it = dag.iterator(); it.hasNext(); ) {
            Site s = (Site)it.next();
            if(s.getCumulativeWeight() >= Wmax)
                Wmax = s.getCumulativeWeight();
            if(s.getCumulativeWeight() <= Wmin)
                Wmin = s.getCumulativeWeight();
        }
        interval = Wmax - Wmin;
        for (Iterator it = dag.iterator(); it.hasNext(); ) {
            Site s = (Site)it.next();
            if((s.getCumulativeWeight() >= Wmin + Parameters.beginofCutset * interval) && (s.getCumulativeWeight() <= Wmin + Parameters.endofCutset * interval)/* && (s.getCumulativeWeight() > cw)*/)
                cutSet.add(s);
        }
        return cutSet;
    }

    private Site moveTowardTips(DirectedAcyclicGraph dag, Site currentSite, ArrayList<Site> tips){
        HashMap<Site, Double> probabilityList = new HashMap<>();
        double sum = 0;
        double next;
        double probability;
        Site nextMove = currentSite;

        Set<DefaultEdge> edges = (Set<DefaultEdge>)dag.incomingEdgesOf(currentSite);
        for (DefaultEdge e : edges) {
            Site s = (Site) dag.getEdgeSource(e);
            sum += Math.exp(-1 * Parameters.ALPHA * (currentSite.getCumulativeWeight() - s.getCumulativeWeight()));
        }
        if(sum != 0)
            sum = Math.pow(sum , -1);

        if(isTip(dag, currentSite)) {
            if(!tips.contains(currentSite))
                tips.add(currentSite);
        }
        else {
            for (DefaultEdge e : edges) {
                Site source = (Site) dag.getEdgeSource(e);
                next = Math.exp(-1 * Parameters.ALPHA * (currentSite.getCumulativeWeight() - source.getCumulativeWeight()));
                probability = Double.valueOf(next * sum);
                probabilityList.put(source, probability);
            }
            nextMove = findNextRandomMove(probabilityList);
            if(isTip(dag, nextMove)  && !tips.contains(nextMove)) {
                tips.add(nextMove);
            }
        }
        return nextMove;
    }

    private Site findNextRandomMove(HashMap<Site, Double> probabilityList) {
        ArrayList<Double> list = new ArrayList<>();
        Iterator hmIterator = probabilityList.entrySet().iterator();
        Double temp = Double.valueOf(0);
        Site s = (Site) (probabilityList.keySet().toArray()[0]);
        Random r = new Random();

        if(probabilityList.size() == 1){
            return s;
        }
        else {
            list.add(temp);
            // Iterate through the hashmap
            while (hmIterator.hasNext()) {
                Map.Entry mapElement = (Map.Entry) hmIterator.next();
                temp += (Double) mapElement.getValue();
                list.add(temp);
            }
            //rand is a random number in (0,1)
            double rand = list.get(0) + r.nextDouble() * (list.get(list.size() - 1) - list.get(0));
            for (int i = 0; i < list.size()-1; i++) {
                if (rand >= list.get(i) && rand < list.get(i + 1)) {
                    s = (Site) (probabilityList.keySet().toArray()[i]);
                    break;
                }
            }
            return s;
        }
    }

    public boolean isTip(DirectedAcyclicGraph dag, Site site){
        Set<DefaultEdge> edges = (Set<DefaultEdge>)dag.incomingEdgesOf(site);
        if(edges.size() == 0)
            return true;
        else
            return false;
    }

    public Boolean addToDAG(Transaction t) {
        Boolean areAllTipsFound = true;
        for (Transaction tip : t.getParents()) {
            Site s = findSiteWithTx(tip);
            if (s == null)
                areAllTipsFound = false;
        }
        if (areAllTipsFound) {
            Site newS = new Site(node, t);
            t.getSiteList().add(newS);
            DAG.addVertex(newS);

            for (Transaction tip : t.getParents()) {
                Site s = findSiteWithTx(tip);
                DAG.addEdge(newS, s);
            }
            ArrayList<Site> alreadyUpdatedCumulativeWeight = new ArrayList<>();
            updateCumulativeWeight(DAG, newS, newS.getOwnWeight(), alreadyUpdatedCumulativeWeight);
        }
        return areAllTipsFound;
    }

    private boolean isSiteApprovedBytheTip(Site st, Site tip){
        ArrayList alreadyVisited = new ArrayList();
        return isSiteApprovedBytheTip_recursive(st, tip, alreadyVisited, false);
    }
    private boolean isSiteApprovedBytheTip_recursive(Site st, Site tip, ArrayList<Site> alreadyVisited, boolean found) {
        if(!found) {
            Set<DefaultEdge> edges = (Set<DefaultEdge>) this.getDAG().outgoingEdgesOf(tip);
            for (DefaultEdge e : edges) {
                Site target = (Site) this.getDAG().getEdgeTarget(e);
                if (target.equals(st)) {
                    found = true;
                } else {
                    if (!alreadyVisited.contains(target)) {
                        alreadyVisited.add(target);
                        found = isSiteApprovedBytheTip_recursive(st, target, alreadyVisited, found);
                    }
                }
            }
        }
        return found;
    }

    public void selectTips(Transaction transaction) {
//        int i, j;
        int areTipsConflicting = 0;
        Site victimTxSite = null, malTxSite = null, tip1 = null, tip2 = null;
        ArrayList<Transaction> tips;
        ArrayList<Site> tipsNotApprovingVictimTx;
        tips = selectKTips(DAG);
        //if tips.size() < Parameters.K, randomly select one of the transactions that are already approved by the selected tips
//            try {
//                while (tips.size() < Parameters.K) {
//                    //select sampler random tip
//                    i = network.getSampler().getRandomNum(0, tips.size() - 1);
//                    //select sampler random approved tx by the selected tip
//                    j = network.getSampler().getRandomNum(0, tips.get(i).getParents().size() - 1);
//
//                    tips.add(tips.get(i).getParents().get(j));
//                }
//            } catch (IllegalArgumentException e) {
//                System.out.println(e.getMessage());
//            }
        if(!node.attackingMode){ //for honest transactions
            if(tips.size() > 1){
                if(node.getSim().isParasiteChainRevealed) {
                    victimTxSite = findSiteWithTx(node.getSim().getTransactions().getVictimTx());
                    malTxSite = findSiteWithTx(node.getSim().getTransactions().getMaliciousTx());
                    tip1 = findSiteWithTx(tips.get(0));
                    tip2 = findSiteWithTx(tips.get(1));
                    areTipsConflicting = areTipsConflicting(victimTxSite, malTxSite, tip1, tip2);
                }
                if(areTipsConflicting > 0){
                    transaction.getParents().add(selectTheOneWithTheHigherConfRate(victimTxSite, malTxSite, tip1, tip2, areTipsConflicting));
                }else {
                    transaction.setParents(tips);
                }
            }
            else {
                transaction.setParents(tips);
            }
        } else { //for transactions arriving to the malicious node after the attack is started
            if(!transaction.getType().equals(Transaction.Type.MALICIOUS)) {//there is only one malicious tx, the rests are fake or honest
                Site victimSite = findSiteWithTx(node.getSim().getTransactions().getVictimTx());
                if (victimSite == null) //it means that victimTx is currently an orphan in the malicious node
                    transaction.getParents().add(tips.get(0));
                if (victimSite != null) {
                    tipsNotApprovingVictimTx = findTips_NotApprovingVictimTx(victimSite);
                    if (tipsNotApprovingVictimTx.size() > 0) {
                        int rand = node.getSim().getSampler().getRandomNum(0, tipsNotApprovingVictimTx.size()-1);
                        transaction.getParents().add(tipsNotApprovingVictimTx.get(rand).getTx());
                    }
                }
                if(transaction.getType().equals(Transaction.Type.HONEST)){
                    //add it to the end of the parasite chain
                    transaction.getParents().add(node.getLastTxinParasiteChain());
                    node.setLastTxinParasiteChain(transaction);
                }

            }

        }
    }

    public ArrayList<Site> getTips(){
        ArrayList<Site> tips = new ArrayList<>();
        for (Iterator it = DAG.iterator(); it.hasNext(); ) {
            Site s = (Site)it.next();
            if(s.getCumulativeWeight() == 1){
                tips.add(s);
            }
        }
        return tips;
    }

    public ArrayList<Transaction> getTxsInDAG(){
        ArrayList<Transaction> txsInDAG = new ArrayList<>();
        for (Iterator it = DAG.iterator(); it.hasNext(); ) {
            Site s = (Site)it.next();
            txsInDAG.add(s.getTx());
        }
        return txsInDAG;
    }

    public ArrayList<Site> getTipsReferringtoVictimTx(){
        Transaction victimTx = node.getSim().getTransactions().getVictimTx();
        Site victimSite = findSiteWithTx(victimTx);
        ArrayList<Site> tips = getTips();
        ArrayList<Site> tipsReferringtoVictimTx = new ArrayList<>();
        for(Site s : tips){
            if(isSiteApprovedBytheTip(victimSite, s))
                tipsReferringtoVictimTx.add(s);
        }
        return tipsReferringtoVictimTx;
    }

    public ArrayList<Site> getTipsReferringtoMaliciousTx(){
        Transaction malTx = node.getSim().getTransactions().getMaliciousTx();
        Site malSite = findSiteWithTx(malTx);
        ArrayList<Site> tips = getTips();
        ArrayList<Site> tipsReferringtoMaliciousTx = new ArrayList<>();
        for(Site s : tips){
            if(isSiteApprovedBytheTip(malSite, s))
                tipsReferringtoMaliciousTx.add(s);
        }
        return tipsReferringtoMaliciousTx;
    }

    private ArrayList<Site> findTips_NotApprovingVictimTx(Site victimSite){
        ArrayList<Site> tips = getTips();
        ArrayList<Site> tipsNotApprovingVictimTx = new ArrayList<>();
        for(Site s : tips) {
            if (s != victimSite && !isSiteApprovedBytheTip(victimSite, s)) {
                tipsNotApprovingVictimTx.add(s);
            }
        }
        return tipsNotApprovingVictimTx;
    }

    private Transaction selectTheOneWithTheHigherConfRate(Site victimTxSite, Site malTxSite, Site tip1, Site tip2, int areTipsConflicting) {
        double victimConfRate = calcConfidenceRate(victimTxSite);
        double malConfRate = calcConfidenceRate(malTxSite);
        if(victimConfRate >= malConfRate) {
            if (areTipsConflicting == 1)
                return tip1.getTx();
            else
                return tip2.getTx();
        } else if(victimConfRate < malConfRate){
            if (areTipsConflicting == 1)
                return tip2.getTx();
            else
                return tip1.getTx();
            }
        return null;
    }

    private int areTipsConflicting(Site victimTxSite, Site malTxSite, Site tip1, Site tip2) {
        if(victimTxSite == null || malTxSite == null){
            return 0;
        }
        else {
            if (((victimTxSite == tip1 || isSiteApprovedBytheTip(victimTxSite, tip1)) && (malTxSite == tip2 || isSiteApprovedBytheTip(malTxSite, tip2))))
                return 1;
            else if (((malTxSite == tip1 || isSiteApprovedBytheTip(malTxSite, tip1)) && (victimTxSite == tip2 || isSiteApprovedBytheTip(victimTxSite, tip2))))
                return 2;
            else
                return 0;
        }
    }

    public DirectedAcyclicGraph getDAG() {
        return DAG;
    }

    public void setDAG(DirectedAcyclicGraph DAG) {
        this.DAG = DAG;
    }

    public ArrayList<Transaction> getOrphans() {
        return orphans;
    }

    public void setOrphans(ArrayList<Transaction> orphans) {
        this.orphans = orphans;
    }

    public ArrayList<Transaction> getRemoveOrphansList() {
        return removeOrphansList;
    }

    public void setRemoveOrphansList(ArrayList<Transaction> removeOrphansList) {
        this.removeOrphansList = removeOrphansList;
    }

}
