/*
* Homework 2 in course: Compilers
* Submitted By:
* Waseem Weshahi          206943391
* Bayan Farhan       	   208300145    
* Omar Khateeb            211706445
*/
import java.util.HashMap;
import java.util.Scanner;
import java.util.*; 


public class homework3 {
            
            static int ADR =5;
            static int LAB =0;
            static int SWITCH_LABEL=0;
            static int last_while;
            static int should_increment=0;
            static int global_offset=-1;
            static int SSP=5;
            static String lastProg="";
            static int FunctionNum=0;
            static int sizeOfParameters=0;
            static boolean allowedToEnter=false;
            static boolean computing=false;
            static int SEP = 0;
            static int MAX_SEP =0;
            static int parameterList=0;
            static boolean calling=false;
            
            
            	
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
                        int[][] dim;//2-d array of dimensions (used only for arrays
                        String points2;
                        int funcSequence;
                        int SEP;
                        int SSP;
                        boolean byRef;
                    
                    public Variable(){}
                    public Variable(String new_type,int new_addr,int offs,String new_pointsTo,int new_size,int num) { //maybe without type
                        addr = new_addr;
                        type = new_type;
                        offset = offs;
                        points2= new_pointsTo;
                        size=new_size;
                        funcSequence = num;
                        this.SEP =-1;
                        this.SSP=-1;
                        byRef=false;

                        }


                    public Variable(String new_type,int new_addr,int new_arraySize,int new_sizeOfSlot,int new_dims,int  new_subPart,int[][] new_dim,String   new_pointsTo,int num) {
                        addr = new_addr;
                        type = new_type;
                        size=new_arraySize;
                        sizeOfSlot=new_sizeOfSlot;
                        dims=new_dims;
                        subPart=new_subPart;
                        dim=new_dim;
                        points2= new_pointsTo;
                        funcSequence = num;
                        this.SEP=-1;
                        this.SSP=-1;
                        byRef=false;


                    }

                                                                       
                }

    static final class SymbolTable {
        // Think! what does a SymbolTable contain?
        public static HashMap<String, List<Variable>> hashtable2;


        public static int getArrayDimension(AST tree) {    // Calcualtes number of Dimensions
            if (tree == null)
                return 0;
            else if (tree.value.equals("range"))
                return 1;
            else
                return getArrayDimension(tree.left) + getArrayDimension(tree.right);
        }

        public static void fillArrayDimension(AST tree, int[] index,int[][] arr)   // Calculate the range of each Dimension of array
        {

            if(tree == null)
                return ;
            if(tree.value.equals("range")) {
                arr[0][index[0]] = Integer.parseInt(tree.left.left.value); // should convert to int (not sure of function)
                arr[1][index[0]]=Integer.parseInt(tree.right.left.value);  // should conver to int (not sure of function)
                index[0]++;
            }
            fillArrayDimension(tree.left,index,arr);
            fillArrayDimension(tree.right,index,arr);

        }
        public SymbolTable(HashMap<String,List<Variable>> hash) {
            hashtable2 = hash;
        }
        public SymbolTable() {
            hashtable2 = new HashMap<String,List<Variable>>();

        }



        public static  SymbolTable generateSymbolTable(AST tree){
            if(tree == null) {
                return null;
            }
            if(tree.value.equals( "program")) {
                if(tree.left.left.left!=null)
                {
                	System.out.printf("%s:\n",tree.left.left.left.value.toUpperCase());
                	lastProg = tree.left.left.left.value.toUpperCase();
                	Variable var = new Variable();
                	var.funcSequence=FunctionNum;
                	List<Variable> list = new ArrayList<>();
            		list.add(var);
                	hashtable2.put(lastProg,list);
                	
                	
   

                }
            	
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
                }
                if(tree.right!= null)
                {
                	

                	generateSymbolTable(tree.right);
                }
                return null;

            }
            if(tree.value.equals("scope")) {
            	 int temp = SSP;
             	

            	if(tree.left!= null)
                {
                   
                    SSP=5;
                	generateSymbolTable(tree.left);
                	
                	
                    
                }
            	if(!computing)
            	{
            		System.out.printf("SSP %d \n",ADR);
            	}
            	
            	
            	String type = "void";
            	Variable var = new Variable(type,0,global_offset,"1   ",SSP,FunctionNum);
            	
            	
            	hashtable2.get(lastProg.toUpperCase()).get(0).type=type;
            	hashtable2.get(lastProg.toUpperCase()).get(0).SSP=ADR;
                hashtable2.get(lastProg.toUpperCase()).get(0).addr=var.addr;
                //hashtable2.get(lastProg.toUpperCase()).get(0).size=0;



            	FunctionNum++;
            	SSP=temp;
                return null;
            }
            if(tree.value .equals( "declarationsList")) {
                
            	if(tree.left!=null) {
                    generateSymbolTable(tree.left);
                    
                }
                if(tree.right!=null)
                {
                    generateSymbolTable(tree.right);
                    
                    Variable var = hashtable2.get(lastProg.toUpperCase()).get(0);
                    if(hashtable2.containsKey(tree.right.left.left.value)) {

                    for(int i=0;i<hashtable2.get(tree.right.left.left.value).size();i++)
                    {
                    	if (hashtable2.get(tree.right.left.left.value).get(i).funcSequence==hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence) {
                    		 var =hashtable2.get(tree.right.left.left.value).get(i);
                    	 }	
                    }
                    
                    //SSP+=var.size;
                    }
                }
            }
            
            if(tree.value.equals( "procedure" )|| tree.value.equals("function")) {
                
            	
            	
            	if(tree.left!=null)
                {
                    generateSymbolTable(tree.left);
                    generateSymbolTable(tree.right);
                }
            	
            	
            }
            if(tree.value.equals("identifierAndParameters"))
            {
            	String temp = lastProg;
            	if(!computing)
            	{
            		System.out.printf("%s:\n",tree.left.left.value.toUpperCase());
            	}
            	lastProg = tree.left.left.value.toUpperCase();
            	Variable var = new Variable();
            	var.funcSequence=FunctionNum;
            	//var.size=ADR-4;
            	List<Variable> list = new ArrayList<>();
        		list.add(var);
            	hashtable2.put(lastProg,list);
            	
            	generateSymbolTable(tree.right);
            	hashtable2.get(tree.left.left.value.toUpperCase()).get(0).SSP=ADR;
            	hashtable2.get(tree.left.left.value.toUpperCase()).get(0).SEP=-1;

            	lastProg = temp;
            }
            if(tree.value.equals("inOutParameters"))
            {
            	
            	
            	generateSymbolTable(tree.left);
            	String type;
            	if(tree.right!=null)
            		type =tree.right.value;
            	else
            		type = "void";
            	
            	Variable var = new Variable(type,0,global_offset,"1   ",SSP,FunctionNum); //making a new variable instance, tree.right holds the variable's type
                
                
                hashtable2.get(lastProg.toUpperCase()).get(0).type=var.type;
                hashtable2.get(lastProg.toUpperCase()).get(0).SSP=ADR;
                hashtable2.get(lastProg.toUpperCase()).get(0).size=ADR-5;
                
                hashtable2.get(lastProg.toUpperCase()).get(0).addr=var.addr;
                
            	
            	
            }
            if(tree.value.equals("parametersList"))
            {
            	if(tree.left!=null)
                {
                    generateSymbolTable(tree.left);
                }

            	if(tree.right!=null)
            	{
            		generateSymbolTable(tree.right);
            		
            	}
            	
            }
            
            
            
            if(tree.value .equals( "var") || allowedToEnter)
            {
                if(should_increment == 1)
                {
                    global_offset++;
                }
                if(tree.right.value.equals("pointer"))
                {
                    String pointsTo = "1";
                    if(tree.right.left.value.equals("identifier"))
                    {
                    	Variable var = new Variable(tree.right.value,ADR,global_offset,tree.right.left.left.value,1,FunctionNum); //making a new variable instance, tree.right holds the variable's type
                        if(!hashtable2.containsKey(tree.left.left.value))
                    	{
                        	List<Variable> list = new ArrayList<>();
                    		list.add(var);
                    		hashtable2.put(tree.left.left.value, list);
                		}
                        else {
                        	hashtable2.get(tree.left.left.value).add(var);
                        }
                        ADR++;
                        if(tree.value.equals("byReference"))
                        {
                        	var.byRef=true;
                        }
                        
                    }
                    else
                    {
                        Variable var = new Variable(tree.right.left.value,ADR,global_offset,tree.right.left.value,1,FunctionNum); //making a new variable instance, tree.right holds the variable's type

                        if(!hashtable2.containsKey(tree.left.left.value))
                    	{
                    		List<Variable> list = new ArrayList<>();
                    		list.add(var);
                    		hashtable2.put(tree.left.left.value, list);
                		}
                        else {
                        	hashtable2.get(tree.left.left.value).add(var);
                        }
                        ADR++;
                        if(tree.value.equals("byReference"))
                        {
                        	var.byRef=true;
                        }
                    }
                    SSP+=1;
                }

//TO-DO:: (maybe) take care of gloab addr and bla bla bla
                else if(tree.right.value.equals("record"))
                {
                	Variable var = new Variable(tree.right.value,ADR,global_offset,"1",1,FunctionNum); //making a new variable instance, tree.right holds the variable's type
                	if(tree.value.equals("byReference"))
                    {
                    	var.byRef=true;
                    }
                    should_increment=1;
                    int temp2=global_offset;
                    global_offset=-1;
                    if(tree.right.left!=null)
                    {
                        generateSymbolTable(tree.right.left);
                    }
                    should_increment=0;
                    global_offset=temp2;
                   var.size=update_size(tree.right.left);
                   SSP+=var.size;
                    if(!hashtable2.containsKey(tree.left.left.value))
                	{
                		List<Variable> list = new ArrayList<>();
                		list.add(var);
                		hashtable2.put(tree.left.left.value, list);
            		}
                    else {
                    	hashtable2.get(tree.left.left.value).add(var);
                    }
                }
                else if(tree.right.value.equals("array"))
                {
                    int i,j,sum=1,arraysize=1,subPart=0,sizeOfSlot=1;
                    int dims=getArrayDimension(tree);   // get array Dimensions
                    int[][] dim=new int[2][dims];
                    int[] index = new int[1];
                    fillArrayDimension(tree,index,dim);// get array Dimensions range

                  //  string arrayType=tree.right.right.value;// Calculate array slot size
                    if(tree.right.right.value.equals("identifier"))
                    {
                       // arrayType = tree.right.right.right.value;
                        for(int ind=0;ind<hashtable2.get(tree.right.right.left.value).size();ind++)
                        {
                        	 if (hashtable2.get(tree.right.right.left.value).get(ind).funcSequence==hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence) {
                        		 sizeOfSlot =hashtable2.get(tree.right.right.left.value).get(ind).size;
                        	 }	
                        }
                        
                    }

                    for(i=0;i<dims;i++) {// Calculate SubPart
                        sum=1;
                        for (j = i + 1; j < dims; j++)
                            sum=sum*(dim[1][j]-dim[0][j]+1);
                        subPart=subPart+sum*sizeOfSlot*dim[0][i];
                    }

                    for (i=0; i < dims; i++)        // Calcualte array size
                        arraysize=arraysize*(dim[1][i]-dim[0][i]+1);
                    arraysize=arraysize*sizeOfSlot;

                    String type="1";
                    if(tree.right.right.value.equals("identifier"))
                        type=tree.right.right.left.value;
                    Variable var = new Variable(tree.right.value,ADR,arraysize,sizeOfSlot,dims,subPart,dim,type,FunctionNum);
                    if(tree.value.equals("byReference"))
                    {
                    	var.byRef=true;
                    }
                    if(!hashtable2.containsKey(tree.left.left.value))
                	{
                		List<Variable> list = new ArrayList<>();
                		list.add(var);
                		hashtable2.put(tree.left.left.value, list);
            		}
                    else {
                    	hashtable2.get(tree.left.left.value).add(var);
                    }
                    ADR=ADR+arraysize;
                    SSP+=arraysize;




                }
                else
                {
                    Variable var = new Variable(tree.right.value,ADR,global_offset,"1   ",1,FunctionNum); //making a new variable instance, tree.right holds the variable's type
            		
                    if(tree.value.equals("byReference"))
                    {
                    	var.byRef=true;
                    }
                    if(!hashtable2.containsKey(tree.left.left.value))
                	{
                		

                    	List<Variable> list = new ArrayList<>();
                		list.add(var);
                		hashtable2.put(tree.left.left.value, list);
            		}
                    else {
                    	hashtable2.get(tree.left.left.value).add(var);
                    }
                    if(!var.byRef) {
                    
                    	ADR=ADR+size_of(tree.right);
                    }
                    else {
                    	ADR+=1;
                    }
                    SSP+=1;
                }


            }
            if((tree.value.equals("byValue")|| tree.value.equals("byReference"))&& !allowedToEnter )
            {
            	allowedToEnter=true;
            	
            	generateSymbolTable(tree);
            	allowedToEnter=false;
            	
            	for(int i=0;i<hashtable2.get(tree.left.left.value).size();i++)
                {
                	 if (hashtable2.get(tree.left.left.value).get(i).funcSequence==hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence) {
                		// SSP= SSP+hashtable2.get(tree.left.left.value).get(i).size;
                		 hashtable2.get(lastProg).get(0).size=hashtable2.get(lastProg).get(0).size+hashtable2.get(tree.left.left.value).get(i).size;
                	 }	
                }

            }
            return null;
        }
    }
    		private static int size_of(AST ast)
    		{
    			if(ast.value.equals("int" )|| ast.value.equals("bool")||ast.value.equals("real"))
    			{
    				return 1;
    			}
    			else if(ast.value.equals("identifier")) {

    				Variable var=new Variable();
    				int counter=0;
    				 return SymbolTable.hashtable2.get(ast.left.value).get(0).size;
    				
    			}
    			System.out.printf("you need to add my type!!! %s \n",ast.value);
    			return 0;
    		}

                private static void generatePCode(AST ast, SymbolTable symbolTable) {
                                   if(ast == null)
                                               return;
                        String curProg=lastProg;
                        ast = ast.right; //now ast begins with the "content" node
                        if(ast.left.right!=null)
                        {
                        	computing=true;
                        	int tempLAB=LAB;
                        	int tempSWITCH_LABEL=SWITCH_LABEL;
                        	int tempshould_increment=should_increment;
                        	int tempSSP=SSP;
                        	String templastProg = lastProg;
                        	int tempFunctionNum=FunctionNum;
                        	int tempsizeOfParameters=sizeOfParameters;
                        	int tempSEP=SEP;
                        	int tempMAX_SEP=MAX_SEP;
                        	SEP=0;
                        	MAX_SEP=0;
                        	makeFunctions(ast.left.right,symbolTable,ast,lastProg);

                			code(ast,symbolTable);	
                			
                			
                			System.out.printf("sep %d \n",MAX_SEP);
                			symbolTable.hashtable2.get(lastProg).get(0).SEP=MAX_SEP;
                        	
                        	computing = false;
                        	LAB =tempLAB;
                        	SWITCH_LABEL=tempSWITCH_LABEL;
                            should_increment=tempshould_increment;
                            SSP=tempSSP;
                            lastProg=templastProg;
                            FunctionNum=tempFunctionNum;
                            sizeOfParameters=tempsizeOfParameters;
                            
                            SEP = tempSEP;
                            MAX_SEP =tempMAX_SEP;
                            System.out.printf("ujp %s_begin \n",lastProg);
                        	makeFunctions(ast.left.right,symbolTable,ast,lastProg);

                        	

                        }
                        else
                        {
                        	computing = true;
                        	int tempLAB=LAB;
                        	int tempSWITCH_LABEL=SWITCH_LABEL;
                        	int tempshould_increment=should_increment;
                        	int tempSSP=SSP;
                        	String templastProg = lastProg;
                        	int tempFunctionNum=FunctionNum;
                        	int tempsizeOfParameters=sizeOfParameters;
                        	int tempSEP=SEP;
                        	int tempMAX_SEP=MAX_SEP;
                			code(ast,symbolTable);	
                			
                			System.out.printf("sep %d \n",MAX_SEP);
                			symbolTable.hashtable2.get(lastProg).get(0).SEP=MAX_SEP;
                        	System.out.printf("ujp %s_begin \n",lastProg);
                        	computing = false;
                        	LAB =tempLAB;
                        	SWITCH_LABEL=tempSWITCH_LABEL;
                            should_increment=tempshould_increment;
                            SSP=tempSSP;
                            lastProg=templastProg;
                            FunctionNum=tempFunctionNum;
                            sizeOfParameters=tempsizeOfParameters;
                            
                            SEP = tempSEP;
                            MAX_SEP =tempMAX_SEP;
                        }
                        lastProg=curProg;
                        System.out.printf("%s_begin:\n",lastProg);
                        code(ast,symbolTable);
                        System.out.printf("stp\n");
                        
                        
                        
                }
                private static void makeFunctions(AST ast,SymbolTable symbolTable,AST parent,String parentName)
                {
                	if(!computing && symbolTable.hashtable2.get(ast.right.left.left.left.value.toUpperCase()).get(0).SEP!=-1 )
                	{
                		System.out.printf("%s:\n", ast.right.left.left.left.value.toUpperCase());
                		System.out.printf("ssp %d\n",symbolTable.hashtable2.get(ast.right.left.left.left.value.toUpperCase()).get(0).SSP);
                		System.out.printf("sep %d\n",symbolTable.hashtable2.get(ast.right.left.left.left.value.toUpperCase()).get(0).SEP);
        				System.out.printf("ujp %s_begin\n",ast.right.left.left.left.value);
                	}
                	if(!symbolTable.hashtable2.containsKey(ast.right.left.left.left.value.toUpperCase())) {
                		
                		ADR=5;
                		String temp=lastProg;
                		lastProg=ast.right.left.left.left.value.toUpperCase();
                		SSP=5;
            			symbolTable.generateSymbolTable(ast.right);
            			lastProg=temp;
                	}
        			if(ast.right.right.left!=null)
                	{
                		if(ast.right.right.left.right!=null) {
        				makeFunctions(ast.right.right.left.right,symbolTable,parent,parentName);}
                	}
                	if(ast.right!=null)
                	{
                		String temp = lastProg;
                		lastProg = ast.right.left.left.left.value.toUpperCase();
                		/*if(!symbolTable.hashtable.containsKey(ast.right.left.left.left.value.toUpperCase())) {
                			ADR=5;
                			symbolTable.generateSymbolTable(ast.right);}*/
                		if(symbolTable.hashtable2.get(lastProg).get(0).SEP==-1)
                		{	
                			
                    		
                    		boolean tempComputing = computing;
                			computing = true;
                		
                		
                			//symbolTable.generateSymbolTable(ast.right);
                			int tempLAB=LAB;
                			int tempSWITCH_LABEL=SWITCH_LABEL;
                			int tempshould_increment=should_increment;
                			int tempSSP=SSP;
                			String templastProg = lastProg;
                			int tempsizeOfParameters=sizeOfParameters;
                			int tempSEP=SEP;
                			int tempMAX_SEP=MAX_SEP;
                			SEP=0;
                			MAX_SEP=0;
                		
                		
                			if(ast.right.right!=null&& ast.right.right.right!=null)
                			{
                				if(!computing)
                				{
                					System.out.printf("%s_begin:\n",ast.right.left.left.left.value);
                				}
                				code(ast.right.right.right,symbolTable);
                				if(ast.right.value.equals("procedure") && !computing) {
                					System.out.printf("retp\n");
                				}
                				if((ast.right.value.equals("function")) && !computing) {
                					System.out.printf("retf\n");
                    				}
                			}
                			symbolTable.hashtable2.get(lastProg).get(0).SEP=MAX_SEP;
                			FunctionNum++;
                			lastProg=temp;
                			computing = tempComputing;
                        	LAB =tempLAB;
                        	SWITCH_LABEL=tempSWITCH_LABEL;
                            should_increment=tempshould_increment;
                            SSP=tempSSP;
                            sizeOfParameters=tempsizeOfParameters;
                            
                            SEP = tempSEP;
                            MAX_SEP =tempMAX_SEP;
                		}
                		else
                		{
                			
                			if(ast.right.right!=null&& ast.right.right.right!=null)
                			{
                				if(!computing)
                				{
                					System.out.printf("%s_begin:\n",ast.right.left.left.left.value.toUpperCase());
                				}
                				code(ast.right.right.right,symbolTable);
                				if(ast.right.value.equals("procedure") && !computing) {
                					System.out.printf("retp\n");
                				}
                				if(ast.right.value.equals("function")&& !computing) {
                					System.out.printf("retf\n");
                    				}
                			}	
                		}
                	}

                }
                
                private static int update_size(AST ast)
                {
                	int size=0;
                	if(ast.value.equals("declarationsList"))
                	{
                		size+=update_size(ast.right);
                		if(ast.left!=null)
                			size+=update_size(ast.left);
                	}
                	else if(ast.value.equals("var"))
                	{
                		for(int i=0;i<SymbolTable.hashtable2.get(ast.left.left.value).size();i++)
                        {
                        	 if (SymbolTable.hashtable2.get(ast.left.left.value).get(i).funcSequence==SymbolTable.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence) {
                        		 return SymbolTable.hashtable2.get(ast.left.left.value).get(i).size;
                        	 }	
                        }
                		
                	}
                	return size;
                }

                private static void coder(AST ast,SymbolTable symbols)
                {        
                        if(ast.value .equals( "plus"))
                        {                                  
                                     coder(ast.left,symbols);
                                     coder(ast.right,symbols);
                                     if(!computing)
                                     {
                                    	 System.out.printf("add\n");
                                     }
                                     else
                                     {
                                     	SEP--;
                                     }
                        }
                                     
                        if(ast.value .equals( "multiply"))
                        {
                                     coder(ast.left,symbols);
                                     coder(ast.right,symbols);
                                     if(!computing)
                                     {
                                    	 System.out.printf("mul\n");
                                     }
                                     else
                                     {
                                     	SEP--;
                                     }
                        }
                        
                        if(ast.value.equals( "divide"))
                        {
                                     coder(ast.left,symbols);
                                     coder(ast.right,symbols);
                                     if(!computing)
                                     {
                                    	 System.out.printf("div\n");
                                     }
                                     else
                                     {
                                     	SEP--;
                                     }
                        }
                        
                        if(ast.value.equals( "negative") && ast.right == null ) // or ast.right.vaule=="-1"
                        {
                                     coder(ast.left,symbols);
                                     if(!computing)
                                     {
                                    	 System.out.printf("neg\n");
                                     }
                        }
                        
                        if(ast.value.equals( "minus"))
                        {
                                   coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    if(!computing)
                                    {
                                    	System.out.printf("sub\n");
                                    }
                                    else
                                    {
                                    	SEP--;
                                    }
                        }
                        
                        if(ast.value.equals( "equals"))
                        {
                                    coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    if(!computing)
                                    {
                                    	System.out.printf("equ\n");
                                    }
                                    else
                                    {
                                    	SEP--;
                                    }
                        }
                        
                        if(ast.value.equals( "notEquals"))
                        {
                                    coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    if(!computing)
                                   {
                                    	System.out.printf("neq\n");
                                   }
                                   else
                                   {
                                   	SEP--;
                                   }
                        }
                        
                        if(ast.value.equals( "and"))
                        {
                                    coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    if(!computing)
                                    {
                                    	System.out.printf("and\n");
                                    }
                                    else
                                    {
                                    	SEP--;
                                    }
                        }
                        
                        if(ast.value.equals( "or"))
                        {
                                   coder(ast.left,symbols);
                                    coder(ast.right,symbols);
                                    if(!computing)
                                    {
                                    	System.out.printf("or\n");
                                    }
                                    else
                                    {
                                    	SEP--;
                                    }
                        }
                        
                        if(ast.value .equals( "false")) {
                        	if(!computing)
                        	{
                        		System.out.print("ldc 0\n");
                        	}
                        	else {
                        		SEP++;
                        		if(SEP>MAX_SEP)
                        			MAX_SEP = SEP;
                        	}
                        }
                        
                        if(ast.value .equals( "true")) {
                        	if(!computing)
                        	{
                        		System.out.print("ldc 1\n");
                        	}
                        	else {
                        		SEP++;
                        		if(SEP>MAX_SEP)
                        			MAX_SEP = SEP;
                        	}
                        }
                        
                        if(ast.value .equals( "lessThan")) {
                                                    coder(ast.left,symbols);
                                                   coder(ast.right,symbols);
                                                   if(!computing)
                                                    {
                                                	   System.out.printf("les\n");
                                                    }
                                                    else
                                                    {
                                                    	SEP--;
                                                    }
                        }
                        
                        if(ast.value .equals( "lessOrEquals")) {
                            coder(ast.left,symbols);
                            coder(ast.right,symbols);
                            if(!computing)
                            {
                            	System.out.printf("leq\n");
                            }
                            else
                            {
                            	SEP--;
                            }
                        }
                       
                        if(ast.value .equals( "greaterOrEquals")) {
                            coder(ast.left,symbols);
                            coder(ast.right,symbols);
                            if(!computing)
                            {
                            	System.out.printf("geq\n");
                            }
                            else
                            {
                            	SEP--;
                            }
                        }
                        
                        if(ast.value .equals( "greaterThan")) {
                            coder(ast.left,symbols);
                            coder(ast.right,symbols);
                            if(!computing)
                            {
                            	System.out.printf("grt\n");
                            }
                            else
                            {
                            	SEP--;
                            }
                        }
                             
                        if(ast.value .equals( "constReal" ))
                         {
                        	if(!computing)
                        	{
                        		System.out.printf("ldc %f\n" , Float.valueOf(ast.left.value)); // "value" is a string but we want to print out a number, possible bug
                        	}
                        	else
                        	{
                        		SEP++;
                        		if(SEP>MAX_SEP)
                        			MAX_SEP = SEP;
                        	}
                         }
                        
                        if(ast.value .equals( "constBool")) {
                        	if(!computing) {
                                   if(ast.left.value .equals( "true"))
                                               System.out.printf("ldc 1\n"); //if true ldc 1 else ldc 0
                                   if(ast.left.value .equals( "false"))
                                               System.out.printf("ldc 0\n");
                        	}
                        	else
                        		SEP++;
                        	if(SEP>MAX_SEP)
                    			MAX_SEP = SEP;
                        }
                   
                        
                        if(ast.value .equals( "not" ))
                        {
                                   coder(ast.left,symbols);
                                   if(!computing)
                                   {
                                	   System.out.printf("not\n" , ast.left.value); 
                                   }
                        }
                        
                        if(ast.value .equals( "constInt")) {
                            if(!computing)
                        	{
                            	System.out.printf("ldc %d\n" , (Integer.parseInt(ast.left.value))); // "value" is a string but we want to print out a number
                        	}
                            else {
                            	SEP++;
                            	if(SEP>MAX_SEP)
                        			MAX_SEP = SEP;
                            }
                        }
                        
                        if(ast.value .equals( "identifier"))
                        { 
                        	codel(ast,symbols);
                            if(!computing)
                        	{
                            	System.out.printf("ind\n");
                        	}
                        }
                        
                        if(ast.value.equals("record"))
                        {
                            codel(ast,symbols);
                            if(!computing)
                            {
                            	System.out.printf("ind\n");
                            }


                        }
                    if(ast.value.equals("array"))
                    {
                        codel(ast,symbols);
                        if(!computing)
                        {
                        	System.out.printf("ind\n");
                        }


                    }
                    if(ast.value.equals("call"))
                    {


                    	int mstVal = Math.abs(symbols.hashtable2.get(ast.left.left.value.toUpperCase()).get(0).funcSequence-symbols.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence-1) ;
                    	if(!computing)
                    	{
                    		System.out.printf("mst %d\n",mstVal);
                    	}
                    	else
                    	{
                    		SEP++;
                    		SEP+=symbols.hashtable2.get(ast.left.left.value.toUpperCase()).get(0).SEP;
                    		if(SEP>MAX_SEP)
                    			MAX_SEP = SEP;
                    	}
                    	 sendArgs(ast.right,symbols);
                    	 int numOfArgs = symbols.hashtable2.get(ast.left.left.value.toUpperCase()).get(0).size;
                    	 if(!computing)
                    	 {
                    		 System.out.printf("cup %d %s\n",numOfArgs,ast.left.left.value.toUpperCase());
                    	 }
                    	 else {
                    		 SEP++;
                    		 if(SEP>MAX_SEP)
                     			MAX_SEP = SEP;
                    	 }
                    }
                        
                }

                private static void codel(AST ast,SymbolTable symbols)
                {
                        if(ast.value .equals( "identifier" ))
                         {                                 
                            if(!computing)
                        	{
                            	String first =ast.left.value;
                            	String second = ast.left.value;
                            	if(!symbols.hashtable2.containsKey(first))
                            	{
                            		first = first.toUpperCase();
                            	}
                            	if(!symbols.hashtable2.containsKey(second))
                            	{
                            		second = second.toUpperCase();
                            	}
                            	
                            	Variable firstvar=new Variable();
                            	Variable secondvar=new Variable();
                            	boolean found=false;
                            	int counter=0;
                            	
                            	while(!found) {
                            	for(int i=0;i<symbols.hashtable2.get(first).size();i++)
                                {
                                	 if ((symbols.hashtable2.get(first).get(i).funcSequence-counter)==symbols.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence) {
                                		 firstvar = symbols.hashtable2.get(first).get(i);
                                		 found=true;
                                	 }	
                                }
                            	counter++;
                            	}
                            	found=false;counter=0;
                            	while(!found) {
                            	for(int i=0;i<symbols.hashtable2.get(second).size();i++)
                                {
                                	 if (symbols.hashtable2.get(second).get(i).funcSequence-counter==symbols.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence) {
                                		 secondvar =symbols.hashtable2.get(second).get(i);
                                		 found = true;
                                	 }	
                                }
                            	counter++;}
                            	
                            	System.out.printf("lda %d %d\n",(firstvar.funcSequence-symbols.hashtable2.get(lastProg).get(0).funcSequence),secondvar.addr); 
                            	if(secondvar.byRef&&!calling)
                            	{
                            		System.out.printf("ind \n");	
                            	}
                        	}
                            else {
                            	SEP++;
                            	if(SEP>MAX_SEP)
                        			MAX_SEP = SEP;
                            }
                         }
                        if(ast.value .equals( "pointer" ))
                        {                                  
                           if(ast.left.value.equals("identifier"))
                                   {
                                      	if(!computing)
                        	   			{
                                      		Variable firstvar=new Variable();
                                        	Variable secondvar=new Variable();
                                        	boolean found=false;
                                        	int counter=0;
                                        	while(!found ) {
                                        	for(int i=0;i<symbols.hashtable2.get(ast.left.left.value).size();i++)
                                            {
                                            	 if ((symbols.hashtable2.get(ast.left.left.value).get(i).funcSequence - counter )==symbols.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence ) {
                                            		 firstvar =symbols.hashtable2.get(ast.left.left.value).get(i);
                                            		 found=true;
                                            	 }	
                                            }
                                        	counter++;
                                        	}
                                        	counter=0;found=false;
                                        	while(!found) {
                                        	for(int i=0;i<symbols.hashtable2.get(ast.left.left.value).size();i++)
                                            {
                                            	 if ((symbols.hashtable2.get(ast.left.left.value).get(i).funcSequence - counter)==symbols.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence) {
                                            		 secondvar =symbols.hashtable2.get(ast.left.left.value).get(i);
                                            		 found = true;
                                            	 }	
                                            }
                                        	counter++;
                                        	}
                                      		System.out.printf("lda %d %d\n",(firstvar.funcSequence-symbols.hashtable2.get(lastProg).get(0).funcSequence),secondvar.addr); 
                        	   		  	  	System.out.printf("ind\n");
                        	   		  	if(secondvar.byRef)
                                    	{
                                    		System.out.printf("ind \n");	
                                    	}
                        	   		  	  	
                        	   			}
                                      	else {
                                      		SEP++;
                                      		if(SEP>MAX_SEP)
                                    			MAX_SEP = SEP;
                                      	}

                                   }
                           else if(ast.left.value.equals("pointer"))
                           {
                               codel(ast.left,symbols);

                        	   if(!computing)
                        	   {
                        		   	System.out.printf("ind\n");
                        	   }
                           }
                           else
                           {
                                       // if(symbols.hashtable.contains(ast.left.value))
                               codel(ast.left,symbols);
                        	   if(!computing)
                               {
                        		   System.out.printf("ind\n");
                               }

                               // System.out.printf("ldc %d\n",SymbolTable.hashtable.get(ast.left.value).addr);
   
                           }
                                   

                        }
                        if(ast.value.equals("record"))
                        {
                            
                                   if(ast.left.value.equals("record")) 
                                   {
                                               codel(ast.left,symbols);
                                               if(!computing)
                                               {
                                            	   Variable firstvar=new Variable();
                                               		boolean found=false;
                                               		int counter=0;
                                               		while(!found ) {
                                               			for(int i=0;i<symbols.hashtable2.get(ast.right.left.value).size();i++)
                                               			{
                                               				if ((symbols.hashtable2.get(ast.right.left.value).get(i).funcSequence - counter )==SymbolTable.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence ) {
                                                   		    firstvar =SymbolTable.hashtable2.get(ast.right.left.value).get(i);
                                                   		    found=true;
                                               				}	
                                               			}
                                               			counter++;
                                               		}
                                            	   System.out.printf("inc %d\n",(firstvar.offset));
                                               }
     
                                   }
                                   else
                                   {

                                               codel(ast.left,symbols); 
                                              
                                               if(!computing)
                                               {
                                            	   Variable firstvar=new Variable();
                                              		boolean found=false;
                                              		int counter=0;
                                              		while(!found ) {
                                              			for(int i=0;i<SymbolTable.hashtable2.get(ast.right.left.value).size();i++)
                                              			{
                                              				if ((SymbolTable.hashtable2.get(ast.right.left.value).get(i).funcSequence - counter )==SymbolTable.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence ) {
                                                  		    firstvar =SymbolTable.hashtable2.get(ast.right.left.value).get(i);
                                                  		    found=true;
                                              				}	
                                              			}
                                              			counter++;
                                              		}
                                            	   System.out.printf("inc %d\n",(firstvar.offset));
                                               }
                                   }
                        }
                        if(ast.value.equals("array"))
                        {
                            Variable var = new Variable();
                                    int[] dim=new int[1];
                                   
                                   codel(ast.left,symbols);
                                   
                                   if(SymbolTable.hashtable2.containsKey(ast.left.left.value) ) {
                                 		boolean found=false;
                                 		int counter=0;
                                 		while(!found ) {
                                 			for(int i=0;i<SymbolTable.hashtable2.get(ast.left.left.value).size();i++)
                                 			{
                                 				if ((SymbolTable.hashtable2.get(ast.left.left.value).get(i).funcSequence - counter )==SymbolTable.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence ) {
                                     		    var =SymbolTable.hashtable2.get(ast.left.left.value).get(i);
                                     		    found=true;
                                 				}	
                                 			}
                                 			counter++;
                                 		}
                                   }
                                   else
                                       var=pointsTo(ast.left,symbols);


                                   indexToAddress(ast.right,symbols,var,dim);
                                   if(!computing)
                                   {
                                	   System.out.printf("dec %d\n",var.subPart );
                                   }



                        }
                        
                }

                public static void indexToAddress(AST tree,SymbolTable symbols,Variable var,int[] dim)
                {
                    if (tree == null)
                        return;


                    if (tree.left == null) {
                        coder(tree.right, symbols);
                        int i = 0, sum = 1;

                        for (i = dim[0]+1 ; i < var.dims; i++) {
                            sum = sum * (var.dim[1][i] - var.dim[0][i] + 1);

                        }
                        if(!computing)
                        {
                        	System.out.printf("ixa %d\n", sum*var.sizeOfSlot);
                        }
                        else {
                     	   SEP--;
                        }
                        dim[0]++;
                        return;
                    }


                    indexToAddress(tree.left,symbols,var,dim);
                    if (tree.right != null) {
                        coder(tree.right, symbols);
                        int i = 0, sum = 1;
                        for (i = dim[0]+1 ; i < var.dims; i++) {
                            sum = sum * (var.dim[1][i] - var.dim[0][i] + 1);
                            //System.out.printf("when dim is:%d , sum is: %d\n",dim, sum);
                        }
                       if(!computing)
                        {
                    	   System.out.printf("ixa %d\n", sum*var.sizeOfSlot);
                        }
                       else {
                    	   SEP--;
                       }
                        dim[0]++;

                    }
                }

                public static Variable pointsTo(AST tree,SymbolTable symbols) {



                    if (tree.value.equals("identifier")){
                    	Variable firstvar=new Variable();
                    	Variable secondvar=new Variable();
                    	boolean found=false;
                    	int counter=0;
                    	while(!found ) {
                    	for(int i=0;i<SymbolTable.hashtable2.get(tree.left.value).size();i++)
                        {
                        	 if ((SymbolTable.hashtable2.get(tree.left.value).get(i).funcSequence - counter )==SymbolTable.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence ) {
                        		 firstvar =SymbolTable.hashtable2.get(tree.left.value).get(i);
                        		 found=true;
                        	 }	
                        }
                    	counter++;
                    	}
                    	counter=0;found=false;
                    	while(!found) {
                    	for(int i=0;i<SymbolTable.hashtable2.get(firstvar.points2).size();i++)
                        {
                        	 if ((SymbolTable.hashtable2.get(firstvar.points2).get(i).funcSequence - counter)==SymbolTable.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence) {
                        		 secondvar =SymbolTable.hashtable2.get(firstvar.points2).get(i);
                        		 found = true;
                        	 }	
                        }
                    	counter++;
                    	}
                        return (secondvar);}
                    if(tree.value.equals("record")) {
                        if(tree.right.value.equals("identifier"))
                        {
                        	Variable firstvar=new Variable();
                        	boolean found=false;
                        	int counter=0;
                        	while(!found ) {
                        	for(int i=0;i<symbols.hashtable2.get(tree.right.left.value).size();i++)
                            {
                            	 if ((symbols.hashtable2.get(tree.right.left.value).get(i).funcSequence - counter )==symbols.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence ) {
                            		 firstvar =SymbolTable.hashtable2.get(tree.right.left.value).get(i);
                            		 found=true;
                            	 }	
                            }
                        	counter++;
                        	}
                        	return (firstvar);
                        }
                        else
                            return pointsTo(tree.right,symbols);
                    //    Variable var=pointsTo(tree.right,symbols);
                      //  return (var);
                    }
                    if(tree.value.equals("array")) {

                        return (pointsTo(tree.left, symbols));

                    }//asd
                   Variable var=pointsTo(tree.left,symbols);

                   if(var.points2=="1")
                       return var;
                
                Variable firstvar=new Variable();
               	boolean found=false;
               	int counter=0;
               	while(!found ) {
               	for(int i=0;i<symbols.hashtable2.get(var.points2).size();i++)
                   {
                   	 if ((symbols.hashtable2.get(var.points2).get(i).funcSequence - counter )==symbols.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence ) {
                   		 firstvar =symbols.hashtable2.get(var.points2).get(i);
                   		 found=true;
                   	 }	
                   }
               	counter++;
               	}

                   return firstvar;
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
                                    if(!computing) 
                                    {
                                    	System.out.printf("sto\n");
                                    }
                                    else
                                    {
                                    	SEP=SEP-2;
                                   	}
                                    
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
                                               if(!computing) 
                                               {
                                                   System.out.printf("print\n");
                                               }
                                               else
                                               	{
                                            	   SEP=SEP-1;
                                               	}
                        }

                        if(ast.value .equals( "if") )
                        {
                                   if(ast.right != null)
                                   {
                                               if(ast.right.value.equals("else"))
                                               {
                                                           int la=LAB++; int lb=LAB++;
                                                           
                                                           coder(ast.left,symbols);
                                                           if(!computing) 
                                                           {
                                                               System.out.printf("fjp L%d\n",la);
                                                           }
                                                           else
                                                           	{
                                                        	   SEP=SEP-1;
                                                           	}
                                                           code(ast.right,symbols);
                                                           if(!computing) 
                                                           {
                                                               System.out.printf("L%d:\n",lb);
                                                           }
                                               }
                                               else
                                               {
                                                           int la=LAB++;
                                                           coder(ast.left,symbols);
                                                           if(!computing) 
                                                           {
                                                               System.out.printf("fjp L%d\n",la);
                                                           }
                                                           else
                                                           	{
                                                        	   SEP=SEP-1;
                                                           	}
                                                           code(ast.right,symbols);
                                                           if(!computing) 
                                                           {
                                                               System.out.printf("L%d:\n",la);
                                                           }
                                                           
                                               }
                                               
                                   }
                                   else
                                   {
                                               int la=LAB++;
                                   coder(ast.left,symbols);
                                   if(!computing) 
                                   {
                                       System.out.printf("fjp L%d\n",la);
                                   }
                                   else
                                   	{
                                	   SEP=SEP-1;
                                   	}
                                   if(!computing) 
                                   {
                                       System.out.printf("L%d:\n",la);
                                   }
                                   }
                        }
                        if(ast.value .equals( "else") )
                        {
                                   int la=LAB-2; int lb=LAB-1;
                                   code(ast.left,symbols);
                                   if(!computing) 
                                   {
                                       System.out.printf("ujp L%d\n",lb);
                                       System.out.printf("L%d:\n",la);

                                   }
                                   code(ast.right,symbols);
                        }
                        if(ast.value .equals( "while") )
                        {
                                   int la=LAB++; int lb=LAB++; last_while=lb;
                                   if(!computing)
                                   {
                                	   System.out.printf("L%d:\n",la);
                                   }
                                   coder(ast.left,symbols);
                                   if(!computing)
                                   {
                                	   System.out.printf("fjp L%d\n",lb);
                                   }
                                   else {
                                	   SEP--;
                                   }
                                   code(ast.right,symbols);
                                   if(!computing)
                                   {
                                	   System.out.printf("ujp L%d\n",la);
                                       System.out.printf("L%d:\n",lb);
                                   }
                        }
                        if(ast.value.equals("switch"))
                        {
                                   int la = LAB++;
                                   coder(ast.left,symbols);
                            if(!computing)       
                            {
                            	System.out.printf("neg\n");
                            	System.out.printf("ixj switch_end_%d\n",la);
                            }
                            else {
                            	SEP--;
                            }
                            codec(ast.right,la,symbols);
                            if(!computing)
                            {
                            	print_labels(ast.right,la);
                                System.out.printf("switch_end_%d:\n",la);

                            }
                           
                        }
                        
                        if(ast.value.equals("break"))
                        {
                            if(!computing)      
                        	{
                            	System.out.printf("ujp L%d\n",last_while);
                        	}
                            
                        }

                        if(ast.value.equals("call"))
                        {
                        	
                        	int mstVal = Math.abs(symbols.hashtable2.get(ast.left.left.value.toUpperCase()).get(0).funcSequence-symbols.hashtable2.get(lastProg).get(0).funcSequence-1) ;
                        	if(!computing)
                        	{
                        		System.out.printf("mst %d\n",mstVal);
                        	}
                        	else {
                        		SEP++;
                        		SEP+=symbols.hashtable2.get(ast.left.left.value.toUpperCase()).get(0).SEP;
                        		if(SEP>MAX_SEP)
                        			MAX_SEP = SEP;
                        	}
                        	sendArgs(ast.right,symbols);
                        	int numOfArgs = symbols.hashtable2.get(ast.left.left.value.toUpperCase()).get(0).size;
                        	if(!computing)
                        	{
                        		System.out.printf("cup %d %s\n",numOfArgs,ast.left.left.value);
                        	}
                        	else
                        	{

                        		SEP++;
                        		if(SEP>MAX_SEP)
                        			MAX_SEP = SEP;
                        	}
                        }

                       return;
                }
                
                private static void sendArgs(AST ast,SymbolTable symbols)
                {
                	
                	if(ast.left!=null)
                		sendArgs(ast.left,symbols);
                	
                	//TO-DO: make sure of the following case
                	
                	if(ast.right.value.equals("identifier")) 
                	{
                		boolean temp=calling;
                		calling=true;
                		codel(ast.right,symbols);
                		calling = temp;
                		Variable firstvar=new Variable();
                   		boolean found=false;
                   		int counter=0;
                   		while(!found ) {
                   			for(int i=0;i<symbols.hashtable2.get(ast.right.left.value).size();i++)
                   			{
                   				if ((symbols.hashtable2.get(ast.right.left.value).get(i).funcSequence - counter )==symbols.hashtable2.get(lastProg.toUpperCase()).get(0).funcSequence ) {
                       		    firstvar =symbols.hashtable2.get(ast.right.left.value).get(i);
                       		    found=true;
                   				}	
                   			}
                   			counter++;
                   		}	
                	if((firstvar.type).equals("array")||firstvar.type.equals("record"))
                	{
                		if(!computing)
                		{
                			System.out.printf("movs %d \n",(firstvar.size));
                		}
                		else {
                			SEP--;
                			SEP = SEP + firstvar.size;
                		}
                	}
                	else {
                		if(!computing) {
                    		System.out.printf("ind \n");
                    		}
                	}
                	}
                	else
                	{
                		coder(ast.right,symbols);
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
                        if(!computing)
                        {
                        	System.out.printf("case_%d_%d:\n",label,Integer.parseInt(ast.right.left.left.value));
                        }
                        code(ast.right.right,symbols);
                        if(!computing)
                        {
                        	System.out.printf("ujp switch_end_%d\n",label);
                        }
  

                        }
                }
                
                private static void print_labels(AST ast,int label)
                {
                        System.out.printf("ujp case_%d_%d\n",label,Integer.parseInt(ast.right.left.left.value));
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