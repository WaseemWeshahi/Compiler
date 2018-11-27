/*
* Homework 2 in course: Compilers
* Submitted By:
* Waseem Weshahi          206943391
* Bayan Farhan       	   208300145    
* Omar Khateeb            211706445
*/
import java.util.HashMap;
import java.util.Scanner;

public class homework2 {
            
            static int ADR =5;
            static int LAB =0;
            static int SWITCH_LABEL=0;
            static int last_while;
            static int should_increment=0;
            static int global_offset=-1;
            
            static int current_la=0;
            static int current_lb=0;
            

                // Abstract Syntax Tree
                static final class AST {
                    public final String value;
                    public final AST left; // can be null
                    public final AST right; // can be null
                    

                    private AST(String val,AST left, AST right) {
                        value = val;
                        this.left = left;
                        this.right = right;
                    }

                    public static AST createAST(Scanner input) {
                        if (!input.hasNext())
                            return null;

                        String value = input.nextLine();
                        if (value.equals("~"))
                            return null;

                        return new AST(value, createAST(input), createAST(input));
                    }
                }

                static final class Variable{
                    // Think! what does a Variable contain?
                        
                        int addr;       //address of Variable
                        String type;    //Type of Variable (pointer/array/record)
                        int offset;     //Used for record
                        int size;       //Size of variable
                        int sizeOfSlot; //Size of array slot (used only for arrays)
                        int subPart;    //Subpart of array (used only for arrays)
                        int dims;       // Number of dimensions
                        int** dim;//2-d array of dimensions (used only for arrays
                        public Variable(String new_type,int new_addr,int offs) { //maybe without type
                        addr = new_addr;
                        type = new_type;
                        offset = offs;
                        }

                    public Variable(String new_type,int new_addr,int new_arraySize,int new_sizeOfSlot,int new_dims,int  new_subPart,int** new_dim) {
                        addr = new_addr;
                        type = new_type;
                        size=new_arraySize;
                        sizeOfSlot=new_sizeOfSlot;
                        dims=new_dims;
                        subPart=new_subPart;
                        dim=new_dim;
                    }

                                                                       
                }

    static final class SymbolTable {
        // Think! what does a SymbolTable contain?
        public static HashMap<String, Variable> hashtable;

        public int getArrayDimension(AST tree) {    // Calcualtes number of Dimensions
            if (tree == null)
                return 0;
            else if (tree.value.equals == "range")
                return 1;
            else
                return getArrayDimension(tree.left) + getArrayDimension(tree.right);
        }

        public void fillArrayDimension(AST tree, int index,int** arr)   // Calculate the range of each Dimension of array
        {
            if(tree==null)
                return;
            if(tree=="range") {
                arr[1][index] = Integer.parseInt(tree.left.left.value); // should convert to int (not sure of function)
                arr[2][index]=Integer.parseInt(tree.right.left.value);  // should conver to int (not sure of function)
                index++;
            }
            fillArrayDimension(tree.left,index,arr);
            fillArrayDimension(tree.right,index,arr);
        }
        public SymbolTable(HashMap<String,Variable> hash) {
            hashtable = hash;
        }
        public SymbolTable() {
            hashtable = new HashMap<String,Variable>();
        }



        public static  SymbolTable generateSymbolTable(AST tree){
            if(tree == null) {
                return null;
            }
            if(tree.value.equals( "program")) {
                if(tree.right!= null)
                {
                    generateSymbolTable(tree.right);
                    return null;
                }
                else
                    return null;
            }
            if(tree.value.equals( "content")) {
                if(tree.left!= null)
                {
                    generateSymbolTable(tree.left);
                    return null;
                }
            }
            if(tree.value.equals("scope")) {
                if(tree.left!= null)
                {
                    generateSymbolTable(tree.left);
                    return null;
                }
            }
            if(tree.value .equals( "declarationsList")) {
                if(tree.left!=null) {
                    generateSymbolTable(tree.left);
                }
                if(tree.right!=null)
                {
                    generateSymbolTable(tree.right);
                }
            }
            if(tree.value .equals( "var"))
            {
                if(should_increment == 1)
                {
                    global_offset++;
                }
                if(tree.right.value.equals("pointer"))
                {
                    if(tree.left.value.equals("identifier"))
                    {Variable var = new Variable(tree.right.value,ADR,global_offset); //making a new variable instance, tree.right holds the variable's type
                        hashtable.put(tree.left.left.value,var); //tree.left.left has the variable name
                        ADR++; }
                    else
                    {
                        Variable var = new Variable(tree.right.left.value,ADR,global_offset); //making a new variable instance, tree.right holds the variable's type
                        hashtable.put(tree.left.left.value,var); //tree.left.left has the variable name
                        ADR++;
                    }
                }
//TO-DO:: (maybe) take care of gloab addr and bla bla bla
                else if(tree.right.value.equals("record"))
                {
                    Variable var = new Variable(tree.right.value,ADR,global_offset); //making a new variable instance, tree.right holds the variable's type
                    hashtable.put(tree.left.left.value,var); //tree.left.left has the variable name
                    should_increment=1;
                    int temp2=global_offset;
                    global_offset=-1;
                    if(tree.right.left!=null)
                    {
                        generateSymbolTable(tree.right.left);
                    }
                    should_increment=0;
                    global_offset=temp2;
                }
                else if(tree.right.value.equals("array"))
                {
                    int i,j,sum=1,arraysize=1,subPart=0,sizeOfSlot=1;
                    int dims=getArrayDimension(tree);   // get array Dimensions
                    int[][] dim=new int[2][dims];
                    fillArrayDimension(tree,0,dim);// get array Dimensions range
                    string arrayType=tree.right.right.value;// Calculate array slot size
                    if(arrayType=="identifier")
                    {
                        arrayType = tree.right.right.right.value;
                        sizeOfSlot=SymbolTable.hashtable.get(arrayType).size;
                    }

                    for(i=0;i<dims;i++) {       // Calculate SubPart
                        sum=1;
                        for (j = i + 1; j < dims; j++)
                            sum=sum*(dim[2][j]-dim[1][j]+1);
                        subPart=subPart+sum*sizeOfSlot;
                    }

                    for (i=0; i < dims; i++)        // Calcualte array size
                        arraysize=arraysize*(dim[2][j]-dim[1][j]+1);



                    Variable var = new Variable(tree.right.value,ADR,arraysize,sizeOfSlot,dims,subPart,dim);
                    hashtable.put(tree.left.left.value,var);
                    ADR=ADR+arraysize;




                }
                else
                {
                    Variable var = new Variable(tree.right.value,ADR,global_offset); //making a new variable instance, tree.right holds the variable's type
                    hashtable.put(tree.left.left.value,var); //tree.left.left has the variable name
                    ADR++;
                }


            }
            return null;
        }
    }

                private static void generatePCode(AST ast, SymbolTable symbolTable) {
                        
                                   if(ast == null)
                                               return;
                        ast = ast.right; //now ast begins with the "content" node
                        code(ast,symbolTable);
                        
                        
                        
                }

                private static void coder(AST ast,SymbolTable symbols)
                {        
                        if(ast.value .equals( "plus"))
                        {                                  
                                     coder(ast.left,symbols);
                                     coder(ast.right,symbols);
                                     System.out.printf("add\n");
                        }
                                     
                        if(ast.value .equals( "multiply"))
                        {
                                     coder(ast.left,symbols);
                                     coder(ast.right,symbols);
                                     System.out.printf("mul\n");
                        }
                        
                        if(ast.value.equals( "divide"))
                        {
                                     coder(ast.left,symbols);
                                     coder(ast.right,symbols);
                                     System.out.printf("div\n");
                        }
                        
                        if(ast.value.equals( "negative") && ast.right == null ) // or ast.right.vaule=="-1"
                        {
                                     coder(ast.left,symbols);
                                     System.out.printf("neg\n");
                        }
                        
                        if(ast.value.equals( "minus"))
                        {
                                   coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    System.out.printf("sub\n");
                        }
                        
                        if(ast.value.equals( "equals"))
                        {
                                    coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    System.out.printf("equ\n");
                        }
                        
                        if(ast.value.equals( "notEquals"))
                        {
                                    coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                   System.out.printf("neq\n");
                        }
                        
                        if(ast.value.equals( "and"))
                        {
                                    coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    System.out.printf("and\n");
                        }
                        
                        if(ast.value.equals( "or"))
                        {
                                   coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    System.out.printf("or\n");
                        }
                        
                        if(ast.value .equals( "false")) {
                                                           System.out.print("ldc 0\n");
                        }
                        
                        if(ast.value .equals( "true")) {
                                                           System.out.print("ldc 1\n");
                        }
                        
                        if(ast.value .equals( "lessThan")) {
                                                    coder(ast.left,symbols);
                                                   coder(ast.right,symbols);
                                                    System.out.printf("les\n");
                        }
                        
                        if(ast.value .equals( "lessOrEquals")) {
                            coder(ast.left,symbols);
                            coder(ast.right,symbols);
                            System.out.printf("leq\n");
                        }
                       
                        if(ast.value .equals( "greaterOrEquals")) {
                            coder(ast.left,symbols);
                            coder(ast.right,symbols);
                            System.out.printf("geq\n");
                        }
                        
                        if(ast.value .equals( "greaterThan")) {
                            coder(ast.left,symbols);
                            coder(ast.right,symbols);
                            System.out.printf("grt\n");
                        }
                             
                        if(ast.value .equals( "constReal" ))
                         {
                                    System.out.printf("ldc %f\n" , Float.valueOf(ast.left.value)); // "value" is a string but we want to print out a number, possible bug
                         }
                        
                        if(ast.value .equals( "constBool")) {
                                   if(ast.left.value .equals( "true"))
                                               System.out.printf("ldc 1\n"); //if true ldc 1 else ldc 0
                                   if(ast.left.value .equals( "false"))
                                               System.out.printf("ldc 0\n");
                        }
                   
                        
                        if(ast.value .equals( "not" ))
                        {
                                                  coder(ast.left,symbols);
                                   System.out.printf("not\n" , ast.left.value); 
                        }
                        
                        if(ast.value .equals( "constInt")) {
                            System.out.printf("ldc %d\n" , (Integer.parseInt(ast.left.value))); // "value" is a string but we want to print out a number
                        }
                        
                        if(ast.value .equals( "identifier"))
                        { 
                                    codel(ast,symbols);
                                    System.out.printf("ind\n");
                        }
                        
                        if(ast.value.equals("record"))
                        {
                                   codel(ast,symbols);
                            System.out.printf("ind\n");


                        }
                    if(ast.value.equals("array"))
                    {
                        codel(ast,symbols);
                        System.out.printf("ind\n");


                    }
                        
                }

                private static void codel(AST ast,SymbolTable symbols)
                {
                        if(ast.value .equals( "identifier" ))
                         {                                 
                            System.out.printf("ldc %d\n",SymbolTable.hashtable.get(ast.left.value).addr); 
                         }
                        if(ast.value .equals( "pointer" ))
                        {                                  
                           if(ast.left.value.equals("identifier"))
                                   {
                                      System.out.printf("ldc %d\n",SymbolTable.hashtable.get(ast.left.left.value).addr); 
                                   }
                           else
                           {
                                      System.out.printf("ldc %d\n",SymbolTable.hashtable.get(ast.left.value).addr); 
   
                           }
                                   System.out.printf("ind\n");

                        }
                        if(ast.value.equals("record"))
                        {
                            
                                   if(ast.left.value.equals("record")) 
                                   {
                                               codel(ast.left,symbols);
                                System.out.printf("inc %d\n",(SymbolTable.hashtable.get(ast.right.left.value).offset));
     
                                   }
                                   else
                                   {

                                               codel(ast.left,symbols);                                
                                               System.out.printf("inc %d\n",(SymbolTable.hashtable.get(ast.right.left.value).offset));
                                   }
                        }
                        if(ast.value.equals("array"))
                        {
                                   coder(ast.left,symbols);
                                   indexToAddress(ast.right,symbols,SymbolTable.hashtable.get(ast.left.left.value),SymbolTable.hashtable.get(ast.left.left.value).dim);


            
                        }
                        
                }

                private static void indexToAddress(AST tree,SymbolTable symbols,Variable var,dim) {
                    if (tree == null)
                        return ;
                    if (tree.left !="IndexList") {
                        coder(ast.left, symbols);
                        int i = 0, sum = 1;
                        for (i = dim + 1; i < var.dims; i++)
                            sum = sum * (var.dim[2][i] - var.dim[1][i] + 1);
                        System.out.printf("ixa %d\n", sum);
                        dim++;

                    }
                    indexToAddress(tree.left,symbols,var,dim)
                    if (tree.right != null) {
                        coder(ast.left, symbols);
                        int i = 0, sum = 1;
                        for (i = dim + 1; i < var.dims; i++)
                            sum = sum * (var.dim[2][i] - var.dim[1][i] + 1);
                        System.out.printf("ixa %d\n", sum);
                        dim++;

                    }






                }
                private static void code(AST ast,SymbolTable symbols)
                {
                        
                                   
                                   if(ast == null) {
                                               return;
                                   }
                        if(ast.value .equals( "statementsList"))
                        { 

                                          if(ast.left!=null) {
                                                   code(ast.left,symbols);
                                          }
                                          if(ast.right!=null) {
                                                code(ast.right,symbols);
                                          }
                                    
                        }
                        
                        if(ast.value .equals( "assignment"))
                        {
                                    codel(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    System.out.printf("sto\n");
                        }               
                              
                        
                        if(ast.value .equals( "content"))
                        {
                                    if(ast.right != null)//or ast.right.value != "-1"
                                    {
                                               code(ast.right,symbols);
                                    }
                                    
                        }
                        
                        if(ast.value .equals( "print")) {
                                               coder(ast.left,symbols);
                                               System.out.printf("print\n");
                        }

                        if(ast.value .equals( "if") )
                        {
                                   if(ast.right != null)
                                   {
                                               if(ast.right.value.equals("else"))
                                               {
                                                           int la=LAB++; int lb=LAB++;
                                                           
                                                           coder(ast.left,symbols);
                                                           System.out.printf("fjp L%d\n",la);
                                                           code(ast.right,symbols);
                                                           
                                                           System.out.printf("L%d:\n",lb);
                                               }
                                               else
                                               {
                                                           int la=LAB++;
                                   coder(ast.left,symbols);
                                   System.out.printf("fjp L%d\n",la);
                                   code(ast.right,symbols);
                                   System.out.printf("L%d:\n",la);
                                                           
                                               }
                                               
                                   }
                                   else
                                   {
                                               int la=LAB++;
                                   coder(ast.left,symbols);
                                   System.out.printf("fjp L%d\n",la);
                                   System.out.printf("L%d:\n",la);   
                                   }
                        }
                        if(ast.value .equals( "else") )
                        {
                                   int la=LAB-2; int lb=LAB-1;
                                   code(ast.left,symbols);
                                   System.out.printf("ujp L%d\n",lb);
                                   System.out.printf("L%d:\n",la);
                                   code(ast.right,symbols);
                        }
                        if(ast.value .equals( "while") )
                        {
                                   int la=LAB++; int lb=LAB++; last_while=lb;
                                   System.out.printf("L%d:\n",la);
                                   coder(ast.left,symbols);
                                   System.out.printf("fjp L%d\n",lb);
                                   code(ast.right,symbols);
                                   System.out.printf("ujp L%d\n",la);
                                   System.out.printf("L%d:\n",lb);
                        }
                        if(ast.value.equals("switch"))
                        {
                                   int la = SWITCH_LABEL++;
                                   coder(ast.left,symbols);
                            System.out.printf("neg \n");
                            System.out.printf("ixj switch_end_%d:\n",la);
                            codec(ast.right,la,symbols);
                            print_labels(ast.right,la);
                            
                            System.out.printf("switch_end_%d:\n",la);

                            

                        }
                        
                        if(ast.value.equals("break"))
                        {
                                   System.out.printf("ujp L%d \n",last_while);
                        }
                        
                       return;
                }
                
                private static void codec(AST ast,int label,SymbolTable symbols)
                {
                        
                        
                        if(ast.value.equals("caseList"))
                        {
                                   if(ast.left != null)
                         {
                             codec(ast.left,label,symbols);
                                   
                         }

                        System.out.printf("case_%d_%d:\n",label,Integer.parseInt(ast.right.left.left.value));
                        code(ast.right.right,symbols);
                        System.out.printf("ujp switch_end_%d:\n",label);
  

                        }
                }
                
                private static void print_labels(AST ast,int label)
                {
                        System.out.printf("ujp case_%d_%d:\n",label,Integer.parseInt(ast.right.left.left.value));
                        if(ast.left != null)
                    {
                        
                                   print_labels(ast.left,label);
                        
                    }
                }

                private static void coded(AST ast,SymbolTable symbols)
                {
                        
                        System.out.printf("under const... ?");
                        
                }
               
                public static void main(String[] args) {
                    Scanner scanner = new Scanner(System.in);
                        
                    AST ast = AST.createAST(scanner);
                    SymbolTable symbolTable = new SymbolTable();
                    SymbolTable.generateSymbolTable(ast);                        
                    generatePCode(ast, symbolTable);
                        
                   
                }
}
            

            

            
