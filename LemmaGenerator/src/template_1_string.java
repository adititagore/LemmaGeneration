
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//This template generates lemmas for the function signature F(s:string(object)) :boolean
//in particular IS_NONDECREASING
public class template_1_string {
	static int star_pos = 2;
	static int star_pos2 = 2, star_pos3 = 2;
	static int and_pos = 2;
	static boolean all_ands = true;
	static String arr1[] = { "ab", "bc", "ac" };

	public static void t1() {
		String arr[] = { "a", "b", "c" };

		for (int a = 0; a < 136; a++) {
			try {

				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				// root elements
				Document doc = docBuilder.newDocument();
				Node body = setUp(doc, a);

				if (a < 2) {
					first_2_lemmas(doc, a, body);
				}

				// lemmas 12 to 14
				else if (a >= 2 && a < 10) {
					lemmas_a_implies_b(doc, a, body, arr);
				}// end if a>=2

				else if (a >= 10 && a <= 72) {

					lemmas_a_andor_b_implies_c(doc, a, body, arr);
				}

				else if (a > 72) {
					lemmas_a_implies_b_andor_c(doc, a, body, arr);
				}

				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result;

				if (a >= 0 && a <= 9)
					result = new StreamResult(new File(
							"C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\template1_file_00"
									+ a + ".xml"));
				else if (a > 9 && a <= 99)
					result = new StreamResult(new File(
							"C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\template1_file_0"
									+ a + ".xml"));
				else
					result = new StreamResult(new File(
							"C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\template1_file_"
									+ a + ".xml"));

				// Output to console for testing
				// StreamResult result1 = new StreamResult(System.out);
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(source, result);

				System.out.println("File saved -- The file number is " + a);

			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
			}
		}// end for -- "a" loop

	}

	public static void first_2_lemmas(Document doc, int a, Node body) {
		Element function = doc.createElement("function");
		function.setAttribute("name", "IS_NONDECREASING");
		function.setAttribute("type", "boolean");
		body.appendChild(function);

		Element arg = doc.createElement("arg");
		function.appendChild(arg);

		if (a == 0) {
			Element empty = doc.createElement("emptystring");
			arg.appendChild(empty);
		} else {
			Element stringle = doc.createElement("stringleton");
			stringle.setAttribute("type", "string(object)");
			arg.appendChild(stringle);

			Element symbol = doc.createElement("symbol");
			stringle.appendChild(symbol);
			symbol.setAttribute("type", "string(object)");
			symbol.appendChild(doc.createTextNode("a"));

		}

	}

	public static void lemmas_a_implies_b(Document doc, int a, Node body,
			String arr[]) {
		int z = a - 2;

		// firstname elements
		Element implies = doc.createElement("implies");
		implies.setAttribute("type", "boolean");
		body.appendChild(implies);

		for (int i = 0; i < 2; i++) {
			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			implies.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			if (z == 0) {
				Element star = doc.createElement("star");
				star.setAttribute("type", "string(object)");
				arg.appendChild(star);

				// create the combinations for lemma 2
				for (int k = 0; k < 2; k++) {
					Element symbol = doc.createElement("symbol");
					symbol.setAttribute("type", "string(object)");
					star.appendChild(symbol);

					if (i == k) {
						symbol.appendChild(doc.createTextNode("a"));
					} else {
						symbol.appendChild(doc.createTextNode("b"));
					}
				}// end for k
			}// end if(a <1)

			else if (z == 1) {
				if (i == 0) {
					Element star = doc.createElement("star");
					star.setAttribute("type", "string(object)");
					arg.appendChild(star);

					Element ele_a = doc.createElement("symbol");
					ele_a.setAttribute("type", "string(object)");
					star.appendChild(ele_a);
					ele_a.appendChild(doc.createTextNode("a"));

					Element inner_star = doc.createElement("star");
					inner_star.setAttribute("type", "string(object)");
					star.appendChild(inner_star);

					Element ele_b = doc.createElement("symbol");
					ele_b.setAttribute("type", "string(object)");
					inner_star.appendChild(ele_b);
					ele_b.appendChild(doc.createTextNode("b"));

					Element ele_c = doc.createElement("symbol");
					ele_c.setAttribute("type", "string(object)");
					inner_star.appendChild(ele_c);
					ele_c.appendChild(doc.createTextNode("c"));

				}

				else if (i == 1) {
					Element star = doc.createElement("star");
					star.setAttribute("type", "string(object)");
					arg.appendChild(star);

					Element inner_star = doc.createElement("star");
					inner_star.setAttribute("type", "string(object)");
					star.appendChild(inner_star);

					Element ele_a = doc.createElement("symbol");
					ele_a.setAttribute("type", "string(object)");
					inner_star.appendChild(ele_a);
					ele_a.appendChild(doc.createTextNode("a"));

					Element ele_b = doc.createElement("symbol");
					ele_b.setAttribute("type", "string(object)");
					inner_star.appendChild(ele_b);
					ele_b.appendChild(doc.createTextNode("b"));

					Element ele_c = doc.createElement("symbol");
					ele_c.setAttribute("type", "string(object)");
					star.appendChild(ele_c);
					ele_c.appendChild(doc.createTextNode("c"));
				}
			}
			// cutting from 12 lemmas to 6 since lemmas 4,5,6 were repitions
			// make 12 combinations - commutative and associative -- lemmas 3 to
			// 14
			else if (z >= 2 && z < 8) {
				Element star = doc.createElement("star");
				star.setAttribute("type", "string(object)");
				arg.appendChild(star);

				if (z == 5 && star_pos == 2)
					star_pos++;

				// for the 1st 3 lemmas of the form (a*b) *c
				if (star_pos == 2) {
					Element inner_star = doc.createElement("star");
					inner_star.setAttribute("type", "string(object)");
					star.appendChild(inner_star);

					String arr0 = arr[0];
					String arr1 = arr[1];
					Element ar0 = doc.createElement("symbol");
					ar0.setAttribute("type", "string(object)");
					inner_star.appendChild(ar0);
					if (a % 2 == 1)
						ar0.appendChild(doc.createTextNode(arr0));
					else
						ar0.appendChild(doc.createTextNode(arr1));

					Element ar1 = doc.createElement("symbol");
					ar1.setAttribute("type", "string(object)");
					inner_star.appendChild(ar1);
					if (a % 2 == 1)
						ar1.appendChild(doc.createTextNode(arr1));
					else
						ar1.appendChild(doc.createTextNode(arr0));

					String arr2 = arr[2];
					Element ar2 = doc.createElement("symbol");
					ar2.setAttribute("type", "string(object)");
					star.appendChild(ar2);
					ar2.appendChild(doc.createTextNode(arr2));

				}// end if(star_pos == 2)

				// for the next 6 lemmas of the form a*(b*c)
				else if (star_pos == 3) {
					String arr0 = arr[0];
					Element ar0 = doc.createElement("symbol");
					ar0.setAttribute("type", "string(object)");
					star.appendChild(ar0);
					ar0.appendChild(doc.createTextNode(arr0));

					Element inner_star = doc.createElement("star");
					inner_star.setAttribute("type", "string(object)");
					star.appendChild(inner_star);

					String arr2 = arr[2];
					String arr1 = arr[1];
					Element ar2 = doc.createElement("symbol");
					ar2.setAttribute("type", "string(object)");
					inner_star.appendChild(ar2);
					if (a % 2 == 1)
						ar2.appendChild(doc.createTextNode(arr2));
					else
						ar2.appendChild(doc.createTextNode(arr1));

					Element ar1 = doc.createElement("symbol");
					ar1.setAttribute("type", "string(object)");
					inner_star.appendChild(ar1);
					if (a % 2 == 1)
						ar1.appendChild(doc.createTextNode(arr1));
					else
						ar1.appendChild(doc.createTextNode(arr2));

				}// end if(star_pos == 3)

				// shift the array only after constructing the 1st argument so
				// that the
				// same combination is repeated for the LHS of the next lemma
				if (i == 0) {
					shift(arr);
				}
			}// end else if(a >=1 && a < 13)
		}// end for i

	}

	public static void lemmas_a_andor_b_implies_c(Document doc, int a,
			Node body, String[] arr) {

		int z = a - 10;

		Element implies = doc.createElement("implies");
		implies.setAttribute("type", "boolean");
		body.appendChild(implies);

		// creating the LHS

		if (z < 2) {
			Element and_or;
			if (z == 0) {
				and_or = doc.createElement("and");
				and_or.setAttribute("type", "boolean");
				implies.appendChild(and_or);
			}

			else {
				and_or = doc.createElement("or");
				and_or.setAttribute("type", "boolean");
				implies.appendChild(and_or);
			}

			String ele1 = arr1[0];
			String ele2 = arr1[1];
			String ele3 = arr1[2];

			for (int k = 0; k < 2; k++) {
				Element function = doc.createElement("function");
				function.setAttribute("name", "IS_NONDECREASING");
				function.setAttribute("type", "boolean");
				and_or.appendChild(function);

				Element arg = doc.createElement("arg");
				function.appendChild(arg);

				Element star = doc.createElement("star");
				star.setAttribute("type", "string(object)");
				arg.appendChild(star);

				Element e1 = doc.createElement("symbol");
				e1.setAttribute("type", "string(object)");
				star.appendChild(e1);

				Element e2 = doc.createElement("symbol");
				e2.setAttribute("type", "string(object)");
				star.appendChild(e2);

				if (k == 0) {
					e1.appendChild(doc.createTextNode(ele1.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele1.substring(1, 2)));
				}

				if (k == 1) {
					e1.appendChild(doc.createTextNode(ele2.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele2.substring(1, 2)));
				}
			}// end for k

			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			implies.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			Element star = doc.createElement("star");
			star.setAttribute("type", "string(object)");
			arg.appendChild(star);

			Element e1 = doc.createElement("symbol");
			e1.setAttribute("type", "string(object)");
			star.appendChild(e1);
			e1.appendChild(doc.createTextNode(ele3.substring(0, 1)));

			Element e2 = doc.createElement("symbol");
			e2.setAttribute("type", "string(object)");
			star.appendChild(e2);
			e2.appendChild(doc.createTextNode(ele3.substring(1, 2)));
		}// if Z<2

		Element and_or;
		if (z >= 2 && z < 39) {
			Element big_or = doc.createElement("or");
			big_or.setAttribute("type", "boolean");
			implies.appendChild(big_or);

			Element and = doc.createElement("and");
			and.setAttribute("type", "boolean");
			big_or.appendChild(and);

			String ele1 = arr1[0];
			String ele2 = arr1[1];
			String ele3 = arr1[2];

			for (int k = 0; k < 2; k++) {
				Element function = doc.createElement("function");
				function.setAttribute("name", "IS_NONDECREASING");
				function.setAttribute("type", "boolean");
				and.appendChild(function);

				Element arg = doc.createElement("arg");
				function.appendChild(arg);

				Element star = doc.createElement("star");
				star.setAttribute("type", "string(object)");
				arg.appendChild(star);

				Element e1 = doc.createElement("symbol");
				e1.setAttribute("type", "string(object)");
				star.appendChild(e1);

				Element e2 = doc.createElement("symbol");
				e2.setAttribute("type", "string(object)");
				star.appendChild(e2);

				if (k == 0) {
					e1.appendChild(doc.createTextNode(ele1.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele1.substring(1, 2)));
				}

				if (k == 1) {
					e1.appendChild(doc.createTextNode(ele2.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele2.substring(1, 2)));
				}
			}

			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			big_or.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			Element star = doc.createElement("star");
			star.setAttribute("type", "string(object)");
			arg.appendChild(star);

			Element e1 = doc.createElement("symbol");
			e1.setAttribute("type", "string(object)");
			star.appendChild(e1);

			Element e2 = doc.createElement("symbol");
			e2.setAttribute("type", "string(object)");
			star.appendChild(e2);

			e1.appendChild(doc.createTextNode(ele3.substring(0, 1)));
			e2.appendChild(doc.createTextNode(ele3.substring(1, 2)));

			if (z == 14 || z == 26) {
				shift(arr1);
			}
		}// end if z>=2 and z<39

		else if (z >= 39) {
			Element big_and_or;
			if (z < 51) {
				big_and_or = doc.createElement("and");
				big_and_or.setAttribute("type", "boolean");
				implies.appendChild(big_and_or);

				and_or = doc.createElement("and");
				and_or.setAttribute("type", "boolean");
				big_and_or.appendChild(and_or);
			} else {
				big_and_or = doc.createElement("or");
				big_and_or.setAttribute("type", "boolean");
				implies.appendChild(big_and_or);

				and_or = doc.createElement("or");
				and_or.setAttribute("type", "boolean");
				big_and_or.appendChild(and_or);
			}
			String ele1 = arr1[0];
			String ele2 = arr1[1];
			String ele3 = arr1[2];

			for (int k = 0; k < 2; k++) {
				Element function = doc.createElement("function");
				function.setAttribute("name", "IS_NONDECREASING");
				function.setAttribute("type", "boolean");
				and_or.appendChild(function);

				Element arg = doc.createElement("arg");
				function.appendChild(arg);

				Element star = doc.createElement("star");
				star.setAttribute("type", "string(object)");
				arg.appendChild(star);

				Element e1 = doc.createElement("symbol");
				e1.setAttribute("type", "string(object)");
				star.appendChild(e1);

				Element e2 = doc.createElement("symbol");
				e2.setAttribute("type", "string(object)");
				star.appendChild(e2);

				if (k == 0) {
					e1.appendChild(doc.createTextNode(ele1.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele1.substring(1, 2)));
				}

				if (k == 1) {
					e1.appendChild(doc.createTextNode(ele2.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele2.substring(1, 2)));
				}
			}

			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			big_and_or.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			Element star = doc.createElement("star");
			star.setAttribute("type", "string(object)");
			arg.appendChild(star);

			Element e1 = doc.createElement("symbol");
			e1.setAttribute("type", "string(object)");
			star.appendChild(e1);

			Element e2 = doc.createElement("symbol");
			e2.setAttribute("type", "string(object)");
			star.appendChild(e2);

			e1.appendChild(doc.createTextNode(ele3.substring(0, 1)));
			e2.appendChild(doc.createTextNode(ele3.substring(1, 2)));

		}

		// now build the RHS

		if (z >= 2) {
			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			implies.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			Element star = doc.createElement("star");
			star.setAttribute("type", "string(object)");
			arg.appendChild(star);

			if (z == 8 || z == 20 || z == 33 || z == 45 || z == 57) {
				star_pos2++;
				// System.out.println("star pos2 is "+ star_pos2 +
				// " and a is "+a);
			} else if (z == 14 || z == 26 || z == 39 || z == 51 || z == 63 ) {
				star_pos2--;
			}

			// for the 1st 6 lemmas of the form (a*b) *c
			if (star_pos2 == 2) {
				Element inner_star = doc.createElement("star");
				inner_star.setAttribute("type", "string(object)");
				star.appendChild(inner_star);

				String arr0 = arr[0];
				String arr1 = arr[1];
				Element ar0 = doc.createElement("symbol");
				ar0.setAttribute("type", "string(object)");
				inner_star.appendChild(ar0);
				if (a % 2 == 1)
					ar0.appendChild(doc.createTextNode(arr0));
				else
					ar0.appendChild(doc.createTextNode(arr1));

				Element ar1 = doc.createElement("symbol");
				ar1.setAttribute("type", "string(object)");
				inner_star.appendChild(ar1);
				if (a % 2 == 1)
					ar1.appendChild(doc.createTextNode(arr1));
				else
					ar1.appendChild(doc.createTextNode(arr0));

				String arr2 = arr[2];
				Element ar2 = doc.createElement("symbol");
				ar2.setAttribute("type", "string(object)");
				star.appendChild(ar2);
				ar2.appendChild(doc.createTextNode(arr2));

			}// end if(star_pos == 2)
			// for the next 6 lemmas of the form a*(b*c)
			else if (star_pos2 == 3) {
				String arr0 = arr[0];
				Element ar0 = doc.createElement("symbol");
				ar0.setAttribute("type", "string(object)");
				star.appendChild(ar0);
				ar0.appendChild(doc.createTextNode(arr0));

				Element inner_star = doc.createElement("star");
				inner_star.setAttribute("type", "string(object)");
				star.appendChild(inner_star);

				String arr2 = arr[2];
				String arr1 = arr[1];
				Element ar2 = doc.createElement("symbol");
				ar2.setAttribute("type", "string(object)");
				inner_star.appendChild(ar2);
				if (a % 2 == 1)
					ar2.appendChild(doc.createTextNode(arr2));
				else
					ar2.appendChild(doc.createTextNode(arr1));

				Element ar1 = doc.createElement("symbol");
				ar1.setAttribute("type", "string(object)");
				inner_star.appendChild(ar1);
				if (a % 2 == 1)
					ar1.appendChild(doc.createTextNode(arr1));
				else
					ar1.appendChild(doc.createTextNode(arr2));

			}// end if(star_pos == 3)

			shift(arr);
		}// if(z>=2)
	}

	public static void lemmas_a_implies_b_andor_c(Document doc, int a,
			Node body, String[] arr) {

		int z = a - 73;

		Element implies = doc.createElement("implies");
		implies.setAttribute("type", "boolean");
		body.appendChild(implies);

		// now build the LHS
		if (z >= 2) {
			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			implies.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			Element star = doc.createElement("star");
			star.setAttribute("type", "string(object)");
			arg.appendChild(star);

			if (z == 8 || z == 20 || z == 33 || z == 45 || z == 57) {
				star_pos3++;
				// System.out.println("star pos2 is "+ star_pos3 +
				// " and a is "+a);
			} else if (z == 14 || z == 26 || z == 39 || z == 51 || z == 63 ) {
				star_pos3--;
			}

			// for the 1st 6 lemmas of the form (a*b) *c
			if (star_pos3 == 2) {
				Element inner_star = doc.createElement("star");
				inner_star.setAttribute("type", "string(object)");
				star.appendChild(inner_star);

				String arr0 = arr[0];
				String arr1 = arr[1];
				Element ar0 = doc.createElement("symbol");
				ar0.setAttribute("type", "string(object)");
				inner_star.appendChild(ar0);
				if (a % 2 == 1)
					ar0.appendChild(doc.createTextNode(arr0));
				else
					ar0.appendChild(doc.createTextNode(arr1));

				Element ar1 = doc.createElement("symbol");
				ar1.setAttribute("type", "string(object)");
				inner_star.appendChild(ar1);
				if (a % 2 == 1)
					ar1.appendChild(doc.createTextNode(arr1));
				else
					ar1.appendChild(doc.createTextNode(arr0));

				String arr2 = arr[2];
				Element ar2 = doc.createElement("symbol");
				ar2.setAttribute("type", "string(object)");
				star.appendChild(ar2);
				ar2.appendChild(doc.createTextNode(arr2));

			}// end if(star_pos == 2)
			// for the next 6 lemmas of the form a*(b*c)
			else if (star_pos3 == 3) {
				String arr0 = arr[0];
				Element ar0 = doc.createElement("symbol");
				ar0.setAttribute("type", "string(object)");
				star.appendChild(ar0);
				ar0.appendChild(doc.createTextNode(arr0));

				Element inner_star = doc.createElement("star");
				inner_star.setAttribute("type", "string(object)");
				star.appendChild(inner_star);

				String arr2 = arr[2];
				String arr1 = arr[1];
				Element ar2 = doc.createElement("symbol");
				ar2.setAttribute("type", "string(object)");
				inner_star.appendChild(ar2);
				if (a % 2 == 1)
					ar2.appendChild(doc.createTextNode(arr2));
				else
					ar2.appendChild(doc.createTextNode(arr1));

				Element ar1 = doc.createElement("symbol");
				ar1.setAttribute("type", "string(object)");
				inner_star.appendChild(ar1);
				if (a % 2 == 1)
					ar1.appendChild(doc.createTextNode(arr1));
				else
					ar1.appendChild(doc.createTextNode(arr2));

			}// end if(star_pos == 3)

			shift(arr);
		}// if(z>=2)

		// creating the RHS

		if (z < 2) {
			Element and_or;
			if (z == 0) {
				and_or = doc.createElement("and");
				and_or.setAttribute("type", "boolean");
				implies.appendChild(and_or);
			}

			else {
				and_or = doc.createElement("or");
				and_or.setAttribute("type", "boolean");
				implies.appendChild(and_or);
			}

			String ele1 = arr1[0];
			String ele2 = arr1[1];
			String ele3 = arr1[2];

			for (int k = 0; k < 2; k++) {
				Element function = doc.createElement("function");
				function.setAttribute("name", "IS_NONDECREASING");
				function.setAttribute("type", "boolean");
				and_or.appendChild(function);

				Element arg = doc.createElement("arg");
				function.appendChild(arg);

				Element star = doc.createElement("star");
				star.setAttribute("type", "string(object)");
				arg.appendChild(star);

				Element e1 = doc.createElement("symbol");
				e1.setAttribute("type", "string(object)");
				star.appendChild(e1);

				Element e2 = doc.createElement("symbol");
				e2.setAttribute("type", "string(object)");
				star.appendChild(e2);

				if (k == 0) {
					e1.appendChild(doc.createTextNode(ele1.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele1.substring(1, 2)));
				}

				if (k == 1) {
					e1.appendChild(doc.createTextNode(ele2.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele2.substring(1, 2)));
				}
			}// end for k

			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			implies.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			Element star = doc.createElement("star");
			star.setAttribute("type", "string(object)");
			arg.appendChild(star);

			Element e1 = doc.createElement("symbol");
			e1.setAttribute("type", "string(object)");
			star.appendChild(e1);
			e1.appendChild(doc.createTextNode(ele3.substring(0, 1)));

			Element e2 = doc.createElement("symbol");
			e2.setAttribute("type", "string(object)");
			star.appendChild(e2);
			e2.appendChild(doc.createTextNode(ele3.substring(1, 2)));
		}// if Z<2

		Element and_or;
		if (z >= 2 && z < 39) {
			Element big_or = doc.createElement("or");
			big_or.setAttribute("type", "boolean");
			implies.appendChild(big_or);

			Element and = doc.createElement("and");
			and.setAttribute("type", "boolean");
			big_or.appendChild(and);

			String ele1 = arr1[0];
			String ele2 = arr1[1];
			String ele3 = arr1[2];

			for (int k = 0; k < 2; k++) {
				Element function = doc.createElement("function");
				function.setAttribute("name", "IS_NONDECREASING");
				function.setAttribute("type", "boolean");
				and.appendChild(function);

				Element arg = doc.createElement("arg");
				function.appendChild(arg);

				Element star = doc.createElement("star");
				star.setAttribute("type", "string(object)");
				arg.appendChild(star);

				Element e1 = doc.createElement("symbol");
				e1.setAttribute("type", "string(object)");
				star.appendChild(e1);

				Element e2 = doc.createElement("symbol");
				e2.setAttribute("type", "string(object)");
				star.appendChild(e2);

				if (k == 0) {
					e1.appendChild(doc.createTextNode(ele1.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele1.substring(1, 2)));
				}

				if (k == 1) {
					e1.appendChild(doc.createTextNode(ele2.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele2.substring(1, 2)));
				}
			}

			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			big_or.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			Element star = doc.createElement("star");
			star.setAttribute("type", "string(object)");
			arg.appendChild(star);

			Element e1 = doc.createElement("symbol");
			e1.setAttribute("type", "string(object)");
			star.appendChild(e1);

			Element e2 = doc.createElement("symbol");
			e2.setAttribute("type", "string(object)");
			star.appendChild(e2);

			e1.appendChild(doc.createTextNode(ele3.substring(0, 1)));
			e2.appendChild(doc.createTextNode(ele3.substring(1, 2)));

			if (z == 14 || z == 26) {
				shift(arr1);
			}
		}// end if z>=2 and z<39

		else if (z >= 39) {
			Element big_and_or;
			if (z < 51) {
				big_and_or = doc.createElement("and");
				big_and_or.setAttribute("type", "boolean");
				implies.appendChild(big_and_or);

				and_or = doc.createElement("and");
				and_or.setAttribute("type", "boolean");
				big_and_or.appendChild(and_or);
			} else {
				big_and_or = doc.createElement("or");
				big_and_or.setAttribute("type", "boolean");
				implies.appendChild(big_and_or);

				and_or = doc.createElement("or");
				and_or.setAttribute("type", "boolean");
				big_and_or.appendChild(and_or);
			}
			String ele1 = arr1[0];
			String ele2 = arr1[1];
			String ele3 = arr1[2];

			for (int k = 0; k < 2; k++) {
				Element function = doc.createElement("function");
				function.setAttribute("name", "IS_NONDECREASING");
				function.setAttribute("type", "boolean");
				and_or.appendChild(function);

				Element arg = doc.createElement("arg");
				function.appendChild(arg);

				Element star = doc.createElement("star");
				star.setAttribute("type", "string(object)");
				arg.appendChild(star);

				Element e1 = doc.createElement("symbol");
				e1.setAttribute("type", "string(object)");
				star.appendChild(e1);

				Element e2 = doc.createElement("symbol");
				e2.setAttribute("type", "string(object)");
				star.appendChild(e2);

				if (k == 0) {
					e1.appendChild(doc.createTextNode(ele1.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele1.substring(1, 2)));
				}

				if (k == 1) {
					e1.appendChild(doc.createTextNode(ele2.substring(0, 1)));
					e2.appendChild(doc.createTextNode(ele2.substring(1, 2)));
				}
			}

			Element function = doc.createElement("function");
			function.setAttribute("name", "IS_NONDECREASING");
			function.setAttribute("type", "boolean");
			big_and_or.appendChild(function);

			Element arg = doc.createElement("arg");
			function.appendChild(arg);

			Element star = doc.createElement("star");
			star.setAttribute("type", "string(object)");
			arg.appendChild(star);

			Element e1 = doc.createElement("symbol");
			e1.setAttribute("type", "string(object)");
			star.appendChild(e1);

			Element e2 = doc.createElement("symbol");
			e2.setAttribute("type", "string(object)");
			star.appendChild(e2);

			e1.appendChild(doc.createTextNode(ele3.substring(0, 1)));
			e2.appendChild(doc.createTextNode(ele3.substring(1, 2)));

		}
	}

	public static void shift(String list[]) {
		// System.out.println("shifting array");
		String temp = list[2]; // store the last element
		for (int i = 2; i > 0; i--) {
			list[i] = list[i - 1]; // do the switch
		}
		list[0] = temp; // restore the last element into the first one
	}

	public static Node setUp(Document doc, int a) {
		Element rootElement = doc.createElement("rsrg");
		doc.appendChild(rootElement);
		rootElement.setAttribute("cName", "1");
		rootElement.setAttribute("lemma", Integer.toString(a));

		// typedefinitions elements
		Element typedefinitions = doc.createElement("typedefinitions");
		rootElement.appendChild(typedefinitions);

		// mathdefinitions elements
		Element mathdefinitions = doc.createElement("mathdefinitions");
		rootElement.appendChild(mathdefinitions);

		// adding the Queue definitions by importing the nodes from the Queue_defn.xml file
		//if you want pretty print of the lemmas to work, comment out this method
		//get_functions(mathdefinitions, doc);

		// lemma elements
		Element lemma = doc.createElement("lemma");
		rootElement.appendChild(lemma);

		Element body = doc.createElement("body");

		if (a > 0) {
			// quant elements
			Element quant = doc.createElement("forall");
			quant.setAttribute("type", "boolean");
			lemma.appendChild(quant);

			// vars elements
			Element vars;

			if (a == 1) {
				vars = doc.createElement("vars");
				vars.setAttribute("type", "T");
				quant.appendChild(vars);
			}

			else if (a > 1) {
				vars = doc.createElement("vars");
				vars.setAttribute("type", "string(object)");
				quant.appendChild(vars);
			}

			else
				vars = doc.createElement("dunno");

			// name element
			if (a > 0) {
				Element name1 = doc.createElement("name");
				vars.appendChild(name1);
				name1.appendChild(doc.createTextNode("a"));
			}

			if (a > 1) {
				Element name2 = doc.createElement("name");
				vars.appendChild(name2);
				name2.appendChild(doc.createTextNode("b"));
			}

			if (a >= 3) {
				Element name3 = doc.createElement("name");
				vars.appendChild(name3);
				name3.appendChild(doc.createTextNode("c"));
			}

			// body elements

			quant.appendChild(body);
		}

		else
			lemma.appendChild(body);

		return body;

	}

	public static void get_functions(Element mathdefinitions, Document doc) {

		Document doc1 = null;
		try {

			FileInputStream fstream = new FileInputStream(
					"C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\defns\\Queue_defn.xml");

			DataInputStream in = new DataInputStream(fstream);

			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc1 = docBuilder.parse(fstream);

			// get all node_ids from doc2 and iterate
			NodeList list = doc1.getElementsByTagName("implicit");

			//System.out.println(list.getLength());
			for (int i = 0; i < list.getLength(); i++) {

				Node n = list.item(i);

				// import them into doc2
				Node imp = doc.importNode(n, true);
				mathdefinitions.appendChild(imp);
			}

			 list = doc1.getElementsByTagName("explicit");

			//System.out.println(list.getLength());
			for (int i = 0; i < list.getLength(); i++) {

				Node n = list.item(i);

				// import them into doc2
				Node imp = doc.importNode(n, true);
				mathdefinitions.appendChild(imp);
			}
			 list = doc1.getElementsByTagName("restricted");

			//System.out.println(list.getLength());
			for (int i = 0; i < list.getLength(); i++) {

				Node n = list.item(i);

				// import them into doc2
				Node imp = doc.importNode(n, true);
				mathdefinitions.appendChild(imp);
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		

	}

}
