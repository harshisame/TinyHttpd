package edu.umb.cs.threads.tinyhttpd;

import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TinyHttpd3 extends TinyHttpd2 implements Runnable{

	private Socket client;
	ReentrantLock aLock;

	public TinyHttpd3(Socket client) {
		super();
		this.setClient(client);
		aLock = new ReentrantLock();
	}

	public void run() {
		aLock.lock();
		try{
			executeCommand(client);
		}finally{
			aLock.unlock();
		}		
	}

	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}
	
	

}
