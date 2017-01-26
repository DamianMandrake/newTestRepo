import org.json.*;
import java.io.*;
import java.net.*;
/*
This class reads the contents of a file according to the pattern specified 
ie it searches for a pattern pair ... one attatched to the beginning of the string and the other at the end.
It also searches for multiple such patternPairs.
Attatches a '#' in the end so that we can process it later.
This program can only execute 1 directory at a time... cant do multiple since


*/


public class SendData implements SendDataConstants{
	private String data;
	private File fileToRead;
	private URL url;
	private HttpURLConnection httpUrlConnection;
	private MyThread m[];
/**************
 **************


**************************
****************************

********************NOTE parentFolder's value must be the same as the name of the table

**************************************************************************************

*********************************
*********************************
*********************************
LEVEL ALSO HAS TO BE SPECIFIED....
*****************************
****************************
***************************
	*/
	private int level=-1;
	private String parentFolder;//to tell the script what type of data is going to be sent
	//can iterate through the string once to obtain the content,questions options and answers using Scanner class



	private StringBuilder content,questions,options,answers,all;

	public SendData(File fileToRead){
		this.m=new MyThread[4];
		this.fileToRead=fileToRead;
		this.content=new StringBuilder();
		this.questions=new StringBuilder();
		this.options=new StringBuilder();
		this.parentFolder=fileToRead.getParent();
		this.initLevel();
		this.answers=new StringBuilder();this.all=new StringBuilder();
		try{
		url=new URL(REGISTRATION_URL);

		this.httpUrlConnection= (HttpURLConnection)url.openConnection();
		this.httpUrlConnection.setRequestMethod("POST");
		this.httpUrlConnection.setDoOutput(true);
		this.httpUrlConnection.setDoInput(true);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	private void initLevel(){
		String name=this.fileToRead.getName();
		if(name.contains(LV_AMATEUR))
			this.level=1;
		else if(name.contains(LV_INTER))
			this.level=2;
		else if(name.contains(LV_PRO))
			this.level=3;
	}

	private void sendDataToServer(){
		try{
		

		OutputStream outputStream=httpUrlConnection.getOutputStream();
		BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));



		JSONObject json=new JSONObject();
		json.put(TYPE,this.parentFolder);
		json.put(LEVEL,this.level);
		json.put(CONTENT,this.content.toString());
		json.put(QUESTION,this.questions.toString());
		json.put(OPT,this.options.toString());
		json.put(ANSWER,this.answers.toString());


		bufferedWriter.write(json.toString());
		bufferedWriter.flush();
		bufferedWriter.close();
		outputStream.close();
		
		//accepting input from server
		getInputFromServer();

		
			}catch (IOException|JSONException e) {
			e.printStackTrace();
			}

	}
	private void getInputFromServer()throws IOException{
		InputStream inputStream=httpUrlConnection.getInputStream();
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder sb=new StringBuilder();
		String temp="";

		while((temp=bufferedReader.readLine())!=null)
			sb.append(temp);

		System.out.println("SERVER REPLY\n"+sb.toString());
		bufferedReader.close();
		inputStream.close();
	}

	private void obtainContentsOfFile(){
		FileInputStream fileIn=null;

		try{
		fileIn=new FileInputStream(fileToRead);
		int ch;
		while((ch=fileIn.read())!=-1)
				this.all.append(""+(char)ch);

			}catch(IOException f){
				f.printStackTrace();
			}
			finally{
				try{
					if(fileIn!=null)
						fileIn.close();
				}catch(IOException io){io.printStackTrace();}
			}
	}
	public void actuallyDoIt(){//main Entry Point

		this.obtainContentsOfFile();
		 this.m[0]=new MyThread(this.content,CONTENT_PATT,all);
		 this.m[1]=new MyThread(this.questions,QUESTION_PATT,all);
		 this.m[2]=new MyThread(this.options,OPT_PAT,all);
		 this.m[3]=new MyThread(this.answers,ANSWER_PAT,all);

		 while(!this.areAllTrue());//waiting for all threads to compute the result
		 System.out.println("DONE WAITING");
		 if(level==-1)
		 {
		 	System.out.println("invalid file found since level cant be specified \nFile is "+fileToRead.toString()+" skipping file ");
			return; 	
		 }
		 this.sendDataToServer();
		


	}

	public static void main(String ar[]){//for testing
		SendData sen=new SendData(new File("rs Files/f1.rs"));
		sen.obtainContentsOfFile();
		
		
		 sen.m[0]=new MyThread(sen.content,CONTENT_PATT,sen.all);
		 sen.m[1]=new MyThread(sen.questions,QUESTION_PATT,sen.all);
		 sen.m[2]=new MyThread(sen.options,OPT_PAT,sen.all);
		 sen.m[3]=new MyThread(sen.answers,ANSWER_PAT,sen.all);
		System.out.println("Back in main*******************");

		while(!sen.areAllTrue());
		System.out.println("DONE WAITING");
		sen.sendDataToServer();

		}

		private boolean areAllTrue(){
			for(int i=0;i<4;i++)
				if(!this.m[i].isDone())
					return false;

			return true;
		}



}
class MyThread implements Runnable{

	private StringBuilder ref;
	private StringBuilder allData;
	private Thread t;
	private String pattern;
	private boolean b;
	//will have to pass in all data to make it execute multiple files at a time....
	//since static will share the value across all objects and once the next file gets
	//set from SendAllToServer , all threads will  have the contents of the next file 
	// NOT THE ORIGINAL one

	MyThread(StringBuilder reference,String patter,StringBuilder allData){
		MyThread.this.ref=reference;
		System.out.println(ref==reference);
		this.allData=allData;
		this.pattern=patter;
		t=new Thread(this);
		t.start();
		b=false;
	}
	

	public void run(){
		int init=0,last=0;
		while(true){
			init=allData.indexOf(pattern,init);
			if(init==-1)
			break;
		 last=allData.indexOf(pattern,init+pattern.length());
		ref.append(allData.toString().substring(init+pattern.length(),last)+"#\n");
		init=last+pattern.length();
		
		}
		b=true;
		

	}
	public boolean isDone(){return b;}


}
interface SendDataConstants{
	static final String CONTENT="PASSAGE",QUESTION="QUESTION",OPT="OPTIONS",ANSWER="ANSWER";


	static final String REGISTRATION_URL="http://127.0.0.1/test/inserter.php";
	static final String CONTENT_PATT="**",QUESTION_PATT="??",OPT_PAT="/",ANSWER_PAT="..";
	static final String TYPE="FILE TYPE";
	final static String LEVEL="LEVEL",LV_AMATEUR="amateur",LV_INTER="intermediate",LV_PRO="proffessional";



}