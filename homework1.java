
import java.util.HashMap;
import java.util.Scanner;

public class homework1 {

            /*
            * Hints in doing the HW:
            *   a) Make sure you first understand what you are doing.
            *   b) Watch Lecture 2 focusing on the code described
            */
                        
            static int ADR =5;
            static int LAB =0;
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
                        
                        public Variable(String new_type,int new_addr) { //maybe without type
                        addr = new_addr;
                        type = new_type;
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
                        // TODO: create SymbolTable from AST
                        // goes over the declaration sub tree
                        // put hashtable.put(ast.value,ADR);
                        // ADR++;
                        if(tree == null) {
                                   return null;
                        }
                        if(tree.value.equals( "program")) {
                                   tree = tree.right;
                                   if(tree!= null)
                                   {
                                               generateSymbolTable(tree);
                                               return null;
                                   }
                                   else
                                               return null;
                        }
                        
                        if(tree.value.equals( "content")) {
                        	
                                   if(tree.left!= null)
                                   {
                                	   tree = tree.left;
                                               generateSymbolTable(tree);
                                               return null;
                                   }
                                  
                        }
                        
                        if(tree.value.equals("scope")) {
                        	tree = tree.left;
                        	if(tree!= null)
                            {
                                        generateSymbolTable(tree);
                                        return null;
                            }
                        	
                        }
                        
                        if(tree.value .equals( "declarationsList")) {
                                   if(tree.left!=null) {
                                               generateSymbolTable(tree.left);
                                   }
                                   else if(tree.right!=null)
                                   {
                                   generateSymbolTable(tree.right);
                        
                                   }
                                   else
                                               return null;
                        }
                        
                        if(tree.value .equals( "var")) {
                                   Variable var = new Variable(tree.right.value,ADR); //making a new variable instance, tree.right holds the variable's type
                                   hashtable.put(tree.left.left.value,var); //tree.left.left has the variable name
                                   ADR++;
                                   
                        }
                        return null;
                    
                }
                }

                private static void generatePCode(AST ast, SymbolTable symbolTable) {
                    // TODO: go over AST and print code
                        ast = ast.right; //now ast begins with the "content" node
                        code(ast,symbolTable);
                        
                        
                        
                }

                
                //Warning: there here we made use out of s.o.println,s.o.print and printf
                //so it might be a bit messy with getting to new line when printing
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
                                     System.out.printf("mull\n");
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
                                   System.out.printf("not \n" , ast.left.value); // "value" is a string but we want to print out a number, possible bug
                        }
                        
                        if(ast.value .equals( "constInt")) {
                            System.out.printf("ldc %d\n" , (Integer.parseInt(ast.left.value))); // "value" is a string but we want to print out a number, possible bug
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
                        //TODO: complete the if
                        if(ast.value .equals( "if") )
                        {
                        	if(ast.right.right != null)
                        	{
                        		if(ast.right.right.value.equals("else"))
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
                        	int la=LAB-1; int lb=LAB;
                                   code(ast.left,symbols);
                                   System.out.printf("ujp L%d\n",lb);
                                   System.out.printf("L%d:\n",la);
                                   code(ast.right,symbols);
                        }
                        if(ast.value .equals( "while") )
                        {
                                   int la=LAB++; int lb=LAB++;
                                   System.out.printf("L%d:\n",la);
                                   coder(ast.left,symbols);
                                   System.out.printf("fjp L%d\n",lb);
                                   code(ast.right,symbols);
                                   System.out.printf("ujp L%d \n",la);
                                   System.out.printf("L%d:\n",lb);
                        }
                                   
                       
                        
                                   

                        return;
                }

                private static void coded(AST ast,SymbolTable symbols)
                {
                        
                        System.out.printf("under const... ?");
                        
                }
               
                public static void main(String[] args) {
                    Scanner scanner = new Scanner(System.in);
                        
                    AST ast = AST.createAST(scanner);
                    if(ast != null)
                    {
                        SymbolTable symbolTable = new SymbolTable();
                         SymbolTable.generateSymbolTable(ast);
                        
                                    generatePCode(ast, symbolTable);
                        
                    }  
                   
                }
}
            

            

            