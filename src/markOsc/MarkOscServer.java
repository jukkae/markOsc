package markOsc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.illposed.osc.*;

public class MarkOscServer {
	OSCPortIn receiver;
	OSCPortOut sender;
	Markov markov;

	MarkOscServer() {
		markov = new Markov();
		try {
			receiver = new OSCPortIn(12010);
			sender = new OSCPortOut(InetAddress.getLocalHost(), 12001); // default port 57120, apparently
			OSCListener listener = new OSCListener() {
				public void acceptMessage(java.util.Date time,
						OSCMessage message) {
					if (message.getAddress().equals("/lerp1")) {
						float lerp = (float) message.getArguments()[0];
						markov.lerpMatricesA(lerp);
					}
					if (message.getAddress().equals("/lerp2")) {
						float lerp = (float) message.getArguments()[0];
						markov.lerpMatricesB(lerp);
					}
					for (int i = 0; i < message.getArguments().length; i++) {
						if (message.getArguments()[i].equals("getnextnote")) {
							int next = markov.getNextNote();
							try {
								sendInt(next);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (message.getArguments()[i].equals("getnextvalue")) {
							int next = markov.getNextValue();
							try {
								sendValue(next);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (next == 16 || next == 12)
								try {
									sendToggle(0);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} // TODO testing toggle!
						}
						if (message.getArguments()[i].equals("getnextmute")) {
							int mute = markov.getNextMute();
							try {
								sendMute(mute);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (message.getArguments()[i].equals("getnextlegato")) {
							int legato = markov.getNextLegato();
							try {
								sendLegato(legato);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			};
			receiver.addListener("/lerp1", listener);
			receiver.addListener("/lerp2", listener);
			receiver.addListener("/test", listener);
			receiver.startListening();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void sendInt(int i) throws IOException {
		Object args[] = new Object[1];
		args[0] = new Integer(i);
		sender.send(new OSCMessage("/state", args));
	}

	void sendValue(int i) throws IOException {
		Object args[] = new Object[1];
		args[0] = new Integer(i);
		sender.send(new OSCMessage("/value", args));
	}

	void sendToggle(int i) throws IOException {
		Object args[] = new Object[1];
		args[0] = new Integer(i);
		sender.send(new OSCMessage("/toggle", args));
	}

	void sendMute(int i) throws IOException {
		Object args[] = new Object[1];
		args[0] = new Integer(i);
		sender.send(new OSCMessage("/mute", args));
	}

	void sendLegato(int i) throws IOException {
		Object args[] = new Object[1];
		args[0] = new Integer(i);
		sender.send(new OSCMessage("/legato", args));
	}
}
