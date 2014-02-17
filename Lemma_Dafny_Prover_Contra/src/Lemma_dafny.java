import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;




public class Lemma_dafny {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//first I will delete the old dfy files

		String DirName1 = ("C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\dafny\\");
		File[] listOfFiles1 =  GetFilesFromDirectory(DirName1);

		int numofFiles1 = listOfFiles1.length;
		String files1; 

		for(int i=0; i< numofFiles1; i++)
		{
			if (listOfFiles1[i].isFile()) 
			{
				listOfFiles1[i].delete();
			}
		}
		
		//This is where the lemmas to be proved are
		String DirName = ("C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\test\\");

		File[] listOfFiles = GetFilesFromDirectory(DirName);

		int numofFiles = listOfFiles.length;
		String files;

		for(int i=0; i< numofFiles; i++)
		{
			if (listOfFiles[i].isFile()) 
			{
				files = listOfFiles[i].getName();
				XMLProcessor xml_processor = new XMLProcessor();
				xml_processor.ProcessXmlsFromDirectory(files, DirName);
			}
		}
		pct(listOfFiles);

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

	public static void pct(File[] listOfFilesXML)
	{
		//this is for the requires files
		try {
			String line;
			String DirName = ("C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\dafny\\");
			File[] listOfFiles =  GetFilesFromDirectory(DirName);
			FileWriter fstream; 
			BufferedWriter out;

			int numofFiles = listOfFiles.length;
			int numofFilesXML = listOfFilesXML.length;
			
			System.out.println(numofFiles+","+numofFilesXML);
			String files; int verified = 0, error = 0, timeout = 0;
			
			fstream = new FileWriter("C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\text\\answer.txt");
			out = new BufferedWriter(fstream);


			for(int i=0; i< numofFiles; i++)
			{
				if (listOfFiles[i].isFile()) 
				{
					files = listOfFiles[i].getName();
					out.write(""+files + "-");  
					//System.out.print(""+files + "-");

					verified = 0; error = 0; timeout = 0;
					//String[] cmd = {"C:\\Users\\adititagore\\Desktop\\research\\dafny_25_May_2012\\dafny.exe", "/timeLimit:1", "/compile:0","/nologo", "C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\dafny\\"+files};
					String[] cmd = {"C:\\Users\\adititagore\\Desktop\\research\\Dafny_Nov12\\dafny.exe", "/timeLimit:10", "/compile:0","/nologo", "C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\dafny\\"+files};
					Process p = Runtime.getRuntime().exec(cmd);


				
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					
					while ((line = input.readLine()) != null)
					{
						System.out.println(line);
						if(line.contains("errors detected") || line.contains("assertion violation"))
						{
							error = 1;
							//System.out.print("Error detected : " );
							System.out.println(line);
						}
						else if (line.contains("time out"))
						{
							timeout = 1;
						}
						else if(line.contains("0 errors") && !(line.contains("time out") ))
						{
							//System.out.println("(DEBUG)- verified is "+verified);
							verified = 1;
							//System.out.print("Verified" );
							//System.out.println(line);
						}
						out.write(line+"-");
					}

					if (verified == 1)
					{
						out.write("VERIFIED");
						System.out.println("VERIFIED");
						
						//will move the proved files to this directory
						File dir = new File("C:\\MY_STUFF\\research\\lemma_generator\\lemmas\\proved\\");

						// Move file to new directory
						String name = listOfFilesXML[i].getName();
						boolean success = listOfFilesXML[i].renameTo(new File(dir, listOfFilesXML[i].getName()));
						if (!success) {
						   System.out.println(" File was not successfully moved: "+name);
						}
						listOfFilesXML[i].delete();
						
					}
					if (error == 1)
					{
						out.write("ERROR" );
						System.out.println("ERROR" );
						
					}
					if (timeout == 1)
					{
						out.write("TIMEOUT" );
						System.out.println("TIMEOUT");
					}
					out.newLine();
					while ((line = stdError.readLine()) != null) {
						out.write(line);
					}

					input.close();
					stdError.close();
					p.destroy();

				}
			}
			out.close();
			System.out.println("DONE");

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


}
