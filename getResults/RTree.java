import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RTree {

    public static HashMap<Integer, ArrayList<RtreeNode>> root=new HashMap<Integer, ArrayList<RtreeNode>>();
    public static ArrayList<String> index=new ArrayList<String>();
    public static ArrayList<SpatialRelation> sprList=new ArrayList<SpatialRelation>();
    public static TreeSet<SpatialRelation> spatialRelationTreeSet=new TreeSet<SpatialRelation>();
    public static HashMap<Integer, ArrayList<SpatialRelation>> leaf=new HashMap<Integer, ArrayList<SpatialRelation>>();
    public static HashMap<String, ArrayList<RtreeNode>> middle=new HashMap<String, ArrayList<RtreeNode>>();
    public static int size=0;

    /**
     *
     * @throws IOException
     */
    public static void bulkLoading() throws IOException {//deciding the node size to be 10, we need to first get all leaf and construct back
        Double xupper, xlower, yupper, ylower;
        SpatialRelation tem;
        readFile();
        dataPreprocessing();
        TreeSet<SpatialRelation> spr_undu = new TreeSet<SpatialRelation>(new latComparator());
        for(SpatialRelation sr : sprList){
            spr_undu.add(sr);
        }
        Iterator<SpatialRelation> iterator = spr_undu.iterator();
        size=(spr_undu.size()/10)+1;
        for(int i=0;i<size;i++){

            ArrayList<SpatialRelation> asr=new ArrayList<SpatialRelation>();
            ylower=Double.MAX_VALUE;
            yupper=Double.MIN_VALUE;
            xlower=Double.MAX_VALUE;
            xupper=Double.MIN_VALUE;
            for(int j=0;j<10;j++){


                SpatialRelation spatialRelation=iterator.next();

                if(j==0) xlower=spatialRelation.getLatitude();

                if(yupper<spatialRelation.getLongitude()) yupper=spatialRelation.getLongitude();
                if(ylower>spatialRelation.getLongitude()) ylower=spatialRelation.getLongitude();
                if(j==9) {
                    xupper=spatialRelation.getLatitude();
                    spatialRelation.xupper=xupper;
                    spatialRelation.xlower=xlower;
                    spatialRelation.ylower=ylower;
                    spatialRelation.yupper=yupper;
                }
                if(!iterator.hasNext()){
                    xupper=spatialRelation.getLatitude();
                    spatialRelation.xupper=xupper;
                    spatialRelation.xlower=xlower;
                    spatialRelation.ylower=ylower;
                    spatialRelation.yupper=yupper;
                    asr.add(spatialRelation);

                    break;
                }
                asr.add(spatialRelation);

            }
            leaf.put(i,asr);
        }
        buildingLeaf(1);//start from level 1
        Integer level =2;//starts from level 2
        while (level<(Math.log(spr_undu.size())/Math.log(10))){

            buildingNonLeaf(level);
            level++;
        }
        buildingRoot(level);
        Set<String> keySet=middle.keySet();
//        for(String s: keySet){
//
//            if(s.equals("6,0")) System.out.println(s);
//        }


        System.out.println("Rtree bulkloading finished");

    }

    /**
     *
     * @param level
     */
    public static void buildingLeaf(Integer level){//input is the size of the current highest level
        Integer count=0;
        Double xupper,xlower,yupper,ylower;
        for(Integer i=0;i<(size/10)+1;i++){
            ylower=Double.MAX_VALUE;
            yupper=Double.MIN_VALUE;
            xlower=Double.MAX_VALUE;
            xupper=Double.MIN_VALUE;
            ArrayList<RtreeNode> asr=new ArrayList<RtreeNode>();
            for(int j=0;j<10;j++){//handling of the end of the tree, not enough entry, needs to be done
                SpatialRelation spatialRelation=new SpatialRelation();
                if(leaf.get(count).size()>9){
                    spatialRelation= leaf.get(count).get(9);
                }
                else{
                    spatialRelation= leaf.get(count).get(leaf.get(count).size()-1);
                }
                if(j==0) xlower=spatialRelation.xlower;
                RtreeNode rtn=new RtreeNode(count.toString());
                if(i==size/10){
                    if(count>=leaf.size()){
                        break;
                    }
                    if(count==leaf.size()-1){
                        if(yupper<spatialRelation.yupper) yupper=spatialRelation.yupper;
                        if(ylower>spatialRelation.ylower) ylower=spatialRelation.ylower;
                        xupper=spatialRelation.xupper;

                        rtn.xupper=xupper;
                        rtn.xlower=xlower;
                        rtn.ylower=ylower;
                        rtn.yupper=yupper;
                        asr.add(rtn);
                        break;
                    }
                }

                //handling of MBR needs to be done here before add

                if(j==0) xlower=spatialRelation.xlower;

                if(yupper<spatialRelation.yupper) yupper=spatialRelation.yupper;
                if(ylower>spatialRelation.ylower) ylower=spatialRelation.ylower;
                if(j==9) {
                    xupper=spatialRelation.xupper;
                    rtn.xupper=xupper;
                    rtn.xlower=xlower;
                    rtn.ylower=ylower;
                    rtn.yupper=yupper;
                }

                asr.add(rtn);
                count++;

            }
            middle.put(level.toString()+","+i.toString(),asr);
        }
        size=(size/10)+1;
    }

    /**
     *
     * @param level
     */
    public static void buildingNonLeaf( Integer level){//input is the size of the current highest level
        Integer count=0;
        Double xupper,xlower,yupper,ylower;
        for(Integer i=0;i<(size/10)+1;i++){
            ylower=Double.MAX_VALUE;
            yupper=Double.MIN_VALUE;
            xlower=Double.MAX_VALUE;
            xupper=Double.MIN_VALUE;
            ArrayList<RtreeNode> asr=new ArrayList<RtreeNode>();
            for(int j=0;j<10;j++){//handling of the end of the tree, not enough entry, needs to be done
                Integer lowerLevel=level-1;
                RtreeNode rtn=new RtreeNode(lowerLevel.toString()+","+count.toString());
                //This part to be finished

                if(i==size/10){
                    if(9>=middle.get(rtn.pointer).size()){
//                        System.out.println(rtn.pointer);
                        xupper=middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).xupper;
                        if(j==0){
                            xlower=middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).xlower;
                        }
                        //problem below, to be solved.
                        if(yupper<middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).yupper) yupper=middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).yupper;
                        if(ylower>middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).ylower) xlower=middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).ylower;
//                        xlower=middle.get(rtn.pointer).get(0).xlower;
                        rtn.xlower=xlower;
                        rtn.xupper=xupper;
                        rtn.yupper=yupper;
                        rtn.ylower=ylower;
                        asr.add(rtn);
                        break;
                    }
                }

                if(j==0) xlower=middle.get(rtn.pointer).get(9).xlower;
                if(yupper<middle.get(rtn.pointer).get(9).yupper) yupper=middle.get(rtn.pointer).get(9).yupper;
                if(ylower>middle.get(rtn.pointer).get(9).ylower) ylower=middle.get(rtn.pointer).get(9).ylower;
                if(j==9) {
                    xupper=middle.get(rtn.pointer).get(9).xupper;
                    rtn.ylower=ylower;
                    rtn.yupper=yupper;
                    rtn.xupper=xupper;
                    rtn.xlower=xlower;
                }

                //handling of MBR needs to be done here before add
                asr.add(rtn);
                count++;

            }
            middle.put(level.toString()+","+i.toString(),asr);
        }
        size=(size/10)+1;
    }

    /**
     *
     * @param level
     */
    public static void buildingRoot(Integer level){
        Integer count=0;
        Double xupper,xlower,yupper,ylower;
        for(Integer i=0;i<(size/10)+1;i++){
            ylower=Double.MAX_VALUE;
            yupper=Double.MIN_VALUE;
            xlower=Double.MAX_VALUE;
            xupper=Double.MIN_VALUE;
            ArrayList<RtreeNode> asr=new ArrayList<RtreeNode>();
            for(int j=0;j<10;j++){//handling of the end of the tree, not enough entry, needs to be done
                Integer lowerLevel=level-1;
                RtreeNode rtn=new RtreeNode(lowerLevel.toString()+","+count.toString());
                //This part to be finished


                if(i==size/10){
                    if(9>=middle.get(rtn.pointer).size()){
//                        System.out.println(rtn.pointer);
                        xupper=middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).xupper;
                        xlower=middle.get(rtn.pointer).get(0).xlower;
                        //problem below, to be solved.
                        if(yupper<middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).yupper) yupper=middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).yupper;
                        if(ylower>middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).ylower) xlower=middle.get(rtn.pointer).get(middle.get((rtn.pointer)).size()-1).ylower;
                        rtn.xlower=xlower;
                        rtn.xupper=xupper;
                        rtn.yupper=yupper;
                        rtn.ylower=ylower;
                        asr.add(rtn);
                        break;
                    }
                }


            }
            root.put(i,asr);
        }
        size=(size/10)+1;



    }

    /**
     *
     * @throws IOException
     */
    public static void readFile() throws IOException {
        String filename = "Gowalla_totalCheckins.txt";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        SpatialRelation s;
        while ( (line=br.readLine()) != null ) {
            s = processLine(line);
            if (s!=null) {
                sprList.add(s);

            }
        }
        br.close();
    }
    private static SpatialRelation processLine(String line) {
        SpatialRelation spr = new SpatialRelation();

        String[] result = line.split("	");

        //if ( result.length != 7 ) return null;

        spr.setLocationid(result[4]);
        spr.setLatitude(Double.parseDouble(result[2]));
        spr.setLongitude(Double.parseDouble(result[3]));

        return spr;

    }
    private static void dataPreprocessing() {
        ArrayList<SpatialRelation> SpRlist_tem = new ArrayList<SpatialRelation>();
        // delete the data with invalid latitude or longitude
        for(SpatialRelation s : sprList) {
            if(s.getLatitude()==null || s.getLongitude()==null) {
                continue;
            }
            SpRlist_tem.add(s);
        }
        sprList=SpRlist_tem;
        SpRlist_tem = new ArrayList<SpatialRelation>();


    }

    /**
     *
     * @param x
     * @param y
     * @param index_path
     * @param k
     * @param n
     * @return search result
     */
    public static String searchRTree(double x, double y, String index_path, int k, int n) {
        PriorityQueue<searchingEntry> BFSearch = new PriorityQueue<searchingEntry>();
        PriorityQueue<SpatialRelation> BFSearchResult = new PriorityQueue<SpatialRelation>();
        String result = "";

        Double xlower, xupper, ylower, yupper;
        //calculate the MBR and the insertion
        for (Integer i = 0; i < root.size(); i++) {
            ArrayList<RtreeNode> tem = root.get(i);
            Double dist = Double.MAX_VALUE;
            if (tem.size() == 10) {
                xupper = tem.get(9).xupper;
                yupper = tem.get(9).yupper;
                xlower = tem.get(9).xlower;
                ylower = tem.get(9).ylower;
                if (getResults.calEuclideanDistance(xupper, yupper, x, y) < dist) {
                    dist = getResults.calEuclideanDistance(xupper, yupper, x, y);
                }
                if (getResults.calEuclideanDistance(xupper, ylower, x, y) < dist) {
                    dist = getResults.calEuclideanDistance(xupper, ylower, x, y);
                }
                if (getResults.calEuclideanDistance(xlower, ylower, x, y) < dist) {
                    dist = getResults.calEuclideanDistance(xlower, ylower, x, y);
                }
                if (getResults.calEuclideanDistance(xlower, yupper, x, y) > dist) {
                    dist = getResults.calEuclideanDistance(xlower, yupper, x, y);
                }
                if ((y > ylower) && (y < yupper)) {
                    if ((x >= xlower) && (x <= xupper)) {
                        dist = 0.0;
                    } else {
                        if (x > xupper) {
                            dist = x - xupper;
                        } else if (x < xlower) {
                            dist = xlower - x;
                        }
                    }
                }
                if ((x > xlower) && (x < xupper)) {
                    if ((y >= ylower) && (y <= yupper)) {
                        dist = 0.0;
                    } else {
                        if (y > yupper) {
                            dist = y - yupper;
                        } else if (y < ylower) {
                            dist = ylower - y;
                        }
                    }
                }
                BFSearch.add(new searchingEntry("5,"+i.toString(), dist));
            } else {
                int pos = tem.size() - 1;
                xupper = tem.get(pos).xupper;
                yupper = tem.get(pos).yupper;
                xlower = tem.get(pos).xlower;
                ylower = tem.get(pos).ylower;
                if (getResults.calEuclideanDistance(xupper, yupper, x, y) < dist) {
                    dist = getResults.calEuclideanDistance(xupper, yupper, x, y);
                }
                if (getResults.calEuclideanDistance(xupper, ylower, x, y) < dist) {
                    dist = getResults.calEuclideanDistance(xupper, ylower, x, y);
                }
                if (getResults.calEuclideanDistance(xlower, ylower, x, y) < dist) {
                    dist = getResults.calEuclideanDistance(xlower, ylower, x, y);
                }
                if (getResults.calEuclideanDistance(xlower, yupper, x, y) > dist) {
                    dist = getResults.calEuclideanDistance(xlower, yupper, x, y);
                }
                if ((y > ylower) && (y < yupper)) {
                    if ((x >= xlower) && (x <= xupper)) {
                        dist = 0.0;
                    } else {
                        if (x > xupper) {
                            dist = x - xupper;
                        } else if (x < xlower) {
                            dist = xlower - x;
                        }
                    }
                }
                if ((x > xlower) && (x < xupper)) {
                    if ((y >= ylower) && (y <= yupper)) {
                        dist = 0.0;
                    } else {
                        if (y > yupper) {
                            dist = y - yupper;
                        } else if (y < ylower) {
                            dist = ylower - y;
                        }
                    }
                }
                BFSearch.add(new searchingEntry("5,"+i.toString(), dist));
            }

        }
        int entryNum = 0;
        searchingEntry searchingEntry;
        Integer level=1;
        boolean indi=false;

        while (entryNum < k) {
            boolean normalIndicator=true;
            double t=Double.MAX_VALUE;
            PriorityQueue<SpatialRelation> temp = new PriorityQueue<SpatialRelation>();
            if(BFSearchResult.size()>=k) {//update the value of t
                for(int i=0;i<k;i++) {
                    SpatialRelation spatialRelation = BFSearchResult.poll();
                    temp.add(spatialRelation);
                    if (i == k - 1) {
                        t = spatialRelation.getDistance();
                    }
                }
                for(int i=0;i<k;i++){
                    BFSearchResult.add(temp.poll());
                }
            }

            //do operations next
            //use this to get the key of next level, it maybe distinguished by different type.
            searchingEntry=BFSearch.poll();
//            if(searchingEntry.distance==0.0){
//                System.out.println(searchingEntry.key);
//            }
            if((BFSearchResult.size()>k) && searchingEntry.distance>=t){
                for(int i=0;i<k;i++){
//                    System.out.println(BFSearchResult.peek().getDistance());
                    if(i==k-1){
                        result+=BFSearchResult.poll().getLocationid();
                        break;
                    }
                    result+=BFSearchResult.poll().getLocationid()+", ";
                }

                break;
            }

            if(searchingEntry.key.contains(",")){
                String[] str= searchingEntry.key.split(",");
                level=Integer.parseInt(str[0])-1;
                indi=true;
            }
            else{
                level=6;
                indi=false;
            }
            String[] str= searchingEntry.key.split(",");
//            System.out.println(searchingEntry.key);
            //another handling needed to be done for the leaf entry
            if(level==-1){
                normalIndicator=false;
                ArrayList<SpatialRelation> tem=leaf.get(Integer.parseInt(str[1]));//this should be the original one
                for(Integer i=0;i<10;i++){
//                    Integer id=Integer.parseInt(str[1])*10+i;
//                    System.out.println(id);


                    if(tem==null) {
                        continue;
                    }
                    if(tem.size()==10){

                        Double distance=getResults.calEuclideanDistance(tem.get(i).getLatitude(),tem.get(i).getLongitude(),x,y);
                        SpatialRelation spatialRelation=tem.get(i);
                        spatialRelation.setDistance(distance);
                        BFSearchResult.add(spatialRelation);

                    }
                    else{
                        int range=tem.size();

                        Double distance=getResults.calEuclideanDistance(tem.get(i).getLatitude(),tem.get(i).getLongitude(),x,y);
                        SpatialRelation spatialRelation=tem.get(i);
                        spatialRelation.setDistance(distance);
                        BFSearchResult.add(spatialRelation);

                    }
                }
                continue;
            }
            if(level==0 ){
                normalIndicator=false;
                //this time should get from leaf instead of middle, but not the exact entry yet.
                for(Integer i=0;i<10;i++){
                    ArrayList<SpatialRelation> tem= leaf.get((Integer.parseInt(str[1])*10+i));
                    Double dist = Double.MAX_VALUE;
                    if (tem.size() == 10) {
                        xupper = tem.get(9).xupper;
                        yupper = tem.get(9).yupper;
                        xlower = tem.get(9).xlower;
                        ylower = tem.get(9).ylower;
                        if (getResults.calEuclideanDistance(xupper, yupper, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xupper, yupper, x, y);
                        }
                        if (getResults.calEuclideanDistance(xupper, ylower, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xupper, ylower, x, y);
                        }
                        if (getResults.calEuclideanDistance(xlower, ylower, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xlower, ylower, x, y);
                        }
                        if (getResults.calEuclideanDistance(xlower, yupper, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xlower, yupper, x, y);
                        }
                        if ((y > ylower) && (y < yupper)) {
                            if ((x >= xlower) && (x <= xupper)) {
                                dist = 0.0;
                            } else {
                                if (x > xupper) {

                                    dist = x - xupper;
                                } else if (x < xlower) {
                                    dist = xlower - x;
                                }
                            }
                        }
                        if ((x > xlower) && (x < xupper)) {
                            if ((y >= ylower) && (y <= yupper)) {
                                dist = 0.0;
                            } else {
                                if (y > yupper) {
                                    dist = y - yupper;
                                } else if (y < ylower) {
                                    dist = ylower - y;
                                }
                            }
                        }
                        BFSearch.add(new searchingEntry(level.toString()+","+(Integer.parseInt(searchingEntry.key.split(",")[1])*10+i), dist));
                    } else {
                        int pos = tem.size() - 1;
                        xupper = tem.get(pos).xupper;
                        yupper = tem.get(pos).yupper;
                        xlower = tem.get(pos).xlower;
                        ylower = tem.get(pos).ylower;
                        if (getResults.calEuclideanDistance(xupper, yupper, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xupper, yupper, x, y);
                        }
                        if (getResults.calEuclideanDistance(xupper, ylower, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xupper, ylower, x, y);
                        }
                        if (getResults.calEuclideanDistance(xlower, ylower, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xlower, ylower, x, y);
                        }
                        if (getResults.calEuclideanDistance(xlower, yupper, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xlower, yupper, x, y);
                        }
                        if ((y > ylower) && (y < yupper)) {
                            if ((x >= xlower) && (x <= xupper)) {
                                dist = 0.0;
                            } else {
                                if (x > xupper) {
                                    dist = x - xupper;
                                } else if (x < xlower) {
                                    dist = xlower - x;
                                }
                            }
                        }
                        if ((x > xlower) && (x < xupper)) {
                            if ((y >= ylower) && (y <= yupper)) {
                                dist = 0.0;
                            } else {
                                if (y > yupper) {
                                    dist = y - yupper;
                                } else if (y < ylower) {
                                    dist = ylower - y;
                                }
                            }
                        }
                        BFSearch.add(new searchingEntry(level.toString()+","+(Integer.parseInt(searchingEntry.key.split(",")[1])*10+i), dist));
                    }

                }
                continue;
            }
            //for non-leaf node
            //this is for the root node
            if(level==100) {
                normalIndicator=false;
                for (Integer i = 0; i < 1; i++) {
                    ArrayList<RtreeNode> tem = root.get(i);
                    Double dist = Double.MAX_VALUE;
                    if (tem.size() == 10) {
                        xupper = tem.get(9).xupper;
                        yupper = tem.get(9).yupper;
                        xlower = tem.get(9).xlower;
                        ylower = tem.get(9).ylower;
                        if (getResults.calEuclideanDistance(xupper, yupper, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xupper, yupper, x, y);
                        }
                        if (getResults.calEuclideanDistance(xupper, ylower, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xupper, ylower, x, y);
                        }
                        if (getResults.calEuclideanDistance(xlower, ylower, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xlower, ylower, x, y);
                        }
                        if (getResults.calEuclideanDistance(xlower, yupper, x, y) > dist) {
                            dist = getResults.calEuclideanDistance(xlower, yupper, x, y);
                        }
                        if ((y > ylower) && (y < yupper)) {
                            if ((x >= xlower) && (x <= xupper)) {
                                dist = 0.0;
                            } else {
                                if (x > xupper) {
                                    dist = x - xupper;
                                } else if (x < xlower) {
                                    dist = xlower - x;
                                }
                            }
                        }
                        if ((x > xlower) && (x < xupper)) {
                            if ((y >= ylower) && (y <= yupper)) {
                                dist = 0.0;
                            } else {
                                if (y > yupper) {
                                    dist = y - yupper;
                                } else if (y < ylower) {
                                    dist = ylower - y;
                                }
                            }
                        }
                        BFSearch.add(new searchingEntry(level.toString() + "," + (Integer.parseInt(searchingEntry.key) * 10 + i), dist));
                    } else {
                        int pos = tem.size() - 1;
                        xupper = tem.get(pos).xupper;
                        yupper = tem.get(pos).yupper;
                        xlower = tem.get(pos).xlower;
                        ylower = tem.get(pos).ylower;
                        if (getResults.calEuclideanDistance(xupper, yupper, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xupper, yupper, x, y);
                        }
                        if (getResults.calEuclideanDistance(xupper, ylower, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xupper, ylower, x, y);
                        }
                        if (getResults.calEuclideanDistance(xlower, ylower, x, y) < dist) {
                            dist = getResults.calEuclideanDistance(xlower, ylower, x, y);
                        }
                        if (getResults.calEuclideanDistance(xlower, yupper, x, y) > dist) {
                            dist = getResults.calEuclideanDistance(xlower, yupper, x, y);
                        }
                        if ((y > ylower) && (y < yupper)) {
                            if ((x >= xlower) && (x <= xupper)) {
                                dist = 0.0;
                            } else {
                                if (x > xupper) {
                                    dist = x - xupper;
                                } else if (x < xlower) {
                                    dist = xlower - x;
                                }
                            }
                        }
                        if ((x > xlower) && (x < xupper)) {
                            if ((y >= ylower) && (y <= yupper)) {
                                dist = 0.0;
                            } else {
                                if (y > yupper) {
                                    dist = y - yupper;
                                } else if (y < ylower) {
                                    dist = ylower - y;
                                }
                            }
                        }
                        BFSearch.add(new searchingEntry(level.toString() + "," + (Integer.parseInt(searchingEntry.key) * 10 + i), dist));
                    }

                }
                continue;
            }//obsoleted code
            if(normalIndicator==false){
                continue;
            }
            for(Integer i=0;i<10;i++){
                ArrayList<RtreeNode> tem= middle.get(level.toString()+","+((Integer.parseInt(str[1])*10+i)));
                if(tem==null){
                    continue;
                }
                Double dist = Double.MAX_VALUE;
                if (tem.size() == 10) {
                    xupper = tem.get(9).xupper;
                    yupper = tem.get(9).yupper;
                    xlower = tem.get(9).xlower;
                    ylower = tem.get(9).ylower;
                    if (getResults.calEuclideanDistance(xupper, yupper, x, y) < dist) {
                        dist = getResults.calEuclideanDistance(xupper, yupper, x, y);
                    }
                    if (getResults.calEuclideanDistance(xupper, ylower, x, y) < dist) {
                        dist = getResults.calEuclideanDistance(xupper, ylower, x, y);
                    }
                    if (getResults.calEuclideanDistance(xlower, ylower, x, y) < dist) {
                        dist = getResults.calEuclideanDistance(xlower, ylower, x, y);
                    }
                    if (getResults.calEuclideanDistance(xlower, yupper, x, y) < dist) {
                        dist = getResults.calEuclideanDistance(xlower, yupper, x, y);
                    }
                    if ((y > ylower) && (y < yupper)) {
                        if ((x >= xlower) && (x <= xupper)) {
                            dist = 0.0;
                        } else {
                            if (x > xupper) {
                                dist = x - xupper;
                            } else if (x < xlower) {
                                dist = xlower - x;
                            }
                        }
                    }
                    if ((x > xlower) && (x < xupper)) {
                        if ((y >= ylower) && (y <= yupper)) {
                            dist = 0.0;
                        } else {
                            if (y > yupper) {
                                dist = y - yupper;
                            } else if (y < ylower) {
                                dist = ylower - y;
                            }
                        }
                    }
                    BFSearch.add(new searchingEntry(level.toString()+","+(Integer.parseInt(searchingEntry.key.split(",")[1])*10+i), dist));
                } else {
                    int pos = tem.size() - 1;
                    xupper = tem.get(pos).xupper;
                    yupper = tem.get(pos).yupper;
                    xlower = tem.get(pos).xlower;
                    ylower = tem.get(pos).ylower;
                    if (getResults.calEuclideanDistance(xupper, yupper, x, y) < dist) {
                        dist = getResults.calEuclideanDistance(xupper, yupper, x, y);
                    }
                    if (getResults.calEuclideanDistance(xupper, ylower, x, y) < dist) {
                        dist = getResults.calEuclideanDistance(xupper, ylower, x, y);
                    }
                    if (getResults.calEuclideanDistance(xlower, ylower, x, y) < dist) {
                        dist = getResults.calEuclideanDistance(xlower, ylower, x, y);
                    }
                    if (getResults.calEuclideanDistance(xlower, yupper, x, y) < dist) {
                        dist = getResults.calEuclideanDistance(xlower, yupper, x, y);
                    }
                    if ((y > ylower) && (y < yupper)) {
                        if ((x >= xlower) && (x <= xupper)) {
                            dist = 0.0;
                        } else {
                            if (x > xupper) {
                                dist = x - xupper;
                            } else if (x < xlower) {
                                dist = xlower - x;
                            }
                        }
                    }
                    if ((x > xlower) && (x < xupper)) {
                        if ((y >= ylower) && (y <= yupper)) {
                            dist = 0.0;
                        } else {
                            if (y > yupper) {
                                dist = y - yupper;
                            } else if (y < ylower) {
                                dist = ylower - y;
                            }
                        }
                    }
                    BFSearch.add(new searchingEntry(level.toString()+","+(Integer.parseInt(searchingEntry.key.split(",")[1])*10+i), dist));
                }

            }
            continue;
        }

        return result;
    }
}

//        for(int i=0;i<10;i++){
//            Double xlower=0.0;
//            Double xupper=0.0;
//            if(i==9){
//                tem=iterator.next();
//                xlower= tem.getLatitude();
//                while (iterator.hasNext()){
//                    tem=iterator.next();
//                    if(!(iterator.hasNext())){
//                        xupper=tem.getLatitude();
//                    }
//                }
//                index.add(xlower.toString()+","+xupper.toString());
//                break;
//            }
//            for(int j=0;j<span;j++){
//                if(j==0){
//                    tem=iterator.next();
//                    xlower=tem.getLatitude();
//                }
//                if(j==span-1){
//                    tem=iterator.next();
//                    xupper=tem.getLatitude();
//                    break;
//                }
//                else if(j!=0){
//                    iterator.next();
//                }
//
//            }
//            index.add(xlower.toString()+","+xupper.toString());
//        }
//        int count=0;
//        for(String s: index){
//            count=0;
//            root.put(s,new ArrayList<RtreeNode>());
//            String[] bound=s.split(",");
//            iterator = spr_undu.iterator();
//            SpatialRelation sr=iterator.next();
//            count++;
//            while (sr.getLatitude()<=Double.parseDouble(bound[1])){
//
//                //actions
////                createNode(count/9, spr_undu);
//                sr=iterator.next();
//                count++;
//            }
//        }