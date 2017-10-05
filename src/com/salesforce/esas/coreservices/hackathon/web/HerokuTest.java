package com.salesforce.esas.coreservices.hackathon.web;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HerokuTest {

	public static void main(String[] args) {
		try{
			URL url = new URL("https://connect-us.heroku.com/api/v3/connections?app=esas-coreservices");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}

/*
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class HerokuTest{

   public static void main(String[] args)
   {
        new HerokuTest().testIt();
   }

   private void testIt(){

      String https_url = "https://connect-us.heroku.com/api/v3/connections?app=esas-coreservices";
      URL url;
      try {

	     url = new URL(https_url);
	     HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

	     //dumpl all cert info
	     print_https_cert(con);

	     //dump all the content
	     print_content(con);

      } catch (MalformedURLException e) {
	     e.printStackTrace();
      } catch (IOException e) {
	     e.printStackTrace();
      }

   }

   private void print_https_cert(HttpsURLConnection con){

    if(con!=null){

      try {

	System.out.println("Response Code : " + con.getResponseCode());
	System.out.println("Cipher Suite : " + con.getCipherSuite());
	System.out.println("\n");

	Certificate[] certs = con.getServerCertificates();
	for(Certificate cert : certs){
	   System.out.println("Cert Type : " + cert.getType());
	   System.out.println("Cert Hash Code : " + cert.hashCode());
	   System.out.println("Cert Public Key Algorithm : "
                                    + cert.getPublicKey().getAlgorithm());
	   System.out.println("Cert Public Key Format : "
                                    + cert.getPublicKey().getFormat());
	   System.out.println("\n");
	}

	} catch (SSLPeerUnverifiedException e) {
		e.printStackTrace();
	} catch (IOException e){
		e.printStackTrace();
	}

     }

   }

   private void print_content(HttpsURLConnection con){
	if(con!=null){

	try {

	   System.out.println("****** Content of the URL ********");
	   BufferedReader br =
		new BufferedReader(
			new InputStreamReader(con.getInputStream()));

	   String input;

	   while ((input = br.readLine()) != null){
	      System.out.println(input);
	   }
	   br.close();

	} catch (IOException e) {
	   e.printStackTrace();
	}

       }

   }

}
*/