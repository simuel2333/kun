package demo;

import java.io.IOException;
import java.io.PrintStream;

import cmd.Message;
import cmd.Registration;
import cmd.RoundAction;
import net.sf.json.JSONObject;

public class Demo {
	public static void main(String args[]) throws IOException {
		int team_id = Integer.parseInt(args[0]);
	    String ip = args[1];
	    int port = Integer.parseInt(args[2]);
	    Proxy p = new Proxy(ip, port);
		
//		System.err.println("Start!");
//		Proxy p = new Proxy("127.0.0.1", 6001);
//		int team_id = 998;
//		String path = "C:\\Users\\admin\\Desktop\\log.txt";
//		System.setOut(new PrintStream(path));
	

		p.connect();
		String team_name = "jinitaimei";
		Client client = new Client(team_id, team_name);

		/* registration */
		Registration r = new Registration(team_id, team_name);
		Message m = new Message("registration", r);
		p.send(m.toString());
		long start = System.currentTimeMillis();
		while (true) {
			String str = p.recieve();
			JSONObject json;
			try {
				json = JSONObject.fromObject(str);
			} catch (Exception e) {
				System.out.println(e.toString());
				continue;
			}
			
			String msg_name = json.getString("msg_name");
			if (msg_name.equals("leg_start")) {
				client.legStart(json.getJSONObject("msg_data"));
			} else if (msg_name.equals("round")) {
				client.round(json.getJSONObject("msg_data"));
				RoundAction action = client.act();
				Message am = new Message("action", action);
				String send = am.toString();
				p.send(send);
			} else if (msg_name.equals("leg_end")) {
				client.legEnd(json.getJSONObject("msg_data"));
			} else if (msg_name.equals("game_over")) {
				System.out.println("game_over");
				System.err.println("game_over");
				break;
			} else {
				System.out.println("unkown message name " + msg_name);
			}
		}
		long end = System.currentTimeMillis();
		System.err.println("ºÄÊ±:"+(end-start)/1000+"s");
	}

}
