package sdis.wetranslate.notifications;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketListener extends WebSocketClient {
	private NotificationService notifier;
	
	public WebSocketListener(URI serverURI, NotificationService notifier) {
		super(serverURI);
		this.notifier = notifier;
	}

	public WebSocketListener(URI serverURI, Draft draft, NotificationService notifier) {
		super(serverURI, draft);
		this.notifier = notifier;
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Connection closed by " + (remote ? "remote peer" : "us"));
	}

	@Override
	public void onError(Exception e) {
		e.printStackTrace();
	}

	@Override
	public void onMessage(String message) {
		if (message.equals("notify")) {
			notifier.notifyClient();
		}
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
		System.out.println("Opened connection");		
	}
	
}
