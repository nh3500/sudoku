package org.isi.sudoku.utility;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.widget.Toast;
import android.content.Context;
import android.content.res.AssetManager;
import java.io.FileInputStream;


public class WriteReadFile {

	public static String Readfromfile(Context context,String filename,int number) {
		
		
		//Crate AssetManager to read file 
		 AssetManager assetManager = context.getAssets();
	     InputStream inputStream = null;

	     String MyStream=null;
	     try {
	    	 inputStream = assetManager.open(filename);
	 
	    	 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    	 byte[] bytes = new byte[4096];
	 
	    	 int len;
	    	 while ((len = inputStream.read(bytes)) > 0){
	    		 byteArrayOutputStream.write(bytes, 0, len);
	    	 }
	    	 	//MyStream = new String(byteArrayOutputStream.toByteArray(), "UTF8");
	    	 	MyStream=byteArrayOutputStream.toString("UTF8");
	     } catch (IOException e) {
	 // TODO Auto-generated catch block
	 e.printStackTrace();
	 //MyStream = e.toString();
	}
	//replace line feed
    MyStream=MyStream.replaceAll("\r","").replaceAll("\n","@");
	String puzzle=getOnePuzzle(number,MyStream);
	//LogHelper.d("get puzzle from assets :"+puzzle);
	return puzzle;
	}
	
	public static String getOnePuzzle(int num, String filestring) {
		String[] strArray = filestring.split("@");
		return strArray[num];
	}

	public static void writefile(Context context,String filename,String title,String inputstring){
		String input;
		if(filename=="record.txt"){
			input=title+"//@//"+inputstring+"\n";
		}
		else
			input=inputstring+"\n";
		
		try {
			 FileOutputStream writer;
			 writer = context.openFileOutput(filename, Context.MODE_APPEND);
			 BufferedWriter out = new BufferedWriter(new OutputStreamWriter
					  (writer,"UTF8"));
			 out.write(input);
			 out.close();
			 Toast.makeText(context, "save success", Toast.LENGTH_LONG).show();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static CharSequence[] readfile(Context context,String filename){
		
		String result=null;
	     try {
	    	 FileInputStream inputStream = context.openFileInput(filename);  
	 
	    	 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    	 byte[] bytes = new byte[4096];
	 
	    	 int len;
	    	 while ((len = inputStream.read(bytes)) > 0){
	    		 byteArrayOutputStream.write(bytes, 0, len);
	    	 }
	    	 	//MyStream = new String(byteArrayOutputStream.toByteArray(), "UTF8");
	    	 result=byteArrayOutputStream.toString("UTF8");
	     } catch (IOException e) {
	    	 return null;
	     }
	     //LogHelper.d(result);

	     	
	    	 result=result.replaceAll("\r","").replaceAll("\n","###");
		     String[] strArray = result.split("###");
		     CharSequence[] items=new CharSequence[strArray.length];
		     for(int i=0;i<strArray.length;i++){
		    	 String[] strArraytmp=strArray[i].split("//@//");
		    	 items[i]=strArraytmp[0];
		     }
		
		     return items;
	     }

		public static String readfile(Context context,String filename,int num){
		
		String result=null;
	     try {
	    	 FileInputStream inputStream = context.openFileInput(filename);  
	 
	    	 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    	 byte[] bytes = new byte[4096];
	 
	    	 int len;
	    	 while ((len = inputStream.read(bytes)) > 0){
	    		 byteArrayOutputStream.write(bytes, 0, len);
	    	 }
	    	 	//MyStream = new String(byteArrayOutputStream.toByteArray(), "UTF8");
	    	 result=byteArrayOutputStream.toString("UTF8");
	     } catch (IOException e) {
		 
	     }
	     //LogHelper.d(result);

	     	
	    	 result=result.replaceAll("\r","").replaceAll("\n","###");
		     String[] strArray = result.split("###");
		     
		     if(filename=="record.txt"){
		     String[] strArraytmp=strArray[num].split("//@//");
		     //LogHelper.d(strArraytmp[1]);
		     return strArraytmp[1];
		     }
		     else{    
			     //LogHelper.d(strArray[0]);
		    	 
			     return strArray[num];
			 }
		     
	     }
		
		public static int getAmountofPuzzle(Context context,String filename) {
			String result=null;
		     try {
		    	 FileInputStream inputStream = context.openFileInput(filename);  
		 
		    	 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		    	 byte[] bytes = new byte[4096];
		 
		    	 int len;
		    	 while ((len = inputStream.read(bytes)) > 0){
		    		 byteArrayOutputStream.write(bytes, 0, len);
		    	 }
		    	 	//MyStream = new String(byteArrayOutputStream.toByteArray(), "UTF8");
		    	 result=byteArrayOutputStream.toString("UTF8");
		     } catch (IOException e) {
		    	 return 10;
		     }

		     result=result.replaceAll("\r","").replaceAll("\n","###");
		     String[] strArray = result.split("###");
		     return strArray.length+10;
		}
		

}
