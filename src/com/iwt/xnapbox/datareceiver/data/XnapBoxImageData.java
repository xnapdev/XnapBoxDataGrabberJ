package com.iwt.xnapbox.datareceiver.data;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class XnapBoxImageData extends XnapBoxData {
	public String trackerID;
	public Calendar timestamp;
	public Calendar captureTime;
	public int blurIndex;
	public int height;
	public int width;
	public Position objPos;
	public String sourceID;
	public int frameNo;
	public Color color1;
	public Color color2;
	public String trackerDir;
	public String imgID;
	
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
		
		captureTime = parseEpochTime(headers.get("X-CaptureTime"));
		sourceID = headers.get("X-SourceID");
		frameNo = Integer.parseInt(headers.get("X-FrameNo"));
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		return "XnapBoxImageData [trackerID=" + trackerID + ", timestamp=" + sdf.format(timestamp.getTime()) + ", captureTime=" + ((captureTime==null)?"null":sdf.format(captureTime.getTime()))
				+ ", blurIndex=" + blurIndex + ", height=" + height + ", width=" + width + ", objPos=" + objPos
				+ ", sourceID=" + sourceID + ", frameNo=" + frameNo + ", color1=" + color1 + ", color2=" + color2
				+ ", trackerDir=" + trackerDir + ", imgID=" + imgID + "]";
	}
	
	
}
