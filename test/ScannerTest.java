import java.util.*;

class ScannerTest{

	public static void main(String arp[]){
		String str="** hello world** asd";
		Scanner sc=new Scanner(str);
		String a="\\*\\*";
		sc.useDelimiter(",");
		System.out.println(a);
		while(sc.hasNext())
			System.out.println(sc.next());
	}

}