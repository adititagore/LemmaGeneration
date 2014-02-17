import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLProcessor {

	ArrayList<String> var_decld = new ArrayList<String>();
	ArrayList<String> fun_decld = new ArrayList<String>();

	
	int c =0, i =0;

	public void ProcessXmlsFromDirectory(String files, String DirName, BufferedWriter out)
	{		
		try
		{
			String wholepath = DirName + files;
			//System.out.println("The whole path is "+wholepath);

			File file = new File(wholepath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setNamespaceAware(true);
			dbf.setIgnoringComments(true);

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			doc = db.parse(file);
			doc.normalizeDocument();

			Node root = doc.getDocumentElement();
			// remove whitespace nodes
			root.normalize();
			removeWhitespace(root);

			Node lemma = root.getLastChild();		

			String lem = doc.getDocumentElement().getAttribute("lemma");			
			//out.newLine();
			//out.write(" " +lem +". ");
			
			//print for contra
		    Node seq = lemma.getFirstChild();
		    //System.out.println(seq.getNodeName().toString());
		    declare_global(seq, doc, out);
		//    declare_global1(seq, doc, out);
		  //  declare_global2(seq.getNextSibling(), doc, out);
		    
		    //temporary
		    declare_vars(doc, out);		    
			Node thelemma = lemma.getLastChild().getLastChild();
			traverseTree(thelemma ,doc , out);

		}
		catch (Exception e) 
		{
			e.printStackTrace();  
		}
	}//end ProcessXMLsFromDirectory

	public void declare_vars(Document doc, BufferedWriter out) throws IOException
	{

		NodeList vars = doc.getElementsByTagName("name");
		int numSymbols = vars.getLength();

		if(numSymbols > 0)
		{
		out.write("forall ");
		}
		for (int i = 0; i < numSymbols; i++) 
		{
			String name = vars.item(i).getFirstChild().getTextContent();
			
			out.write(name);
			if(i< numSymbols-1)
			out.write(", ");
		}
		
		if(numSymbols >0)
		{
		String type = vars.item(0).getParentNode().getAttributes().
				getNamedItem("type").getNodeValue();
		out.write (": "+type +" :: ");
		}
		
		

	}//end declare_vars(Document doc)	


	public static void removeWhitespace(Node n) {
		NodeList nl = n.getChildNodes();
		for (int pos = 0, c = nl.getLength(); pos < c; pos++) {
			Node child = nl.item(pos);
			if (child.getNodeType() != Node.TEXT_NODE) {
				removeWhitespace(child);
			}
		}

		// count backwards so that pos is correct even if nodes are removed
		for (int pos = nl.getLength() - 1; pos >= 0; pos--) {
			Node child = nl.item(pos);
			if (child.getNodeType() == Node.TEXT_NODE) {
				// if node's text is made up only of whitespace characters
				if (child.getTextContent().trim().equals("")) {
					n.removeChild(child);
				}
			}
		}
	}

	public void traverseTree(Node node, Document doc, BufferedWriter out) throws Exception
	{
		NodeList card = null;
		card = doc.getElementsByTagName("bar");
		String cName = doc.getDocumentElement().getAttribute("cName");
		NodeList dots = doc.getElementsByTagName("dot");
		int numDots = dots.getLength();
		//System.out.println("numDots"+numDots);

		// Extract node info:
		String elementName = node.getNodeName();
		String val = node.getNodeValue();

		if(elementName.equals("implies"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write("=>");
			traverseTree(node.getLastChild(), doc, out);
			out.write(")");
		}
		
		if(elementName.equals("neq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write("!=");
			traverseTree(node.getLastChild(), doc, out);
			out.write(")");
		}

		else if(elementName.equals("eq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write("==");
			traverseTree(node.getLastChild(), doc, out);
			out.write(")");

		}					 

		else if(elementName.equals("geq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write(">=");
			traverseTree(node.getLastChild(), doc, out);
			out.write(")");
		}

		else if(elementName.equals("leq"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write("<=");
			traverseTree(node.getLastChild(), doc, out);
			out.write(")");
		}

		else if(elementName.equals("gt"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write(">");
			traverseTree(node.getLastChild(), doc, out);
			out.write(")");
		}

		else if(elementName.equals("lt"))
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write("<");
			traverseTree(node.getLastChild(), doc, out);
			out.write(")");
		}

		else if(elementName.equals("add")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write("+");
			traverseTree(node.getLastChild(), doc, out);
			out.write(")");
		}

		else if(elementName.equals("subtract")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc, out);
			out.write("-");
			traverseTree(node.getLastChild(),doc , out);
			out.write(")");
		}

		else if(elementName.equals("star")) // for integers
		{	
			out.write("(");
			traverseTree(node.getFirstChild(),doc , out);
			if(cName.contains("Queue") || cName.contains("List") || cName.contains("Sequence") || cName.contains("Stack")|| cName.contains("InputStreamTemplate"))
			{
				out.write("+");
			}
			else
				out.write("*");
			traverseTree(node.getLastChild(),doc , out);
			out.write(")");
		}

		else if(elementName.equals("union"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write("+");
			traverseTree(node.getLastChild(),doc , out);
			out.write(") ");
		}

		else if(elementName.equals("intersection"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write("*");
			traverseTree(node.getLastChild(),doc , out);
			out.write(") ");
		}
		else if(elementName.equals("and"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write("&&");
			traverseTree(node.getLastChild(),doc , out);
			out.write(") ");
		}
		else if(elementName.equals("or"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write("||");
			traverseTree(node.getLastChild(),doc , out);
			out.write(") ");
		}

		else if(elementName.equals("bar"))
		{
		//	System.out.println(cName);
			String type = node.getFirstChild().getAttributes().getNamedItem("type").getNodeValue();;
		//	System.out.println(type);
			
	
			if(type.equals("string(object)") || type.equals("string(integer)") || type.equals("string(character)"))
			{
				out.write("(|");
				traverseTree(node.getFirstChild(),doc , out);
				out.write("|) ");
			}
			
			else if(type.equals("finiteset(object)") || type.equals("integer") || type.equals("finiteset(tuple(d:object,r:object))"))
			{
				out.write("(card (");
				traverseTree(node.getFirstChild(),doc , out);
				out.write(")) ");

			}
		}

		else if (elementName.equals("dot"))
		{
			String p1 = node.getFirstChild().getTextContent();
			String p2 =node.getLastChild().getTextContent();
			String dot_var = p1+"_"+p2;
			if (dot_var.contains("."))
			{
				int index = dot_var.indexOf(".");	                
				dot_var = dot_var.substring(0, index) + "_" + dot_var.substring(index + 1);
			}
			out.write(" " +dot_var + " ");
		}
		else if(elementName.equals("negate"))
		{
			out.write("(- ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write(") ");
		}
		else if(elementName.equals("difference"))
		{
			out.write("( ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write("-");
			traverseTree(node.getLastChild(),doc , out);
			out.write(") ");
		}

		else if(elementName.equals("singleton"))
		{
			out.write("({ ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write("}) ");
		}
		else if(elementName.equals("symbol") && !(node.getParentNode().getNodeName().toString().equals("dot")))
		{
			String symbol = node.getFirstChild().getTextContent();
			if (symbol.contains("."))
			{
				int index = symbol.indexOf(".");	                
				symbol = symbol.substring(0, index) + "_" + symbol.substring(index + 1);
				//System.out.println(symbol);
			}
			out.write(" " +symbol + " ");

		}

		else if(elementName.equals("constant"))
		{
			//op = read_text_set(node);
			String constant = node.getFirstChild().getTextContent();			
			out.write(" " +constant + " ");			
		}

		else if(elementName.equals("emptyset"))
		{
			out.write(" {}");			
		}
		else if(elementName.equals("emptystring"))
		{
			out.write(" []");
		}
		else if(elementName.equals("stringleton"))
		{
			out.write("([ ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write("]) ");
		}
		else if(elementName.equals("zero"))
		{
			out.write(" 0");
		}

		else if(elementName.equals("is_initial"))
		{

			out.write(" (is_initial (");
			traverseTree(node.getFirstChild(),doc , out);
			out.write(")) ");
		}
		else if(elementName.equals("elements"))
		{

			out.write(" (elements (");
			traverseTree(node.getFirstChild(),doc , out);
			out.write(")) ");
		}

		else if(elementName.equals("element"))
		{
			out.write(" ( ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write("in");
			traverseTree(node.getLastChild(),doc , out);

			out.write(") ");
		}

		else if(elementName.equals("not"))
		{

			out.write("( ! ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write(")  ");
		}

		else if(elementName.equals("thereexists") || elementName.equals("exists"))
		{
			out.write("exists ");
			traverseTree(node.getFirstChild(),doc , out);
			traverseTree(node.getLastChild(),doc , out);
		}
		else if(elementName.equals("vars"))
		{
			String type = node.getAttributes().getNamedItem("type").getNodeValue();
			String dfny_type= "";

			//check whether the symbol has been declared or not

			if(type.equals("finiteset(object)"))
			{

				dfny_type = "set<T>";
			}

			if(type.equals("string(object)") || type.equals("string of item") || type.equals("string"))
			{

				dfny_type = "seq<T>";
			}

			if(type.equals("string(integer)") )
			{

				dfny_type = "seq<int>";
			}

			else if (type.equals("object"))
			{
				dfny_type = "T";
			}

			else if (type.equals("integer"))
			{
				dfny_type = "int" ;
			}

			NodeList var = node.getChildNodes();
			int v = var.getLength();
			//if there are multiple variables, we need to add commas between their names
			if(v>1)
			{
				for (int j =0; j<v-1; j++)
				{
					String vars = var.item(j).getTextContent();
					out.write(vars + ",");
				}
			}
			String vars = var.item(v-1).getTextContent();

			out.write(vars + " ");
			out.write(":"+dfny_type+":: ");
		}

		else if(elementName.equals("body"))
		{
			//System.out.println("found body");
			traverseTree(node.getFirstChild(),doc , out);

		}
		else if(elementName.equals("true"))
		{
			out.write("true");
		}
		else if(elementName.equals("false"))
		{
			out.write("false");
		}

		else if(elementName.equals("function"))
		{
			String name = node.getAttributes().getNamedItem("name").getNodeValue();
			String type = node.getAttributes().getNamedItem("type").getNodeValue();

			out.write(" "+name+ "(");
			
			NodeList args = node.getChildNodes();
			int a = args.getLength();
			
			//System.out.println(a);
			
			//System.out.println(node.getFirstChild().getNodeName().toString());

			//findMethods(doc);

			if (a> 1) 
			{
				for (int j = 0; j < a-1; j++)
				{
					traverseTree(args.item(j).getFirstChild(),doc , out);
					out.write(",");
				}
			}
			traverseTree(args.item(a-1).getFirstChild(),doc , out);
			out.write(")  ");
		}
		else if(elementName.equals("substring") || elementName.equals("SUBSTRING"))
		{
			out.write("SUBSTRING( ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write(",");
			traverseTree(node.getFirstChild().getNextSibling(),doc , out);
			out.write(",");
			traverseTree(node.getLastChild(),doc , out);
			out.write(")");
		}
		else if(elementName.equals("reverse"))
		{
			out.write("reverse( ");
			traverseTree(node.getFirstChild(),doc , out);
			out.write(")");
		}

	}//end traverseTree(Node node)
	
	public void declare_global(Node node, Document doc, BufferedWriter out) throws Exception
	{
		//out.newLine();
		out.write("s = [");
		
		NodeList ele = node.getLastChild().getChildNodes();
		int numchildren = node.getLastChild().getChildNodes().getLength();
		System.out.println("numchildren: "+numchildren);
		
		if (numchildren > 1) {
			for (int j = 0; j < numchildren - 1; j++) 
			{
				String elementName = ele.item(j).getNodeName();
			
				System.out.println("elementName: "+elementName);
				
				 if (elementName.equals("zero"))				 
						out.write(" 0, ");
				 else if(elementName.equals("constant"))
				 {
					 String constant = ele.item(j).getFirstChild().getTextContent();
						out.write(" " + constant + ", ");					 
				 }
				 
			}
		}
		
		String elementName = ele.item(numchildren-1).getNodeName();
		
		 if (elementName.equals("zero"))				 
				out.write(" 0");
		 else if(elementName.equals("constant"))
		 {
			 String constant = ele.item(numchildren-1).getFirstChild().getTextContent();
				out.write(" " + constant);					 
		 }
			
		out.write("];");
		//out.newLine();
	}
	
	public void declare_global1(Node node, Document doc, BufferedWriter out) throws Exception
	{
		//out.newLine();
		//out.write("s2 = [");
		out.write("[");
		
		NodeList ele = node.getLastChild().getChildNodes();
		int numchildren = node.getLastChild().getChildNodes().getLength();
		System.out.println("numchildren: "+numchildren);
		
		if (numchildren > 0) {
		if (numchildren > 1) {
			for (int j = 0; j < numchildren - 1; j++) 
			{
				String elementName = ele.item(j).getNodeName();
			
				System.out.println("elementName: "+elementName);
				
				 if (elementName.equals("zero"))				 
						out.write(" 0, ");
				 else if(elementName.equals("constant"))
				 {
					 String constant = ele.item(j).getFirstChild().getTextContent();
						out.write(" " + constant + ", ");					 
				 }
				 
			}
		}
		
		String elementName = ele.item(numchildren-1).getNodeName();
		
		 if (elementName.equals("zero"))				 
				out.write(" 0");
		 else if(elementName.equals("constant"))
		 {
			 String constant = ele.item(numchildren-1).getFirstChild().getTextContent();
				out.write(" " + constant);					 
		 }
		}
			
		//out.write("];");
		//out.newLine();
		out.write("]");
	}
	
	public void declare_global2(Node node, Document doc, BufferedWriter out) throws Exception
	{
		//out.newLine();
		//out.write("s1 = [");
		out.write("[");
		
		NodeList ele = node.getLastChild().getChildNodes();
		int numchildren = node.getLastChild().getChildNodes().getLength();
		System.out.println("numchildren: "+numchildren);
		
		if (numchildren > 0) {
		if (numchildren > 1) {
			for (int j = 0; j < numchildren - 1; j++) 
			{
				String elementName = ele.item(j).getNodeName();
			
				System.out.println("elementName: "+elementName);
				
				 if (elementName.equals("zero"))				 
						out.write(" 0, ");
				 else if(elementName.equals("constant"))
				 {
					 String constant = ele.item(j).getFirstChild().getTextContent();
						out.write(" " + constant + ", ");					 
				 }
				 
			}
		}
		
		String elementName = ele.item(numchildren-1).getNodeName();
		
		 if (elementName.equals("zero"))				 
				out.write(" 0");
		 else if(elementName.equals("constant"))
		 {
			 String constant = ele.item(numchildren-1).getFirstChild().getTextContent();
				out.write(" " + constant);					 
		 }
		}
			
		//out.write("];");
		//out.newLine();
		out.write("]");
	}

}
