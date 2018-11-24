/*
 * Homework 2 in course: Compilers
 * Submitted By:
 * Waseem Weshahi	206943391
 * Bayan Farhan 	208300145	
 * Omar Khateeb		211706445
 */
import java.util.HashMap;
import java.util.Scanner;

public class homework2 {
	
            static int ADR =5;
            static int LAB =0;
            static int SWITCH_LABEL=0;
            static int last_while;
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
                        
                        int addr;
                        String type;
                        int is_pointer;//maybe we do not need it
                        
                        public Variable(String new_type,int new_addr,int pointer) { //maybe without type
                        addr = new_addr;
                        type = new_type;
                        is_pointer = pointer;
                        }

                                                                       
                }

                static final class SymbolTable{
                    // Think! what does a SymbolTable contain?
                        public static HashMap<String,Variable> hashtable;
                        
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
                        
                        if(tree.value .equals( "var")) {
                                   
                        		   if(tree.right.value.equals("pointer"))
                        		   {
                        			  if(tree.right.left.value.equals("identifier"))
                        			   {Variable var = new Variable(tree.right.left.left.value,ADR,1); //making a new variable instance, tree.right holds the variable's type
                                       hashtable.put(tree.left.left.value,var); //tree.left.left has the variable name
                                       ADR++; }
                        			  else
                        			  {
                        				  Variable var = new Variable(tree.right.left.value,ADR,1); //making a new variable instance, tree.right holds the variable's type
                                          hashtable.put(tree.left.left.value,var); //tree.left.left has the variable name
                                          ADR++;
                        				  
                        			  }
                        		   }
                        		   else
                        		   {
                        			   
                        			   Variable var = new Variable(tree.right.value,ADR,0); //making a new variable instance, tree.right holds the variable's type
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
            

            

            