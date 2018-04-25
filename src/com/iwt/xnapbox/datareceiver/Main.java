package com.iwt.xnapbox.datareceiver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.iwt.xnapbox.datareceiver.data.XnapBoxData;

public class Main 
{
	static class FileWritter implements XnapBoxDataReceiver.XBDataProcessor 
	{
		int cnt=0;
		String outputPath;

		public void setOutputPath(String op)
		{
			outputPath = op;
		}
		
		private String getOutputName(XnapBoxData xbdata)
		{		
			return String.format("%s/XB_%s_%08d.jpg", outputPath, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), cnt++);
		}
				
		@Override
		public void OutputJpeg(String header, byte[] buffer) 
		{
			FileOutputStream fos;
			try 
			{
//				System.out.println("~~~OutputJpeg~~~");
				XnapBoxData xbdata = XnapBoxData.getInstance(header);
				System.out.println(xbdata.toString());
				
				fos = new FileOutputStream(getOutputName(xbdata));
				fos.write(buffer);
				fos.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}			
		}
	}
	
	
	public static void main(String[] args) throws InterruptedException, IOException 
	{	
		Path dir = Paths.get("./images");
		
		FileWritter fw = new FileWritter();
		fw.setOutputPath(dir.toString());
		
		if (Files.notExists(dir))
		{
			Files.createDirectories(dir);
		}
		
		XnapBoxDataReceiver xbdr = new XnapBoxDataReceiver(fw);
		
		System.out.println("Try Connect");
		
		if(xbdr.connect("http://172.16.1.61:8080/", 8000, 30000))
		{
			System.out.println("Start To Read for 300s");
			
			xbdr.startRead();
			
			Thread.sleep(300000);
			
			xbdr.stopRead();
			System.out.println("Disconnect");
			xbdr.disconnect();
		}
		
		System.out.println("End");
	}
}
