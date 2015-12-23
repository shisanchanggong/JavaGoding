package httpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author springzero E-mail: 464150147@qq.com
 * @version 创建时间：2015年12月23日 下午1:54:28
 * 类说明
 */
public class HttpSolver {
	Socket incomingSocket;
	String root;
	
	public HttpSolver(Socket incomingSocket, String root) {
		this.incomingSocket = incomingSocket;
		this.root = root;
	}
	
	public void serverDoing() {
		Scanner in = null;
		OutputStream out = null;
		
		try {
			in = new Scanner(incomingSocket.getInputStream());
			out = incomingSocket.getOutputStream();
			HttpMessage httpMessage = new HttpMessage();
			httpMessage.root = root;
			while(in.hasNextLine()) {
				String input = in.nextLine();
				String splitResult[] = null;
				if(input.startsWith("GET") || input.startsWith("get")) {	//input= "GET /servlet/default.jsp HTTP/1.1"
					splitResult = input.split(new String(" "));
					if(!splitResult[2].equals("HTTP/1.1")) {
						new BadRequest().work(out);
						incomingSocket.close();
						break;
					}
					if(splitResult[1].equals("/")) {
						httpMessage.targetFile = "index.html";
					} else {
						httpMessage.targetFile = splitResult[1].substring(1,splitResult[1].length()); //这里就是处理链接地址的地方，很显然现在写的太简单了
					}
				} else {	//Accept: text/plain; text/html 处理这一类
					splitResult = input.split(new String(":"));
					switch (splitResult[0]) {
						case "If-Modified-Since":
								httpMessage.If_Modified_Since = true;
								httpMessage.If_Modified_SinceString = splitResult[1];
								break;
						case "":
								new GetDisk(httpMessage).work(out);
								incomingSocket.close();
								break;
						default:
								break;
					}
				}
			}
		} catch(IOException e) {
			System.out.println("socket io exception");
			e.printStackTrace();
		} finally {
			try {
				if(in != null) {
					in.close();
				}
				if(out != null) {
					out.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
