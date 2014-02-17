import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
public class PrettyPrinter {
	
		public static void main(String[] args) throws IOException {
			FileWriter fstream; 
			BufferedWriter out;
			
			String DirName = ("C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\test\\");

			
			File[] listOfFiles = GetFilesFromDirectory(DirName);
			
			int numofFiles = listOfFiles.length;
			String files;

			//this is where the pretty print is stored
			fstream = new FileWriter("C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\text\\lemmas_in_text.txt" );
			out = new BufferedWriter(fstream);
			
			for(int i=0; i< numofFiles; i++)
			{
				if (listOfFiles[i].isFile()) 
				{
					files = listOfFiles[i].getName();
					//out.write(files.substring(25,27)+ " ");
					XMLProcessor xml_processor = new XMLProcessor();
					xml_processor.ProcessXmlsFromDirectory(files, DirName, out);
					out.newLine();
				}
			}
			
			out.close();
		System.out.println("Pretty printed !!");

		}//end main

		public static File[] GetFilesFromDirectory(String path)
		{

			String files;
			File folder = new File(path);
			
			File[] listOfFiles = folder.listFiles(); 

			for (int i = 0; i < listOfFiles.length; i++) 
			{

				if (listOfFiles[i].isFile()) 
				{
					files = listOfFiles[i].getName();
					//  System.out.println(files);
				}
			}

			return listOfFiles;
		}//end GetFilesFromDirectory(String path)

		
	}



