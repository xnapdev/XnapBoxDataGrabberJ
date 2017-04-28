package com.iwt.xnapbox.datareceiver.data;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class XnapBoxImageData extends XnapBoxData {
	public Color color1;
	public Color color2;
	public Position objPos;
	public int height;
	public int width;
	public String trackerID;
	public String trackerDir;
	public Calendar timestamp;
	public String imgID;
	public int blurIndex;
	
	public XnapBoxImageData(HashMap<String, String> headers)
	{
		super();
		color1 = parseColor(headers.get("X-ObjectColor1HSV"));
		color2 = parseColor(headers.get("X-ObjectColor2HSV"));
		objPos = new Position();
		objPos.x = Integer.parseInt(headers.get("X-objectXpos"));
		objPos.y = Integer.parseInt(headers.get("X-objectYpos"));
		height = Integer.parseInt(headers.get("X-objectHeight"));
		width = Integer.parseInt(headers.get("X-objectWidth"));
		trackerID = headers.get("X-TrackerID");
		trackerDir = headers.get("X-TrackDir");
		
		if (headers.get("X-Timestamp").equals("000000000000000-000000000000000000000000000"))
		{
			timestamp = Calendar.getInstance();
			System.out.println("Heartbeat");
		}else{
			timestamp = parseTimestamp(headers.get("X-Timestamp"));
		}
		imgID = parseImageID(headers.get("X-Timestamp"));
		blurIndex = parseInt(headers.get("X-BlurIndex"));
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		return "XnapBoxImageData [color1=" + color1 + ", color2=" + color2 + ", objPos=" + objPos + ", height=" + height
				+ ", width=" + width + ", trackerID=" + trackerID + ", trackerDir=" + trackerDir + ", timestamp="
				+ sdf.format(timestamp.getTime()) + ", imgID=" + imgID + ", blurIndex=" + blurIndex + "]";
	}
	
}
