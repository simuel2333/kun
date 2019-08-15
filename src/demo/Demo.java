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
//		String path = "C:\\Users\\simuel\\Desktop\\log.txt";
//		System.setOut(new PrintStream(path));
	

		p.connect();
		String team_name = "jinitaimei";
		Client client = new Client(team_id, team_name);

		/* registration */
		Registration r = new Registration(team_id, team_name);
		Message m = new Message("registration", r);
		p.send(m.toString());
		long start = System.currentTimeMillis();
		int enemy_score = 0;
		int self_score = 0;
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
				enemy_score += client.enemy.score.getPoint();
				self_score += client.self.score.getPoint();
				System.err.println(client.roundId+1);
			} else if (msg_name.equals("game_over")) {
				System.out.println("game_over");
				System.err.println("game_over");
				break;
			} else {
				System.out.println("unkown message name " + msg_name);
			}
		}
		System.err.println("我方得分:"+self_score);
		System.err.println("敌方得分:"+enemy_score);
		long end = System.currentTimeMillis();
		System.err.println("耗时:"+(end-start)/1000+"s");
	}

}
