
package com.jepongdevxyz.vpn.tunnel;

import com.jepongdevxyz.vpn.logger.SkStatus;
import java.nio.channels.SocketChannel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Date;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.LocalPortForwarder;
import java.net.HttpURLConnection;
import java.net.URL;

public class Pinger extends Thread {
    
    private final Connection a;
	private final String b;
    private LocalPortForwarder c;
    private boolean d;
    private Socket f;

    public Pinger(Connection connection, String str) {
        
        this.a = connection;
        
		this.b = str;
        
    }

    private int b() {
        
        return (new Random().nextInt(6) + 2) * 1000;
        
    }

    public void close() {
        synchronized (this) {
            
            this.d = false;
            
            interrupt();
            
        }
    }

    public void run() {
        try {
            this.c = this.a.createLocalPortForwarder(9395, this.b, 80);
            this.d = true;
            while (this.d) {
                try {
				//	SkStatus.logInfo("(Ping) ping server: " + this.b + " "  + "with (HEAD)");
					InetAddress address = InetAddress.getByName(this.b);
					String ipAddress = address.getHostAddress();
                    this.f = new Socket("127.0.0.1", 9395);
                    this.f.setSoTimeout(5000);
                    OutputStream outputStream = this.f.getOutputStream();
                   outputStream.write(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append(new StringBuffer().append("GET http://").append(this.b).toString()).append("/ HTTP/1.1\r\nHost: ").toString()).append(this.b).toString()).append("\r\n\r\n").toString().getBytes());
                    outputStream.flush();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.f.getInputStream()));
                    bufferedReader.readLine();
					HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(new StringBuffer().append("https://").append(this.b).toString()).openConnection();
                    httpURLConnection.setRequestMethod("HEAD");
                    long currentTimeMillis = System.currentTimeMillis();
                    int responseCode = httpURLConnection.getResponseCode();
					currentTimeMillis = System.currentTimeMillis() - currentTimeMillis;
          if (currentTimeMillis < 150) {
            SkStatus.logInfo(
                "Ping "
                    + responseCode
                    + " OK "
                    + " <font color=\"green\">"
                    + "("
                    + currentTimeMillis
                    + "ms"
                    + ")"
                    + "</font>");
          } else if (currentTimeMillis < 200) {
            SkStatus.logInfo(
                "Ping "
                    + responseCode
                    + " OK "
                    + " <font color=\"#ffc107\">"
                    + "("
                    + currentTimeMillis
                    + "ms"
                    + ")"
                    + "</font>");
          } else if (currentTimeMillis > 200) {
            SkStatus.logInfo(
                "Ping "
                    + responseCode
                    + " OK "
                    + " <font color=\"red\">"
                    + "("
                    + currentTimeMillis
                    + "ms"
                    + ")"
                    + "</font>");
          } else {
						//  SkStatus.logInfo("<font color='#C33E3D'>No Data</font>");
					}
				//	bufferedReader.close();
				//	outputStream.close();
				//	this.f.close();
				} catch (Exception e) {
				//	SkStatus.logInfo("Error occurred: " + e.getMessage());
				}
                try {
                    Thread.sleep(b());
                } catch (Exception e2) {
					//  SkStatus.logInfo("ping stopped");
                 //   this.c.close();
                //    this.c = null;
                    return;
                }
            }
        } catch (Exception e3) {
           SkStatus.logInfo(new StringBuffer().append("Ping: ").append(e3.toString()).toString());
        }
    }
}
