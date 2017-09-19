package com.salesforce.esas.salesgtm;

import java.util.Scanner;

public class Test {

	public static void main(String[] args){
		try{
			Scanner reader = new Scanner(System.in); 
			System.out.println("Please Enter the file path : ");
			String fileName = reader.nextLine();
		}catch(Exception e){
			System.out.println(e);
		}
	}
}
