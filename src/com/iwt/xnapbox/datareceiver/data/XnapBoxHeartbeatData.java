package com.iwt.xnapbox.datareceiver.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class XnapBoxHeartbeatData extends XnapBoxData {
	public Calendar receiveTime;
	public Calendar captureTime;

	public XnapBoxHeartbeatData(HashMap<String, String> headers) {
		super();
		receiveTime = Calendar.getInstance();
		captureTime = parseEpochTime(headers.get("X-CaptureTime"));
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return "XnapBoxHeartbeatData [receiveTime=" + sdf.format(receiveTime.getTime()) + ", captureTime=" + ((captureTime==null)?"null":sdf.format(captureTime.getTime())) + "]";
	}

	
}
