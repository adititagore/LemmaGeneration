import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class XMLProcessor {

	ArrayList<String> var_decld = new ArrayList<String>();
	ArrayList<String> fun_decld = new ArrayList<String>();

	FileWriter fstream;
	BufferedWriter out;
	int c = 0, i = 0;

	public void ProcessXmlsFromDirectory(String files, String DirName) {
		try {
			String wholepath = DirName + files;
			// System.out.println("The whole path is "+wholepath);

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

			// open file for writing

			fstream = new FileWriter(
					"C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\dafny\\"
							+ files.substring(0, (files.length() - 4)) + ".dfy");
			out = new BufferedWriter(fstream);

			// Node formula = root.getLastChild();

			// Node implies = formula.getFirstChild();
			// System.out.println(implies.toString());
			// System.out.println(implies.getFirstChild().toString());

			String cName = doc.getDocumentElement().getAttribute("cName");
			
			//change for contra
			out.write("class Client<T>{");
			
			out.newLine();
			// declare_vars(doc);
			// declare_funs(doc);

			get_function_header();
			out.write("method formula() {");
			out.newLine();

			get_functions();

			// declare_lemmas(doc);
			// NodeList facts = implies.getFirstChild().getChildNodes();
			// Node obligation = implies.getLastChild();
			// int f = facts.getLength();
			// findMethods(doc);

			// if (f> 0)
			// {
			// for (int j = 0; j < f; j++)
			// {
			// out.newLine();
			// out.write("assume ");
			// traverseTree(facts.item(j) , doc);
			// out.write(";");
			// }
			// }//end if (f> 0)

			Node lemma = root.getLastChild();
			
			//declare_global(lemma.getFirstChild(), doc);
			//declare_global_1(lemma.getFirstChild(), doc);
			//declare_global_2(lemma.getFirstChild().getNextSibling(), doc);
			out.newLine();
			out.write("assert ");
			//need to change for Diego's style from Contra
			//traverseTree(lemma.getFirstChild(), doc);
			
			
			traverseTree(lemma.getLastChild(), doc);
			out.write(";");
			out.newLine();
			out.write("} }");
			out.newLine();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}// end ProcessXMLsFromDirectory


	public void get_function_header() {
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(
					"C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\defns\\defn_header.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				// System.out.println (strLine);
				out.write(strLine);
				out.newLine();
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public void get_functions() {
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(
					"C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\defns\\defn.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				// System.out.println (strLine);
				out.write(strLine);
				out.newLine();
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

	}

	public void declare_vars(Document doc) throws IOException {

		NodeList vars = doc.getElementsByTagName("symbol");
		int numSymbols = vars.getLength();

		out.newLine();

		if (numSymbols > 0) {
			for (int i = 0; i < numSymbols; i++) {
				// System.out.println(vars.item(i).getParentNode().getNodeName().toString());
				if (!(vars.item(i).getParentNode().getNodeName().toString()
						.equals("dot"))) {
					String symbol = vars.item(i).getFirstChild()
							.getTextContent();
					// System.out.println("symbol: "+symbol);
					String type = vars.item(i).getAttributes()
							.getNamedItem("type").getNodeValue();

					if (symbol.contains(".")) {
						int index = symbol.indexOf(".");
						symbol = symbol.substring(0, index) + "_"
								+ symbol.substring(index + 1);
						// System.out.println(symbol);
					}
					if (!(var_decld.contains(symbol))) {
						var_decld.add(symbol);

						if (type.equals("finiteset(object)")) {

							out.write("var " + symbol + " : set<T>;");
							out.newLine();
						}

						else if (type.equals("object")
								|| type.equals("character")) {
							out.write("var " + symbol + " : T;");
							out.newLine();
						}

						else if (type.equals("integer")) {
							out.write("var " + symbol + " : int;");
							out.newLine();
						} else if (type.equals("string(object)")
								|| type.equals("string(character)")) {
							out.write("var " + symbol + " : seq<T>;");
							out.newLine();
						} else if (type.equals("string(integer)")) {
							out.write("var " + symbol + " : seq<int>;");
							out.newLine();
						} else if (type.equals("boolean")) {
							out.write("var " + symbol + " : bool;");
							out.newLine();
						}
					}

				}// end for
			}// end if
		}
		out.newLine();

		NodeList dots = doc.getElementsByTagName("dot");
		int numDots = dots.getLength();
		// System.out.println("numDots"+numDots);

		for (int i = 0; i < numDots; i++) {
			String type = dots.item(i).getAttributes().getNamedItem("type")
					.getNodeValue();

			int numChild = dots.item(i).getChildNodes().getLength();
			String dot_var = "";
			if (numChild <= 2) {
				String p1 = dots.item(i).getFirstChild().getTextContent();
				String p2 = dots.item(i).getLastChild().getTextContent();
				// System.out.println("p1: "+p1);
				// System.out.println("p2: "+p2);
				dot_var = p1 + "_" + p2;
			} else {
				String p1 = dots.item(i).getFirstChild().getTextContent();
				String p2 = dots.item(i).getFirstChild().getNextSibling()
						.getTextContent();
				String p3 = dots.item(i).getLastChild().getTextContent();

				dot_var = p1 + "_" + p2 + "_" + p3;
			}

			if (dot_var.contains(".")) {
				int index = dot_var.indexOf(".");
				dot_var = dot_var.substring(0, index) + "_"
						+ dot_var.substring(index + 1);
			}
			if (!(var_decld.contains(dot_var))) {
				var_decld.add(dot_var);
				if (type.equals("string(object)")
						|| type.equals("string(character)")) {
					out.write("var " + dot_var + " : seq<T>;");
				}
				if (type.equals("string(integer)")) {
					out.write("var " + dot_var + " : seq<int>;");
				} else if (type.equals("integer")) {
					out.write("var " + dot_var + " : int;");
					out.newLine();
				} else if (type.equals("boolean")) {
					out.write("var " + dot_var + " : bool;");
					out.newLine();
				} else if (type.equals("object") || type.equals("character")) {
					out.write("var " + dot_var + " : T;");
					out.newLine();
				}
				out.newLine();
			}
		}// end for

	}// end declare_vars(Document doc)

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

	public void traverseTree(Node node, Document doc) throws Exception {
		NodeList card = null;
		card = doc.getElementsByTagName("bar");
		String cName = doc.getDocumentElement().getAttribute("cName");
		NodeList dots = doc.getElementsByTagName("dot");
		int numDots = dots.getLength();
		// System.out.println("numDots"+numDots);

		// Extract node info:
		String elementName = node.getNodeName();
		String val = node.getNodeValue();

		if (elementName.equals("neq")) {
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write("!=");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("eq")) {
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write("==");
			traverseTree(node.getLastChild(), doc);
			out.write(")");

		}

		else if (elementName.equals("geq")) {
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(">=");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("leq")) {
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write("<=");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("gt")) {
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write(">");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("lt")) {
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write("<");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("add")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write("+");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("subtract")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write("-");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("star")) // for integers
		{
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			// if(cName.contains("Queue") || cName.contains("List") ||
			// cName.contains("Sequence") || cName.contains("Stack")||
			// cName.contains("InputStreamTemplate"))
			// {
			out.write("+");
			// }
			// else
			// out.write("*");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		}

		else if (elementName.equals("union")) {
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write("+");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("intersection")) {
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write("*");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		} else if (elementName.equals("and")) {
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write("&&");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("or")) {
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write("||");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}
		if (elementName.equals("implies")) {
			out.write("(");
			traverseTree(node.getFirstChild(), doc);
			out.write("==>");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		} else if (elementName.equals("bar")) {
			// System.out.println(cName);
			String type = node.getFirstChild().getAttributes()
					.getNamedItem("type").getNodeValue();
			;
			// System.out.println(type);

			/*
			 * if(cName.contains("Set") || cName.contains("Integer")) {
			 * if(!(cName.contains("ListOfIntegerFacility")) &&
			 * !(cName.contains("SequenceTemplate")) &&
			 * !(cName.contains("QueueOfIntegerFacility")) &&
			 * !(cName.contains("InputStreamTemplate"))) { out.write("(card (");
			 * traverseTree(node.getFirstChild(), doc); out.write(")) "); } }
			 * 
			 * if(cName.contains("Queue")|| cName.contains("SequenceTemplate")
			 * || cName.contains("ListTemplate") ||
			 * cName.contains("ListOfInteger") || cName.contains("Stack") ||
			 * cName.contains("TextFacility") ||
			 * cName.contains("ArrayAsStringTemplate") ||
			 * cName.contains("InputStreamTemplate")) { //
			 * System.out.println("hey"); out.write("(|");
			 * traverseTree(node.getFirstChild(), doc); out.write("|) "); }
			 */

			if (type.equals("string(object)") || type.equals("string(integer)")
					|| type.equals("string(character)")) {
				out.write("(|");
				traverseTree(node.getFirstChild(), doc);
				out.write("|) ");
			}

			else if (type.equals("finiteset(object)") || type.equals("integer")
					|| type.equals("finiteset(tuple(d:object,r:object))")) {
				out.write("(card (");
				traverseTree(node.getFirstChild(), doc);
				out.write(")) ");

			}
		}

		else if (elementName.equals("dot")) {
			String p1 = node.getFirstChild().getTextContent();
			String p2 = node.getLastChild().getTextContent();
			String dot_var = p1 + "_" + p2;
			if (dot_var.contains(".")) {
				int index = dot_var.indexOf(".");
				dot_var = dot_var.substring(0, index) + "_"
						+ dot_var.substring(index + 1);
			}
			out.write(" " + dot_var + " ");
		} else if (elementName.equals("negate")) {
			out.write("(- ");
			traverseTree(node.getFirstChild(), doc);
			out.write(") ");
		} else if (elementName.equals("difference")) {
			out.write("( ");
			traverseTree(node.getFirstChild(), doc);
			out.write("-");
			traverseTree(node.getLastChild(), doc);
			out.write(") ");
		}

		else if (elementName.equals("singleton")) {
			out.write("({ ");
			traverseTree(node.getFirstChild(), doc);
			out.write("}) ");
		} else if (elementName.equals("symbol")
				&& !(node.getParentNode().getNodeName().toString()
						.equals("dot"))) {
			String symbol = node.getFirstChild().getTextContent();
			if (symbol.contains(".")) {
				int index = symbol.indexOf(".");
				symbol = symbol.substring(0, index) + "_"
						+ symbol.substring(index + 1);
				// System.out.println(symbol);
			}
			out.write(" " + symbol + " ");

		}

		else if (elementName.equals("constant")) {
			// op = read_text_set(node);
			String constant = node.getFirstChild().getTextContent();
			out.write(" " + constant + " ");
		}

		else if (elementName.equals("emptyset")) {
			out.write(" {}");
		} else if (elementName.equals("emptystring")) {
			out.write(" []");
		} else if (elementName.equals("stringleton")) {
			out.write("([ ");
			traverseTree(node.getFirstChild(), doc);
			out.write("]) ");
		} else if (elementName.equals("zero")) {
			out.write(" 0");
		}

		else if (elementName.equals("is_initial")) {

			out.write(" (is_initial (");
			traverseTree(node.getFirstChild(), doc);
			out.write(")) ");
		} else if (elementName.equals("elements")) {

			out.write(" (elements (");
			traverseTree(node.getFirstChild(), doc);
			out.write(")) ");
		}

		else if (elementName.equals("element")) {
			out.write(" ( ");
			traverseTree(node.getFirstChild(), doc);
			out.write("in");
			traverseTree(node.getLastChild(), doc);

			out.write(") ");
		}

		else if (elementName.equals("not")) {

			out.write("( ! ");
			traverseTree(node.getFirstChild(), doc);
			out.write(")  ");
		}

		else if (elementName.equals("thereexists")
				|| elementName.equals("exists")) {
			out.write("exists ");
			traverseTree(node.getFirstChild(), doc);
			traverseTree(node.getLastChild(), doc);
		} else if (elementName.equals("forall")) {
			out.write("forall ");
			traverseTree(node.getFirstChild(), doc);
			//added the next line for contra
			//out.write(", ");
			//traverseTree(node.getFirstChild().getNextSibling(), doc);
			out.write(":: ");
			traverseTree(node.getLastChild(), doc);
		} else if (elementName.equals("vars")) {
			String type = node.getAttributes().getNamedItem("type")
					.getNodeValue();
			String dfny_type = "";

			// check whether the symbol has been declared or not

			if (type.equals("finiteset(object)")) {

				dfny_type = "set<T>";
			}

			if (type.equals("string(object)") || type.equals("string of item")
					|| type.equals("string")) {

				dfny_type = "seq<T>";
				//for contra
				//dfny_type = "seq<int>";
			}

			if (type.equals("string(integer)")) {

				dfny_type = "seq<int>";
			}

			else if (type.equals("object")) {
				dfny_type = "T";
				//for contra
				//dfny_type = "int";
			}

			else if (type.equals("integer")) {
				dfny_type = "int";
			}

			NodeList var = node.getChildNodes();
			int v = var.getLength();
			// if there are multiple variables, we need to add commas between
			// their names
			if (v > 1) {
				for (int j = 0; j < v - 1; j++) {
					String vars = var.item(j).getTextContent();
					out.write(vars + ": " + dfny_type + ",");
				}
			}
			String vars = var.item(v - 1).getTextContent();

			out.write(vars + " ");
			out.write(":" + dfny_type);
		}

		else if (elementName.equals("body")) {
			// System.out.println("found body");
			traverseTree(node.getFirstChild(), doc);

		} else if (elementName.equals("true")) {
			out.write("true");
		} else if (elementName.equals("false")) {
			out.write("false");
		}

		else if (elementName.equals("function")) {
			String name = node.getAttributes().getNamedItem("name")
					.getNodeValue();
			String type = node.getAttributes().getNamedItem("type")
					.getNodeValue();

			out.write(" " + name + "(");

			NodeList args = node.getChildNodes();
			int a = args.getLength();

			// findMethods(doc);

			if (a > 1) {
				for (int j = 0; j < a - 1; j++) {
					traverseTree(args.item(j).getFirstChild(), doc);
					out.write(",");
				}
			}
			traverseTree(args.item(a - 1).getFirstChild(), doc);
			out.write(")  ");
		} else if (elementName.equals("substring")
				|| elementName.equals("SUBSTRING")) {
			out.write("SUBSTRING( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(",");
			traverseTree(node.getFirstChild().getNextSibling(), doc);
			out.write(",");
			traverseTree(node.getLastChild(), doc);
			out.write(")");
		} else if (elementName.equals("reverse")) {
			out.write("reverse( ");
			traverseTree(node.getFirstChild(), doc);
			out.write(")");
		}

	}// end traverseTree(Node node)

	public void declare_funs(Document doc) throws IOException {
		// find card

		NodeList card = doc.getElementsByTagName("bar");
		NodeList substring = doc.getElementsByTagName("substring");

		NodeList reverse = doc.getElementsByTagName("reverse");

		String cName = doc.getDocumentElement().getAttribute("cName");

		if (cName.contains("Set") && !(cName.contains("SequenceTemplate"))) 
		{

			if (card.getLength() > 0) {
				out.newLine();
				// out.write("(declare-fun card (Set) Int)");
				// out.write("method card (s: set<T>) returns (y:int)");
				out.write("function card (s: set<T>) :int");
				out.newLine();
			}
		}//end if (cName.contains("Set") && !(cName.contains("SequenceTemplate"))) 

		if ((cName.contains("Integer") || cName.contains("Natural"))
				&& !(cName.contains("ListOfIntegerFacility")))
		{
			if (card.getLength() > 0) {
				out.newLine();
				out.write("function card (x: int):int");
				out.newLine();
				out.write("{ if (x >= 0) then x else  -x }");
				out.newLine();

			}
		}//end if ((cName.contains("Integer") || cName.contains("Natural"))	&& !(cName.contains("ListOfIntegerFacility")))

		// find is_initial

		NodeList is_initial = doc.getElementsByTagName("is_initial");

		if (is_initial.getLength() > 0) 
		{
			out.newLine();
			out.write("function is_initial(x: T) : bool");
			out.newLine();
			// out.write("{}");
			out.newLine();
		}// end if (is_initial.getLength() > 0) 
		
		NodeList elements = doc.getElementsByTagName("elements");

		if (cName.contains("ListOfIntegerFacility")) 
		{
			if (elements.getLength() > 0) {
				out.newLine();
				out.write(" function elements(s: seq<int>): set<int>");
				out.newLine();
				out.newLine();
			}

			if (substring.getLength() > 0) {
				out.newLine();
				out.write("function SUBSTRING(s: seq<int>, start : int, finish: int) : seq<int>");
				out.newLine();
				out.newLine();
			}
		} //end if (cName.contains("ListOfIntegerFacility")) 
		
		else 
		{

			if (elements.getLength() > 0) {
				out.newLine();
				out.write(" function elements(s: seq<T>): set<T>");
				out.newLine();
				out.newLine();
			}

			if (substring.getLength() > 0) {
				out.newLine();
				out.write("function SUBSTRING(s: seq<T>, start : int, finish: int) : seq<T>");
				out.newLine();
				out.newLine();
			}
		}//end else
		
		// List definitions

		if (cName.contains("Natural")) 
		{
			if (reverse.getLength() > 0) {
				out.newLine();
				out.write("function reverse(s: seq<int>): seq<int> ");
				out.newLine();
				out.newLine();
			}

		} else {
			if (reverse.getLength() > 0) {
				out.newLine();
				out.write("function reverse(s: seq<T>): seq<T> ");
				out.newLine();
				out.newLine();
			}
		}// end else if (cName.contains("Natural")) 

		NodeList fn = doc.getElementsByTagName("function");

		int numfuns = fn.getLength();

		out.newLine();

		for (int i = 0; i < numfuns; i++) {
			 System.out.println("heyyy");
			 System.out.println(fn.item(i).getFirstChild().toString());
			String fn_name = fn.item(i).getAttributes().getNamedItem("name")
					.getNodeValue();

			// System.out.println(fn_name);
			if (!(var_decld.contains(fn_name))) {
				var_decld.add(fn_name);

				if (cName.contains("QueueOfIntegerFacility")) {
					if (fn_name.equals("OCCURS_COUNT")) {

						out.write("function " + fn_name
								+ " (s : seq<int>, i :int): int");
						out.newLine();
					} else if (fn_name.equals("ARE_PERMUTATIONS")) {

						out.write("function " + fn_name
								+ " (s1 :seq<int>, s2: seq<int>): bool");
						out.newLine();
					} else if (fn_name.equals("PRECEDES")) {

						out.write("function " + fn_name
								+ "  (s1: seq<int>, s2: seq<int>): bool");
						out.newLine();
					} else if (fn_name.equals("IS_NONDECREASING")) {

						out.write("function " + fn_name
								+ " (s: seq<int>): bool ");
						out.newLine();
					}

				}// end if
				else {
					if (fn_name.equals("ARE_IN_ORDER")) {

						out.write("function " + fn_name
								+ " (x : T, y : T) : bool");
						out.newLine();
					}

					else if (fn_name.equals("OCCURS_COUNT")) {

						out.write("function " + fn_name
								+ " (s : seq<T>, i :T): int");
						out.newLine();
					} else if (fn_name.equals("ARE_PERMUTATIONS")) {

						out.write("function " + fn_name
								+ " (s1 :seq<T>, s2: seq<T>): bool");
						out.newLine();
					} else if (fn_name.equals("PRECEDES")) {

						out.write("function " + fn_name
								+ "  (s1: seq<T>, s2: seq<T>): bool");
						out.newLine();
					} else if (fn_name.equals("IS_NONDECREASING")) {

						out.write("function " + fn_name + " (s: seq<T>): bool ");
						out.newLine();
					}
				}// end else

				if (fn_name.equals("IS_PRECEDING")) {

					out.write("function " + fn_name
							+ " (x: set<T>, y: set<T>) : bool ");
					out.newLine();
				} else if (fn_name.equals("IS_ODD")) {

					out.write("function " + fn_name + " (n: int): bool ");
					out.newLine();
				} else if (fn_name.equals("DIFFER_BY_ONE")) {

					out.write("function "
							+ fn_name
							+ " (t1: seq<T>, t2: seq<T>, pos: int, ch: T): bool ");
					out.newLine();
				} else if (fn_name.equals("SAME_EXCEPT_AT")) {

					out.write("function "
							+ fn_name
							+ " (t1: seq<T>, t2: seq<T>, pos: int, x: T, y: T): bool ");
					out.newLine();
				} else if (fn_name.equals("SUBSTRING")) {

					out.write("function " + fn_name
							+ " (s: seq<T>, start : int, finish: int) : seq<T>");
					out.newLine();
				} else if (fn_name.equals("SUBSTRING_REPLACEMENT")) {

					out.write("function "
							+ fn_name
							+ " (s: seq<T>, ss: seq<T>, start: int, finish: int): seq<T>");
					out.newLine();
				} else if (fn_name.equals("FUNCTION")) {

					out.write("function " + fn_name + " (x: set<T>): set<T>");
					out.newLine();
				} else if (fn_name.equals("HAS_ONLY_DIGITS")) {

					out.write("function " + fn_name
							+ " (digits: seq<int>, radix: int): bool");
					out.newLine();
				} else if (fn_name.equals("NUMERICAL_VALUE")) {

					out.write("function " + fn_name
							+ " (s: seq<int>, r: int): int");
					out.newLine();
				} else if (fn_name.equals("IS_WELL_FORMED_FUNCTION")) {

					out.write("function " + fn_name
							+ " (digits: seq<int>, radix: int): bool");
					out.newLine();
				}
			}// end if

		}// end for

		out.newLine();

	}
	public static NodeList appendNodeLists(Document root,
			NodeList a, NodeList b, NodeList c)
	{
		Element allNodes = root.createElement("allNodes");

		for (int i = 0; i < a.getLength(); i++)
			allNodes.appendChild(a.item(i));

		for (int i = 0; i < b.getLength(); i++) 
			allNodes.appendChild(b.item(i));

		for (int i = 0; i < c.getLength(); i++)
			allNodes.appendChild(c.item(i));

		return allNodes.getChildNodes();
	}
	public void declare_lemmas(Document doc) throws IOException {
		NodeList card = doc.getElementsByTagName("bar");

		String cname = doc.getDocumentElement().getAttribute("cName");

		if (cname.contains("Set") && !(cname.contains("SequenceTemplate"))) {

			if (card.getLength() > 0) {
				out.newLine();
				out.write("//	cardinality lemmas		");
				out.newLine();
				out.write("assume (forall s: set<T> :: card(s) >= 0);");
				out.newLine();
				out.write("assume (forall s: set<T> :: s == {} ==> card(s) == 0);");
				out.newLine();
				out.write("assume (forall s: set<T>, x: T :: x in s ==> card(s-{x}) == (card(s) - 1 ));");
				out.newLine();
				out.newLine();

			}
		}

		NodeList substring = doc.getElementsByTagName("substring");

		NodeList elements = doc.getElementsByTagName("elements");

		if (cname.contains("ListOfIntegerFacility")) {
			if (substring.getLength() > 0) {
				out.newLine();
				out.write("//substring definition");
				out.newLine();
				out.write("assume ( forall s: seq<int>, start : int, finish: int ::(start < 0 || start > finish || finish > |s| ==> SUBSTRING(s, start, finish) == []) && (!(start < 0 || start > finish || finish > |s|) ==> (exists a:seq<int>, b:seq<int> :: s == a + SUBSTRING(s, start, finish) + b && |a| == start&& |b| == |s| - finish)));");
				out.newLine();
				out.write("//substring lemmas");
				out.newLine();
				out.write("assume (forall s: seq<int>, start : int, finish: int :: start < 0 || start > finish || finish > |s| ==> SUBSTRING(s, start, finish) == []) ;");
				out.newLine();
				out.write("assume (forall s: seq<int>, start : int, finish: int :: |s| == 0 ==> SUBSTRING(s, start, finish) == []) ;");
				out.newLine();
				out.write("assume (forall m: int, n:int, a: seq<int> ::  m >=0 && m <= n && n <= |a| ==> SUBSTRING(a, 0, m) + SUBSTRING(a, m, n) == SUBSTRING(a, 0, n));");
				out.newLine();
				out.write("assume (forall m: int, n:int, a: seq<int> :: m >=0 && m <= n && n <= |a| ==> SUBSTRING(a, 0, m) + SUBSTRING(a, m, n) + SUBSTRING(a, n, |a|) == a );");
				out.newLine();
				out.write("assume (forall j: int, k:int, s1: seq<int>, s2: seq<int> :: j >=0 && j <= k && k <= |s1| ==> SUBSTRING(s1 + s2, j, k) == SUBSTRING(s1, j, k));");
				out.newLine();
				out.write("assume (forall j: int, k:int, s1: seq<int>, s2: seq<int> :: j >= |s1| && j <= k && k <= (|s1| + |s2|) ==> SUBSTRING(s1 + s2, j, k) == SUBSTRING(s2, j - |s1|, k - |s1|));");
				out.newLine();
				out.newLine();
			}

			if (elements.getLength() > 0) {
				out.newLine();
				out.write("assume (forall s:seq<int> :: if s ==[] then elements(s) == {} else (exists t: seq<int>, x: int :: s == [x] + t && elements(s) == {x} + elements(t)));");
				out.newLine();
			}
		}

		if (!(cname.contains("ListOfIntegerFacility"))) {
			if (elements.getLength() > 0) {
				out.newLine();
				out.write("// elements definition");
				out.newLine();
				out.write("assume (forall s:seq<T> :: if s ==[] then elements(s) == {} else (exists t: seq<T>, x: T :: s == [x] + t && elements(s) == {x} + elements(t)));");
				out.newLine();
				out.write("//elements lemmas");
				out.newLine();
				out.write("assume(forall a:seq<T>, b: seq<T> :: elements(a+b) == elements(a) + elements(b));");
				out.newLine();
				out.write("assume(forall a:seq<T>, x: T :: elements([x] + a) == elements(a) + {x});");
				out.newLine();
				out.write("assume(forall a:seq<T>, b:seq<T> :: (a == b) ==> (elements(a) == elements(b)));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: elements((a + (b + c))) == elements(((a + b) + c)));");
				out.newLine();
				out.write("assume (elements([]) == {});");
				out.newLine();
				out.write("assume (forall a: seq<T>, x: T :: ((elements([x] + a)) - {x}) == elements(a));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, x: T :: elements((a + b)) == (elements(((a + [x]) + b)) - {x}));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: elements((a + b)) == (elements(((a + c) + b)) - elements(c)));");
				out.newLine();
				// out.write("assume (forall a: seq<T>, x: T :: (x !in elements(a)) ==> (|a + [x]| ) >= card(elements(a) + {x}));");
				// out.newLine();
				// out.write("assume (forall a: seq<T>, x: T :: (x !in elements(a)) ==>(card(elements(a)) < card(elements(a) + {x})));");
				// out.newLine();
				// out.write("assume(forall a: seq<T>, x: T :: |a+[x]| == card(elements(a)) + 1 ==> |a| == card(elements(a)));");
				// out.newLine();
				out.write("assume(forall a:seq<T>, b: seq<T> :: elements(a+b) == elements(b+a));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: elements(((a + b) + c)) == elements(((c + b) + a)));");
				out.newLine();
				out.newLine();
			}

			if (substring.getLength() > 0) {
				out.newLine();
				out.write("//substring definition");
				out.newLine();
				out.write("assume ( forall s: seq<T>, start : int, finish: int ::(start < 0 || start > finish || finish > |s| ==> SUBSTRING(s, start, finish) == []) && (!(start < 0 || start > finish || finish > |s|) ==> (exists a:seq<T>, b:seq<T> :: s == a + SUBSTRING(s, start, finish) + b && |a| == start&& |b| == |s| - finish)));");
				out.newLine();
				out.write("//substring lemmas");
				out.newLine();
				out.write("assume (forall s: seq<T>, start: int, finish: int :: start < 0 || start > finish || finish > |s| ==> SUBSTRING(s, start, finish) == []);");
				out.newLine();
				out.write("assume (forall s: seq<T>, start: int, finish: int :: |s| == 0 ==> SUBSTRING(s, start, finish) == []);");
				out.newLine();
				out.write("assume (forall m: int, n: int, a: seq<T> :: 0 <= m && m <= n && n <= |a| ==> SUBSTRING(a, 0, m) + SUBSTRING(a, m, n) == SUBSTRING(a, 0, n));");
				out.newLine();
				out.write("assume (forall m: int, n: int, a: seq<T> :: 0 <= m && m <= n && n <= |a| ==> SUBSTRING(a, 0, m) + SUBSTRING(a, m, n) + SUBSTRING(a, n, |a|) == a);");
				out.newLine();
				out.write("assume (forall j: int, k: int, s1: seq<T>, s2: seq<T> :: 0 <= j && j <= k && k <= |s1| ==> SUBSTRING(s1 + s2, j, k) == SUBSTRING(s1, j, k));");
				out.newLine();
				out.write("assume (forall j: int, k: int, s1: seq<T>, s2: seq<T> :: |s1| <= j && j <= k && k <= (|s1| + |s2|) ==> SUBSTRING(s1 + s2, j, k) == SUBSTRING(s2, j - |s1|, k - |s1|));");
				out.newLine();
				out.newLine();
			}
		}
		NodeList reverse = doc.getElementsByTagName("reverse");

		if (cname.contains("Natural")) {
			if (reverse.getLength() > 0) {
				out.newLine();
				out.write("//reverse definition");
				out.newLine();
				out.write("assume (forall s:seq<int> :: if s ==[] then reverse(s) == [] else (exists t: seq<int>, x: int :: s == [x] + t && reverse(s) == reverse(t) + [x]));");
				out.newLine();
				out.write("//reverse lemmas");
				out.newLine();
				out.write("assume (forall s1:seq<int>, s2:seq<int> :: s1 == reverse(s2) ==> s2 == reverse(s1));");
				out.newLine();
				out.write("assume (reverse([]) == []);");
				out.newLine();
				out.write("assume(forall x: int :: reverse([x]) == [x]);");
				out.newLine();
				out.write("assume (forall x: seq<int>, s: seq<int> :: reverse(s + x) == reverse(x) + reverse(s));");
				out.newLine();
				out.write("assume ( forall s1:seq<int>, s2: seq<int>, s3: seq<int>, s4: seq<int> :: ((reverse(s1) + s2) == (reverse(s3) + s4) && |s3| == |s1| )==> (s1 ==s3 && s2 == s4));");
				out.newLine();
				out.write("assume (forall a: seq<int> :: |reverse(a)| == |a|);");
				out.newLine();
				out.write("assume (forall a: seq<int>, b: seq<int> :: (reverse(a) == reverse(b)) ==> (a == b));");
				out.newLine();
				out.newLine();
			}
		} else {
			if (reverse.getLength() > 0) {
				out.newLine();
				out.write("//reverse definition");
				out.newLine();
				out.write("assume (forall s:seq<T> :: if s ==[] then reverse(s) == [] else (exists t: seq<T>, x: T :: s == [x] + t && reverse(s) == reverse(t) + [x]));");
				out.newLine();
				out.write("//reverse lemmas");
				out.newLine();
				out.write("assume (reverse([]) == []);");
				out.newLine();
				out.write("assume (forall x: T :: reverse([x]) == [x]);");
				out.newLine();
				out.write("assume (forall s1: seq<T>, s2: seq<T> :: s1 == reverse(s2) ==> s2 == reverse(s1));");
				out.newLine();
				out.write("assume (forall s1: seq<T>, s2: seq<T> :: reverse(s2 + s1) == reverse(s1) + reverse(s2));");
				out.newLine();
				out.write("assume (forall s1: seq<T>, s2: seq<T>, s3: seq<T>, s4: seq<T> :: ((reverse(s1) + s2) == (reverse(s3) + s4) && |s3| == |s1|)==> (s1 == s3 && s2 == s4));");
				out.newLine();
				out.write("assume (forall s1: seq<T> :: |reverse(s1)| == |s1|);");
				out.newLine();
				out.write("assume (forall s1: seq<T>, s2: seq<T> :: (reverse(s1) == reverse(s2)) ==> (s1 == s2));");
				out.newLine();
				out.newLine();
			}
		}

		if (cname.contains("QueueOfIntegerFacility")) {
			if (var_decld.contains("OCCURS_COUNT")) {
				out.newLine();
				out.write("//definition of OCCURS_COUNT");
				out.newLine();
				out.write("assume ( forall s:seq<int>, i :int :: if s == [] then OCCURS_COUNT(s, i) == 0 else (exists x: int, r: seq<int> :: s == [x] + r && (if x == i then OCCURS_COUNT(s, i) == OCCURS_COUNT(r, i) +1 else OCCURS_COUNT(s, i) == OCCURS_COUNT(r, i)))); ");
				out.newLine();
			}
			if (var_decld.contains("PRECEDES")) {
				out.newLine();
				out.write("//definition of PRECEDES");
				out.newLine();
				out.write("assume (forall s1: seq<int>, s2: seq<int> :: PRECEDES(s1, s2) ==(forall i : int , j : int :: OCCURS_COUNT(s1, i) > 0 && OCCURS_COUNT(s2, j) > 0 ==> (i <= j))) ;");
				out.newLine();
			}
			if (var_decld.contains("IS_NONDECREASING")) {
				out.newLine();
				out.write("//definition of IS_NONDECREASING");
				out.newLine();
				out.write("assume (forall s: seq<int>:: IS_NONDECREASING(s) == (forall a: seq<int> , b: seq<int> :: s == a + b ==> PRECEDES(a, b)));  ");
				out.newLine();
				out.write("//IS_NONDECREASING lemmas");
				out.newLine();
				out.write("assume (IS_NONDECREASING([]));");
				out.newLine();
				out.write("assume (forall x:int :: IS_NONDECREASING([x]));");
				out.newLine();
				out.write("assume (forall q: seq<int> :: |q| <= 1 ==> IS_NONDECREASING(q));");
				out.newLine();
				out.write("assume (forall x:seq<int> :: IS_NONDECREASING([]+x) ==> IS_NONDECREASING(x));");
				out.newLine();
				out.write("assume (forall x:seq<int> :: IS_NONDECREASING(x+[])  ==> IS_NONDECREASING(x));");
				out.newLine();
				out.write("assume (forall a: int, b: int :: (a <= b) ==> IS_NONDECREASING([a] + [b]));");
				out.newLine();
				out.write("assume (forall x: seq<int>, y: seq<int> :: IS_NONDECREASING(x+y) ==> IS_NONDECREASING(x) && IS_NONDECREASING (y));");
				out.newLine();
				out.write("assume (forall x: seq<int>, y: seq<int>, z: seq<int> :: IS_NONDECREASING(x+y+z) ==> IS_NONDECREASING(x + z) && IS_NONDECREASING(y + z) && IS_NONDECREASING(x + y));");
				out.newLine();
				out.write("assume(forall a: seq<int>, b:seq<int>, x: int, y:int :: IS_NONDECREASING(a + [y]) && IS_NONDECREASING([y] + b) ==> IS_NONDECREASING(a + [y] + b));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, x:int, y:int :: IS_NONDECREASING(a + [x]) && IS_NONDECREASING([y] + b) && (x <= y) ==> IS_NONDECREASING(a + [x] + [y] + b));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, x:int, y:int :: IS_NONDECREASING(a + [x]) && IS_NONDECREASING([y] + b) && (x <= y) ==> IS_NONDECREASING(a + ([x] + ([y] + b))));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, x:int, y:int :: IS_NONDECREASING(a + [x]) && IS_NONDECREASING([y] + b) && (x <= y) ==> IS_NONDECREASING(((a + [x]) + [y]) + b));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, x:int, y:int :: IS_NONDECREASING(a + [x]) && IS_NONDECREASING([y] + b) && (x <= y) ==> IS_NONDECREASING((a + [x]) + ([y] + b)));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c: seq<int> :: IS_NONDECREASING(a + c) && IS_NONDECREASING(c + b) && c!= [] ==> IS_NONDECREASING(a + c + b));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c: seq<int> :: IS_NONDECREASING(a + c) && IS_NONDECREASING(c + b) && c!= [] ==> IS_NONDECREASING((a + c) + b));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c: seq<int> :: IS_NONDECREASING(a + c) && IS_NONDECREASING(c + b) && c!= [] ==> IS_NONDECREASING(a + (c + b)));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c: seq<int> :: IS_NONDECREASING(a + b +c) ==> IS_NONDECREASING(a + b) && IS_NONDECREASING(b + c) && IS_NONDECREASING(a + c));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c: seq<int> :: IS_NONDECREASING((a + b) +c) ==> IS_NONDECREASING(a + b) && IS_NONDECREASING(b + c) && IS_NONDECREASING(a + c));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c: seq<int> :: IS_NONDECREASING(a + (b +c)) ==> IS_NONDECREASING(a + b) && IS_NONDECREASING(b + c) && IS_NONDECREASING(a + c));");
				out.newLine();
				out.write("assume (forall a: seq<int>, x: int, y: int :: IS_NONDECREASING([x] + a + [y]) ==> (x <= y) );");
				out.newLine();
				out.write("assume(forall a: seq<int>, x: int, y: int :: (x <= y) && IS_NONDECREASING([y] + a) ==> IS_NONDECREASING([x] + a) );");
				out.newLine();
				out.newLine();
			}
			if (var_decld.contains("ARE_PERMUTATIONS")) {
				out.newLine();
				out.write("//definition of ARE_PERMUTATIONS");
				out.newLine();
				out.write("assume (forall s1: seq<int>, s2: seq<int> :: ARE_PERMUTATIONS(s1, s2) == (forall i: int :: OCCURS_COUNT(s1, i) == OCCURS_COUNT(s2, i))) ;  ");
				out.newLine();
				out.write("//ARE_PERMUTATIONS lemmas");
				out.newLine();
				out.write("assume (forall a:seq<int> , b: seq<int> :: a == b ==> ARE_PERMUTATIONS(a, b));");
				out.newLine();
				out.write("assume (forall a:seq<int> :: ARE_PERMUTATIONS(a, a));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c: seq<int> :: ARE_PERMUTATIONS(a, b) && ARE_PERMUTATIONS(b, c) ==> ARE_PERMUTATIONS(a, c));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c :seq<int> :: ARE_PERMUTATIONS((a + b) + c, a + (b + c)));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int>, c :seq<int> :: ARE_PERMUTATIONS(a + (b + c), b + (a + c))); ");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int> :: ARE_PERMUTATIONS(a, b) ==> ARE_PERMUTATIONS(b, a));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b:seq<int> :: ARE_PERMUTATIONS(a, b) ==>  |a| == |b|); ");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int> :: ARE_PERMUTATIONS(a + [] , b) ==> ARE_PERMUTATIONS(a , b));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int> :: ARE_PERMUTATIONS([] + a , b) ==> ARE_PERMUTATIONS(a , b)); ");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int> :: ARE_PERMUTATIONS(a , [] + b ) ==> ARE_PERMUTATIONS(a , b)); ");
				out.newLine();
				out.write("assume (forall a:seq<int>, b: seq<int> :: ARE_PERMUTATIONS(a , b + []) ==> ARE_PERMUTATIONS(a , b));");
				out.newLine();
				out.write("assume(forall a:seq<int>, b: seq<int>, c: seq<int>, d: seq<int>, e: seq<int>, f: seq<int>,g: seq<int>, h:seq<int>, i:seq<int> :: ARE_PERMUTATIONS((((a + (b + c)) + d) + e), (((f + g) + h) + i)) ==> ARE_PERMUTATIONS(((((a + e) + d) + c) + b), (((f + g) + h) + i)));");
				out.newLine();
				out.write("assume(forall a:seq<int>, b: seq<int>, c: seq<int>, d: seq<int>, e: seq<int> :: ARE_PERMUTATIONS((((a + (b + c)) + d) + e), ((c + (d + a)) + (e + b))));");
				out.newLine();
				out.write("assume(forall a:seq<int>, b: seq<int>, c: seq<int>, d: seq<int>, e: seq<int> :: ARE_PERMUTATIONS((((a + (b+ c)) + d) + e),((((a + b) + c) + d) +e)));");
				out.newLine();
				out.write("assume(forall a:seq<int>, b: seq<int>, c: seq<int>, d: seq<int>, e: seq<int> ::  ARE_PERMUTATIONS((((a + (b+ c)) +d) + e),((((a + e) + d) + c) +b)));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b:seq<int>, c:seq<int>, d:seq<int> :: ARE_PERMUTATIONS(a, (b+c)) && ARE_PERMUTATIONS(c, d)==> ARE_PERMUTATIONS(a, (b+ d)));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b:seq<int>, c:seq<int>, d:seq<int> :: ARE_PERMUTATIONS(a, (b+c)) && ARE_PERMUTATIONS(b, d)==> ARE_PERMUTATIONS(a, (d+ c)));");
				out.newLine();
				out.write("assume (forall a:seq<int>, b:seq<int>, c:seq<int>, d:seq<int> :: ARE_PERMUTATIONS(a, (b+c)) &&  ARE_PERMUTATIONS((b + c), d) ==> ARE_PERMUTATIONS(a, d));");
				out.newLine();
				out.newLine();
			}
		} else {
			if (var_decld.contains("ARE_IN_ORDER")) {
				out.newLine();
				out.write("//definition of ARE_IN_ORDER");
				out.newLine();
				out.write("assume (forall x, y  :: ARE_IN_ORDER(x, y) || ARE_IN_ORDER(y, x));");
				out.newLine();
				out.write("assume (forall x, y, z :: (ARE_IN_ORDER(x, y) && ARE_IN_ORDER(y, z) ==> ARE_IN_ORDER(x, z)));");
				out.newLine();
			}

			if (var_decld.contains("OCCURS_COUNT")) {
				out.newLine();
				out.write("//definition of OCCURS_COUNT");
				out.newLine();
				out.write("assume ( forall s:seq<T>, i :T :: if s == [] then OCCURS_COUNT(s, i) == 0 else (exists x: T, r: seq<T> :: s == [x] + r && (if x == i then OCCURS_COUNT(s, i) == OCCURS_COUNT(r, i) +1 else OCCURS_COUNT(s, i) == OCCURS_COUNT(r, i))));	");
				out.newLine();
			}
			if (var_decld.contains("PRECEDES")) {
				out.newLine();
				out.write("//definition of PRECEDES");
				out.newLine();
				out.write("assume (forall s1: seq<T>, s2: seq<T>:: PRECEDES(s1, s2) == (forall i, j :: (OCCURS_COUNT(s1, i) > 0 && OCCURS_COUNT(s2, j) > 0 ==> ARE_IN_ORDER(i, j)))) ;	");
				out.newLine();
				out.write("//PRECEDES lemmas");
				out.newLine();
				out.write("assume (forall x:seq<T> :: PRECEDES([], x));");
				out.newLine();
				out.write("assume (forall x:seq<T> :: PRECEDES(x, []));");
				out.newLine();
				out.write("assume (forall a: T, b: T :: ARE_IN_ORDER(a, b) ==> PRECEDES([a], [b]));");
				out.newLine();
				out.write("assume(forall x: seq<T>, a: T, b: T :: ARE_IN_ORDER(a, b) && PRECEDES(x, [b]) ==> PRECEDES(x + [a], [b]) );");
				out.newLine();
				out.newLine();
			}
			if (var_decld.contains("IS_NONDECREASING")) {
				out.newLine();
				out.write("//definition of IS_NONDECREASING");
				out.newLine();
				out.write("assume (forall s: seq<T>:: IS_NONDECREASING(s) == (forall a: seq<T> , b: seq<T> :: s == a + b ==> PRECEDES(a, b))); ");
				out.newLine();
				out.write("//IS_NONDECREASING lemmas ");
				out.newLine();
				out.write("assume (IS_NONDECREASING([]));");
				out.newLine();
				out.write("assume (forall x:T :: IS_NONDECREASING([x]));");
				out.newLine();
				out.write("assume (forall q: seq<T> :: |q| <= 1 ==> IS_NONDECREASING(q));");
				out.newLine();
				out.write("assume (forall x:seq<T> :: IS_NONDECREASING([]+x) <==> IS_NONDECREASING(x));");
				out.newLine();
				out.write("assume (forall x:seq<T> :: IS_NONDECREASING(x+[])  <==> IS_NONDECREASING(x));");
				out.newLine();
				out.write("assume (forall a: T, b: T :: ARE_IN_ORDER(a, b) ==> IS_NONDECREASING([a] + [b]));");
				out.newLine();
				out.write("assume (forall x: seq<T>, y: seq<T> :: IS_NONDECREASING(x+y) ==> IS_NONDECREASING(x) && IS_NONDECREASING (y));");
				out.newLine();
				out.write("assume (forall x: seq<T>, y: seq<T>, z: seq<T> :: IS_NONDECREASING(x+y+z) ==> IS_NONDECREASING(x + z) && IS_NONDECREASING(y + z) && IS_NONDECREASING(x + y));");
				out.newLine();
				out.write("assume(forall a: seq<T>, b:seq<T>, x: T, y:T :: IS_NONDECREASING(a + [y]) && IS_NONDECREASING([y] + b) ==> IS_NONDECREASING(a + [y] + b));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, x:T, y:T :: IS_NONDECREASING(a + [x]) && IS_NONDECREASING([y] + b) && ARE_IN_ORDER(x, y) ==> IS_NONDECREASING(a + [x] + [y] + b));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, x:T, y:T :: IS_NONDECREASING(a + [x]) && IS_NONDECREASING([y] + b) && ARE_IN_ORDER(x, y) ==> IS_NONDECREASING(a + ([x] + ([y] + b))));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, x:T, y:T :: IS_NONDECREASING(a + [x]) && IS_NONDECREASING([y] + b) && ARE_IN_ORDER(x, y) ==> IS_NONDECREASING(((a + [x]) + [y]) + b));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, x:T, y:T :: IS_NONDECREASING(a + [x]) && IS_NONDECREASING([y] + b) && ARE_IN_ORDER(x, y) ==> IS_NONDECREASING((a + [x]) + ([y] + b)));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: IS_NONDECREASING(a + c) && IS_NONDECREASING(c + b) && c!= [] ==> IS_NONDECREASING(a + c + b));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: IS_NONDECREASING(a + c) && IS_NONDECREASING(c + b) && c!= [] ==> IS_NONDECREASING((a + c) + b));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: IS_NONDECREASING(a + c) && IS_NONDECREASING(c + b) && c!= [] ==> IS_NONDECREASING(a + (c + b)));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: IS_NONDECREASING(a + b +c) ==> IS_NONDECREASING(a + b) && IS_NONDECREASING(b + c) && IS_NONDECREASING(a + c));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: IS_NONDECREASING((a + b) +c) ==> IS_NONDECREASING(a + b) && IS_NONDECREASING(b + c) && IS_NONDECREASING(a + c));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: IS_NONDECREASING(a + (b +c)) ==> IS_NONDECREASING(a + b) && IS_NONDECREASING(b + c) && IS_NONDECREASING(a + c));");
				out.newLine();
				out.write("assume (forall a: seq<T>, x: T, y: T :: IS_NONDECREASING([x] + a + [y]) ==> ARE_IN_ORDER(x, y) );");
				out.newLine();
				out.write("assume(forall a: seq<T>, x: T, y: T :: ARE_IN_ORDER(x, y) && IS_NONDECREASING([y] + a) ==> IS_NONDECREASING([x] + a) );");
				out.newLine();
				out.newLine();
			}
			if (var_decld.contains("ARE_PERMUTATIONS")) {
				out.newLine();
				out.write("//definition of ARE_PERMUTATIONS");
				out.newLine();
				out.write("assume (forall s1: seq<T>, s2: seq<T>:: ARE_PERMUTATIONS(s1, s2) == (forall i :: OCCURS_COUNT(s1, i) == OCCURS_COUNT(s2, i))) ;");
				out.newLine();
				out.write("//ARE_PERMUTATIONS lemmas");
				out.newLine();
				out.write("assume (forall a:seq<T> , b: seq<T> :: a == b ==> ARE_PERMUTATIONS(a, b));");
				out.newLine();
				out.write("assume (forall a:seq<T> :: ARE_PERMUTATIONS(a, a));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c: seq<T> :: ARE_PERMUTATIONS(a, b) && ARE_PERMUTATIONS(b, c) ==> ARE_PERMUTATIONS(a, c));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c :seq<T> :: ARE_PERMUTATIONS((a + b) + c, a + (b + c)));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T>, c :seq<T> :: ARE_PERMUTATIONS(a + (b + c), b + (a + c)));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T> :: ARE_PERMUTATIONS(a, b) ==> ARE_PERMUTATIONS(b, a));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b:seq<T> :: ARE_PERMUTATIONS(a, b) ==>  |a| == |b|);");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T> :: ARE_PERMUTATIONS(a + [] , b) ==> ARE_PERMUTATIONS(a , b));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T> :: ARE_PERMUTATIONS([] + a , b) ==> ARE_PERMUTATIONS(a , b)); ");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T> :: ARE_PERMUTATIONS(a , [] + b ) ==> ARE_PERMUTATIONS(a , b)); ");
				out.newLine();
				out.write("assume (forall a:seq<T>, b: seq<T> :: ARE_PERMUTATIONS(a , b + [])==> ARE_PERMUTATIONS(a , b));");
				out.newLine();
				out.write("assume(forall a:seq<T>, b: seq<T>, c: seq<T>, d: seq<T>, e: seq<T>, f: seq<T>,g: seq<T>, h:seq<T>, i:seq<T> :: ARE_PERMUTATIONS((((a + (b + c)) + d) + e), (((f + g) + h) + i)) ==> ARE_PERMUTATIONS(((((a + e) + d) + c) + b), (((f + g) + h) + i)));");
				out.newLine();
				out.write(" assume(forall a:seq<T>, b: seq<T>, c: seq<T>, d: seq<T>, e: seq<T> :: ARE_PERMUTATIONS((((a + (b + c)) + d) + e), ((c + (d + a)) + (e + b))));");
				out.newLine();
				out.write(" assume(forall a:seq<T>, b: seq<T>, c: seq<T>, d: seq<T>, e: seq<T> :: ARE_PERMUTATIONS((((a + (b+ c)) + d) + e),((((a + b) + c) + d) +e)));");
				out.newLine();
				out.write(" assume(forall a:seq<T>, b: seq<T>, c: seq<T>, d: seq<T>, e: seq<T> ::  ARE_PERMUTATIONS((((a + (b+ c)) +d) + e),((((a + e) + d) + c) +b)));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b:seq<T>, c:seq<T>, d:seq<T> :: ARE_PERMUTATIONS(a, (b+c)) && ARE_PERMUTATIONS(c, d)==> ARE_PERMUTATIONS(a, (b+ d)));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b:seq<T>, c:seq<T>, d:seq<T> :: ARE_PERMUTATIONS(a, (b+c)) && ARE_PERMUTATIONS(b, d)==> ARE_PERMUTATIONS(a, (d+ c)));");
				out.newLine();
				out.write("assume (forall a:seq<T>, b:seq<T>, c:seq<T>, d:seq<T> :: ARE_PERMUTATIONS(a, (b+c)) &&  ARE_PERMUTATIONS((b + c), d) ==> ARE_PERMUTATIONS(a, d));");
				out.newLine();
				out.newLine();
			}
		}// end else

		if (var_decld.contains("IS_PRECEDING")) {
			out.newLine();
			out.write("assume (forall x: set<T> :: IS_PRECEDING({}, x));");
			out.newLine();
			out.write("	assume (forall x: set<T> :: IS_PRECEDING(x, {}));");
			out.newLine();
			out.write("assume (forall x: set<T>, y: set<T> :: IS_PRECEDING(x, y) || IS_PRECEDING(y, x));");
			out.newLine();
			out.write("assume (forall x:set<T>, y: set<T>, s: set<T>, t:set<T> :: (y == t + s && IS_PRECEDING(x, y)) ==> (IS_PRECEDING(x, t) && IS_PRECEDING(y, t)));");
			out.newLine();
			out.write("assume (forall x:set<T>, y: set<T>, s: set<T>, t:set<T> :: (x == t + s && IS_PRECEDING(x, y)) ==> (IS_PRECEDING(t, y) && IS_PRECEDING(s, y)));");
			out.newLine();
		}
		if (var_decld.contains("elements")) {
			out.write("	assume (forall s:seq<T> :: if s ==[] then elements(s) == {} else (exists t: seq<T>, x: T :: s == [x] + t && elements(s) == {x} + elements(t)));");
			out.newLine();
		}

		if (var_decld.contains("IS_ODD")) {
			out.write("//is_odd definition");
			out.write("assume (forall n :: IS_ODD(n) ==> (exists k :: n == 2*k + 1)) ;");
			out.newLine();
			out.write("//  is_odd lemmas");
			out.write("assume (forall k :: IS_ODD(k) != IS_ODD(k+1));");
			out.newLine();
			out.write("assume (forall k :: k>=0 && IS_ODD(k) ==> k>0);");
			out.newLine();
			out.write("assume (forall k :: k>0 && !IS_ODD(k) ==> k>1);");
			out.newLine();
			out.newLine();
		}
		if (var_decld.contains("SUBSTRING_REPLACEMENT")) {
			out.write("// substring_replacement definition");
			out.newLine();
			out.write("assume ( forall s: seq<T>, ss: seq<T>, start: int, finish: int :: (start <0 || start > finish || finish > |s|) ==> SUBSTRING_REPLACEMENT(s, ss, start, finish) == s &&(!(start <0 || start > finish || finish > |s|) ==> (exists a:seq<T>, b:seq<T>, c:seq<T> :: s == a + b + c && |a| == start && |c| == |s| - finish && SUBSTRING_REPLACEMENT(s, ss, start, finish) == a + ss + c)));");
			out.newLine();
		}

		if (var_decld.contains("DIFFER_BY_ONE")
				&& var_decld.contains("SAME_EXCEPT_AT")
				&& var_decld.contains("SUBSTRING_REPLACEMENT")) {
			out.newLine();
			out.write("//differ_by_one definition");
			out.newLine();
			out.write("assume ( forall t1: seq<T>, t2: seq<T>, pos: int, ch: T :: (DIFFER_BY_ONE(t1, t2, pos, ch)) == (exists a: seq<T>, b : seq<T> :: t1 == (a + b) && t2 == (a + [ch] + b) && |a| == pos));");
			out.newLine();
			out.write("// same_except_at definition");
			out.newLine();
			out.write("assume ( forall t1: seq<T>, t2: seq<T>, pos: int, x: T , y: T:: (SAME_EXCEPT_AT(t1, t2, pos, x, y)) == (exists a: seq<T>, b : seq<T> :: t1 == (a + [x] + b) && t2 == (a + [y] + b) && |a| == pos)); ");
			out.newLine();

			out.write("//differ_by_one lemmas");
			out.newLine();
			out.write("assume (forall s1: seq<T>, s2: seq<T>, pos: int, x: T :: DIFFER_BY_ONE(s1, s2, pos, x) && 0 <= pos && pos < |s2| ==> |s1| == |s2| - 1);");
			out.newLine();
			out.write("assume (forall s1: seq<T>, s2: seq<T>, x: T :: DIFFER_BY_ONE(s1, s2, 0, x) && |s2| > 0 ==> (s2 == [x] + s1));");
			out.newLine();
			out.write("assume (forall s1: seq<T>, s2: seq<T>, x: T :: DIFFER_BY_ONE(s1, s2, |s1|, x) ==> (s1 + [x] == s2));");
			out.newLine();
			out.write("assume (forall s1: seq<T>, s2: seq<T>, x: T :: DIFFER_BY_ONE(s1, s2, |s2|-1 , x)  ==> (s1 + [x] == s2));");
			out.newLine();
			out.write("assume (forall s1: seq<T>, s2: seq<T>, x: T :: DIFFER_BY_ONE( (s1 + s2), (s1 + [x] + s2), |s1|, x)) ;");
			out.newLine();
			out.write("assume (forall s1: seq<T>, s2: seq<T>, x: T , y: T :: SAME_EXCEPT_AT( (s1 + [x] + s2), (s1 + [y] + s2), |s1|, x, y));");
			out.newLine();
			out.write("assume (forall s: seq<T>, start: int :: s == SUBSTRING_REPLACEMENT(s, [], start, start));");
			out.newLine();
			out.write("assume (forall s1: seq<T>, s2: seq<T>, start: int, finish: int, x: T :: DIFFER_BY_ONE(s1, s2, start, x) ==> (SUBSTRING_REPLACEMENT(s1, [], start, (finish - 1)) == SUBSTRING_REPLACEMENT(s2, [], start, finish)));");
			out.newLine();
			out.newLine();
		}

		if (var_decld.contains("SUBSTRING")) {
			out.newLine();
			out.write("//substring definition");
			out.newLine();
			out.write("assume ( forall s: seq<T>, start : int, finish: int ::(start < 0 || start > finish || finish > |s| ==> SUBSTRING(s, start, finish) == []) && (!(start < 0 || start > finish || finish > |s|) ==> (exists a:seq<T>, b:seq<T> :: s == a + SUBSTRING(s, start, finish) + b && |a| == start&& |b| == |s| - finish)));");
			out.newLine();
			out.write("//substring lemmas");
			out.newLine();
			out.write("assume (forall s: seq<T>, start: int, finish: int :: start < 0 || start > finish || finish > |s| ==> SUBSTRING(s, start, finish) == []);");
			out.newLine();
			out.write("assume (forall s: seq<T>, start: int, finish: int :: |s| == 0 ==> SUBSTRING(s, start, finish) == []);");
			out.newLine();
			out.write("assume (forall m: int, n: int, a: seq<T> :: 0 <= m && m <= n && n <= |a| ==> SUBSTRING(a, 0, m) + SUBSTRING(a, m, n) == SUBSTRING(a, 0, n));");
			out.newLine();
			out.write("assume (forall m: int, n: int, a: seq<T> :: 0 <= m && m <= n && n <= |a| ==> SUBSTRING(a, 0, m) + SUBSTRING(a, m, n) + SUBSTRING(a, n, |a|) == a);");
			out.newLine();
			out.write("assume (forall j: int, k: int, s1: seq<T>, s2: seq<T> :: 0 <= j && j <= k && k <= |s1| ==> SUBSTRING(s1 + s2, j, k) == SUBSTRING(s1, j, k));");
			out.newLine();
			out.write("assume (forall j: int, k: int, s1: seq<T>, s2: seq<T> :: |s1| <= j && j <= k && k <= (|s1| + |s2|) ==> SUBSTRING(s1 + s2, j, k) == SUBSTRING(s2, j - |s1|, k - |s1|));");
			out.newLine();
			out.newLine();
		}

		if (var_decld.contains("FUNCTION")) {
			out.newLine();
			out.write("assume (FUNCTION({}) == {});");
			out.newLine();
			out.write("assume (forall s:set<T>, t: set<T> :: FUNCTION(s + t) == FUNCTION(s) + FUNCTION(t));");
			out.newLine();
		}
		if (var_decld.contains("HAS_ONLY_DIGITS")) {
			out.newLine();
			out.write("//definition of HAS_ONLY_DIGITS");
			out.newLine();
			out.write("assume ( forall digits: seq<int>, radix: int :: (HAS_ONLY_DIGITS(digits, radix)) == (forall d: int, r : seq<int> :: digits == [d] + r && 0 <= d && d < radix && HAS_ONLY_DIGITS(r, radix))); ");
			out.newLine();
		}

		if (var_decld.contains("NUMERICAL_VALUE")) {
			out.newLine();
			out.write("//definition of NUMERICAL_VALUE");
			out.newLine();
			out.write("assume ( forall s:seq<int>, r :int :: if s == [] then NUMERICAL_VALUE(s, r) == 0 else (forall k: seq<int>, d: int :: s == k + [d] && NUMERICAL_VALUE(s, r) == NUMERICAL_VALUE(k, r) * r + d));");
			out.newLine();
			out.write("//lemmas");
			out.newLine();
			out.write("assume (forall radix: int:: radix>1 ==>  NUMERICAL_VALUE([], radix) == 0);");
			out.newLine();
			out.write("assume (forall digits : seq<int>, radix: int, x: int :: radix>1 && x >= 0 && x < radix ==>  (((NUMERICAL_VALUE(digits, radix) * radix) + x) == NUMERICAL_VALUE(digits+[x], radix))) ;");
			out.newLine();
			out.newLine();
		}

		if (var_decld.contains("IS_WELL_FORMED_FUNCTION")) {
			out.newLine();
			out.write("//definition of IS_WELL_FORMED_FUNCTION");
			out.newLine();
			out.write("assume ( forall digits: seq<int>, radix: int :: (IS_WELL_FORMED_FUNCTION(digits, radix)) == (forall d: int, r : seq<int> :: digits == [d] + r && 0 < d && d < radix && HAS_ONLY_DIGITS(r, radix))); ");
			out.newLine();
			out.write("//lemmas");
			out.newLine();
			out.write("assume (forall radix: int:: radix>1 ==> IS_WELL_FORMED_FUNCTION([], radix));");
			out.newLine();
			out.write("assume (forall digits : seq<int>, radix: int, x: int :: radix>1 && x >= 0 && x < radix && IS_WELL_FORMED_FUNCTION(digits, radix) ==> IS_WELL_FORMED_FUNCTION(digits+[x], radix)) ;");
			out.newLine();
		}
	}
	
	public void declare_global(Node node, Document doc) throws Exception
	{
		out.newLine();
		out.write("var s := [");
		
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
		out.newLine();
	}
	
	

	public void declare_global_1(Node node, Document doc) throws Exception
	{
		
		out.newLine();
		out.write("var s1 := [");
		
		NodeList ele = node.getLastChild().getChildNodes();
		int numchildren = node.getLastChild().getChildNodes().getLength();
		System.out.println("numchildren: "+numchildren);
		
		if(numchildren >0)
		{
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
			
		out.write("];");
		out.newLine();
	}
	
	public void declare_global_2(Node node, Document doc) throws Exception
	{
		
		out.newLine();
		out.write("var s2 := [");
		
		NodeList ele = node.getLastChild().getChildNodes();
		int numchildren = node.getLastChild().getChildNodes().getLength();
		System.out.println("numchildren: "+numchildren);
		
		if(numchildren >0)
		{
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
			
		out.write("];");
		out.newLine();
	}
}
