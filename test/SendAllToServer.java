import java.io.File;
import java.io.IOException;
public class SendAllToServer{

	public static void main(String arp[])throws IOException{


		SendData sendData;
		File f=new File("rs Files/");
		for(File a:f.listFiles()){
			System.out.println(a.getName()); 
			
			System.out.println("parent name is "+a.getParent());
			sendData=new SendData(a);
			sendData.actuallyDoIt();



		}




	}
}
