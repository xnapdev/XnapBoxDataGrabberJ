package com.iwt.xnapbox.datareceiver.data;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class XnapBoxData {
	public XnapBoxData()
	{
		
	}
	
	public static XnapBoxData getInstance(String header)
	{
		return getInstance(extractHeader(header));
	}
	
	public static XnapBoxData getInstance(HashMap<String, String> headers)
	{
		if (headers.get("X-Timestamp").equals("000000000000000-000000000000000000000000000"))
		{
			return new XnapBoxHeartbeatData(headers);
		}else{
			return new XnapBoxImageData(headers);
		}
	}
		
	protected static HashMap<String, String> extractHeader(String header)
	{
		HashMap<String, String> headers = new HashMap<String, String>();
		String[] lines = header.split("\n");
		for (String l : lines)
		{
			if (l.contains(":"))
			{
//				System.out.print(l);
				headers.put(l.substring(0, l.indexOf(":")).trim(), l.substring(l.indexOf(":")+1).trim());
			}
		}
		return headers;		
	}
		
	protected String parseImageID(String imgIDStr)
	{
		String ret = imgIDStr;
		
		if (imgIDStr.contains("-"))
		{
			String[] parts = imgIDStr.split("-");
			ret = parts[parts.length-1];
		}
		
		return ret;
	}
	
	protected Calendar parseTimestamp(String timestampStr)
	{
		//20170411T165018-00000004877.790039-00121853
		Calendar ts = Calendar.getInstance();
		if (timestampStr.contains("-"))
		{
			String[] parts = timestampStr.split("-");
			
			if (parts[0].contains("T"))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
				try {
					Date d = sdf.parse(parts[0].replace('T', ' '));
					ts.setTime(d);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				String parts1 = parts[1];
				if (parts1.contains("."))
				{
					int millisecond = Integer.parseInt(parts1.substring(parts1.indexOf(".")+1));
					ts.add(Calendar.MILLISECOND, millisecond/1000);
				}
			}
		}
		return ts;
	}
	
	protected Color parseColor(String hsvStr)
	{
		Color c = null;
		
		String[] hsv = hsvStr.split("#");
		float h = Float.parseFloat(hsv[1]);
		float s = Float.parseFloat(hsv[2]);
		float v = Float.parseFloat(hsv[3]);
		c = Color.getHSBColor(h, s, v);
		
		return c;
	}
	
	protected Integer parseInt(String intStr)
	{
		try
		{
			return Integer.parseInt(intStr);
		}catch(NumberFormatException ex)
		{
			return -1;
		}
	}
}
