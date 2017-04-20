package com.iwt.xnapbox.datareceiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

public class XnapBoxDataReceiver {
	URL xbcam;
	InputStream is;
	ReadThread rt;
	boolean rtStarted = false;
	XBDataProcessor xbdp;

	public interface XBDataProcessor {
		void OutputJpeg(String header, byte[] buffer);
	}

	public XnapBoxDataReceiver(XBDataProcessor _xbdp) {
		xbdp = _xbdp;
		rt = new ReadThread();
	}

	public boolean connect(String urlToRead, int connectTimeout, int readTimeout) {
		boolean ret = false;

		try {
			xbcam = new URL(urlToRead);

			URLConnection con = xbcam.openConnection();

			con.setConnectTimeout(connectTimeout);
			con.setReadTimeout(readTimeout);
			is = con.getInputStream();

			ret = (is != null);

			if (ret)
				System.out.println("Connected");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	public void startRead() {
		if (rtStarted) {
			rt.resumeR();
		} else {
			rtStarted = true;
			rt.start();
		}
	}

	public void stopRead() {
		rt.pause();
	}

	public void disconnect() {
		try {
			rt.resumeR();
			rt.end();

			Thread.sleep(1000);

			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ReadThread extends Thread {
		Object lock = new Object();
		boolean paused = false;
		boolean terminated = false;

		public void pause() {
			paused = true;
		}

		public void resumeR() {
			paused = false;

			synchronized (lock) {
				lock.notify();
			}
		}

		public void end() {
			terminated = true;
		}

		@Override
		public void run() {
			ByteArrayBuffer mReadBuffer = new ByteArrayBuffer(0);
			ByteArrayBuffer mHeaderBuffer = new ByteArrayBuffer(0);

			int b = -1;

			do {
				if (terminated) {
					return;
				}

				if (paused) {
					try {
						synchronized (lock) {
							lock.wait();
						}
					} catch (InterruptedException e) {
					}
				}

				try {
					b = is.read();

					if (b == 0xff) {
						b = is.read();
						if (b == 0xd8) {
							// Jpeg Start
							mReadBuffer.append(0xff);
							mReadBuffer.append(0xd8);

							do {
								b = is.read();
								mReadBuffer.append(b);

								if (b == 0xff) {
									b = is.read();
									mReadBuffer.append(b);

									if (b == 0xd9) {
										// Jpeg End
										break;
									}
								}
							} while (true);
							String header = "";
							if (!mHeaderBuffer.isEmpty()) {
								header = new String(mHeaderBuffer.toByteArray());
//								System.out.println(header);
							}
							xbdp.OutputJpeg(header, mReadBuffer.buffer());
							mReadBuffer.clear();
						}

						mHeaderBuffer.clear();
					} else {
						mHeaderBuffer.append(b);
					}
				} catch (IOException e1) {
					e1.printStackTrace();

					try {
						while (!terminated) {
							System.out.println("Try Reconnect");
							if (is != null)
								is.close();
							if ((is = xbcam.openStream()) != null) {
								System.out.println("Reconnected");
								break;
							}
							Thread.sleep(1000);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} while (true);
		}
	}
}
