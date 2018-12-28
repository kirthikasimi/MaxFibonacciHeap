import java.io.*;
import java.util.*;
import java.util.regex.*;
public class keywordcounter{

    public static final class Node
    {       public Node parent;
            public Node child;
            public Node leftSib;
            public Node rightSib;
            public boolean childCut = false; 
            int degree = 0;       
            public String key;
            int value;
    
            Node(String key,int value)
            {
               this.parent = null;
               this.leftSib = this;
               this.rightSib = this;
               this.degree = 0;
               this.key = key;
               this.value=value;
            }
       
        }

       static HashMap<String,Node> hashmap = new HashMap();
        public Node maxNode=null;
       public int numNodes=0;
       
       public void insertNode(Node newNode) {
    	  	
        if(maxNode == null)
        {
             maxNode= newNode;
        }
        else 
        {
            //adding newnode to the right of max node
            newNode.rightSib = maxNode.rightSib;
            newNode.leftSib = maxNode;
            maxNode.rightSib = newNode;
    
            if ( newNode.rightSib==null)
            {
                maxNode.leftSib = newNode;
                newNode.rightSib = maxNode;
               
            }
            if ( newNode.rightSib!=null) {                               
                newNode.rightSib.leftSib = newNode;
            }
            if (newNode.value > maxNode.value)
             {
                maxNode = newNode;
            }
        }
      numNodes=numNodes+1;
    }

    public void increaseKey(Node n, int i)
    {
        
        n.value = i;
        Node m = n.parent;
        if ( m!=null && (n.value > m.value)) 
        {
            cut(n, m);
            cascadingCut(m);
        }   
        if (n.value > maxNode.value) 
        {
            maxNode = n;
        }
    }

    public void cut(Node x, Node y)
    {
        // remove x as child of y 
        x.leftSib.rightSib = x.rightSib;
        x.rightSib.leftSib = x.leftSib;
        y.degree--;

        // Assign y.child if the child is the removed node
        if (y.child == x) {
            y.child = x.rightSib;
        }
       //If the degree of y becomes zero after removal of child then y.child is null
        if (y.degree == 0)
         {
            y.child = null;
        }

        // adding x to rootlist 
        x.leftSib = maxNode;
        x.rightSib = maxNode.rightSib;
        maxNode.rightSib = x;
        x.rightSib.leftSib = x;
        // setting parent of x to nil
        x.parent = null;
        // setting mark of x to false
        x.childCut= false;
    }

 
    public void cascadingCut(Node x)
    {
        Node y = x.parent;

        if (y != null) 
        {
           
            if (!x.childCut) 
            {
                x.childCut = true;
            } 
            else 
            {
                cut(x,y);
                cascadingCut(y);
            }
        }
    }

    public Node removeMax()
    {
        Node max = maxNode;
        if (max != null)
         {
           
            Node mc = max.child;
            Node temp;
            int numChildren = max.degree;
            //Removing the children of maxNode
            while (numChildren>0) {
                temp= mc.rightSib;

                // removing maxnode child from child list
                mc.leftSib.rightSib = mc.rightSib;
                mc.rightSib.leftSib = mc.leftSib;

                // adding maxNode child to the rootlist 
                mc.rightSib = maxNode.rightSib;
                mc.leftSib = maxNode;
                maxNode.rightSib = mc;
                mc.rightSib.leftSib = mc;

                // setting parent to null
                mc.parent = null;
                // moving to the right child of maxNode
                mc= temp;
                //decreasing the number of children of maxNode
                numChildren=numChildren-1;

            }


            // remove max from  the rootlist 
            max.leftSib.rightSib = max.rightSib;
            max.rightSib.leftSib = max.leftSib;

            if (max == max.rightSib)
             {
                maxNode = null;

            }
             else 
             {
               maxNode = max.rightSib;
               degreeMerge();
           }
           numNodes=numNodes-1;
           return max;
       }
        return null;
    }

    public void degreeMerge()
    {
        
        int degreeTableSize =50;
        ArrayList<Node> degreeTable =new ArrayList<Node>(degreeTableSize);

        // Initializing the degree table with null
       for (int i = 0; i < degreeTableSize; i++)
        {
            degreeTable.add(null);
        }
        
        int numRoots = 0;
        Node max = maxNode;
        if (max != null)
         {
            numRoots++;
            max = max.rightSib;                     

            while (max != maxNode)
             {
                numRoots++;
                max = max.rightSib;
            }
        }

        while (numRoots > 0)
         {

            int d = max.degree;
            Node nextNode = max.rightSib;

            // If the degree is  in degreetable then combine and merge else add to the degreetable
            while(true) 
            {
                Node x = degreeTable.get(d);
                if (x == null) {
                   
                    break;
                }

                if (x.value > max.value)
                 {
                    Node temp = x;
                    x = max;
                    max = temp;
                }

                //Making max the child of x 
                makeChild(x, max);
                degreeTable.set(d, null);
                d++;
            }

            //Insert the newly merged node into the  degreetable 
            degreeTable.set(d, max);
            max = nextNode;
            numRoots=numRoots-1;
        }

        maxNode = null;

        for (int i = 0; i < degreeTableSize; i++) 
        {
            Node k = degreeTable.get(i);
            if (k == null) 
            {
                continue;
            }

            if (maxNode == null) 
            {
                maxNode = k;
            }
            else 
            {
               // removing node from rootlist
               k.leftSib.rightSib = k.rightSib;
               k.rightSib.leftSib = k.leftSib;

               // adding to rootlist
               k.leftSib = maxNode;
               k.rightSib = maxNode.rightSib;
               maxNode.rightSib= k;
               k.rightSib.leftSib = k;

               if (k.value > maxNode.value)
                {
                   maxNode = k;
               }
            }
        }
    }

    //Makes y the child of node x
    public void makeChild(Node x, Node y)
    {    
        // remove y from root list of heap
        x.leftSib.rightSib = x.rightSib;
        x.rightSib.leftSib = x.leftSib;

        // make y a child of x
        x.parent = y;

        if (y.child!=null) 
        {
            x.leftSib = y.child;
            x.rightSib = y.child.rightSib;
            y.child.rightSib = x;
            x.rightSib.leftSib = x;

           
        } else {
           
            y.child = x;
            x.rightSib = x;
            x.leftSib = x;
        }

        // increasing degree of y
        y.degree++;

        // mark x as false
        x.childCut = false;
    }


    public static void main(String[] args){
      String inFile = args[0];
      String outFile= "output_file.txt";
      BufferedReader br=null;
      BufferedWriter bw=null;
      keywordcounter kwc=new keywordcounter();
      try {

        br = new BufferedReader(new FileReader(inFile));
        bw = new BufferedWriter(new FileWriter(outFile));
        String str = br.readLine();
       
       
        while(str!=null){
            String string[]=str.split(" ");
            
           
            if (string.length>1) {

                String hashkey=string[0].substring(string[0].indexOf("$") + 1);
                int hashvalue = Integer.parseInt(string[1]);
                if ( !hashmap.containsKey(hashkey))
                {   
                    //Create new node and insert in fibonacci heap and hash map
                    Node node = new Node(hashkey,hashvalue);
                    kwc.insertNode(node);
                    hashmap.put(hashkey,node); 
                }
                else
                {
                   //If the word is already hashmap then  increaseKey 
                   int increaseKey = hashmap.get(hashkey).value + hashvalue;
                   kwc.increaseKey(hashmap.get(hashkey),increaseKey);         
                }
            }
            else if(string[0].equalsIgnoreCase("stop"))
            {
                break;
            }
            else 
            {
               
                int numRemoveNodes = Integer.parseInt(string[0]);

                ArrayList<Node> removedNodes= new ArrayList<Node>(numRemoveNodes);
               
                for ( int i=0;i<numRemoveNodes;i++)
                {

                    //Removing the maxNode
                    Node node = kwc.removeMax();
                    //Remove the maxNode from hashmap
                    hashmap.remove(node.key);
                    //Creating new node 
                    Node newNode= new Node(node.key,node.value);
                    //Adding the newnode to removed nodes list
                    removedNodes.add(newNode);

                    if ( i <numRemoveNodes-1) 
                    {
                        bw.write(node.key + ",");
                    }
                    else 
                    {
                        bw.write(node.key);
                    }

                }
                for(int j=0;j<numRemoveNodes;j++)
                {
                    Node n= removedNodes.get(j);
                    kwc.insertNode(n);
                    hashmap.put(n.key,n);
                }
                   
                bw.newLine();
            }
            //Reading the next input line
            str = br.readLine();
        }
    }

    catch(Exception e){
        e.printStackTrace();
        System.out.println(e);
    }
        //Closing  the bufferedWriter
    finally {
        if ( bw != null ) {
            try {
                bw.close();
            } catch (IOException ioe2) 
            {
            }
        }
    }

    }
}