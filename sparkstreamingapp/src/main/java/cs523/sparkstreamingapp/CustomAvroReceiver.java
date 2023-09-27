package cs523.sparkstreamingapp;

	import org.apache.spark.storage.StorageLevel;
	import org.apache.spark.streaming.receiver.Receiver;

	import java.io.IOException;
	import java.io.ObjectInputStream;
	import java.net.ServerSocket;
	import java.net.Socket;

	public class CustomAvroReceiver extends Receiver<byte[]> {

	    private int port = 41234;  // Specify the port number you configured in Flume
	    private transient ServerSocket serverSocket = null;

	    public CustomAvroReceiver() {
	        super(StorageLevel.MEMORY_AND_DISK_2());
	    }

	    @Override
	    public void onStart() {
	        // Start the thread that receives data
	        new Thread(this::receive).start();
	    }

	    @Override
	    public void onStop() {
	        // Cleanup logic if needed
	    }

	    private void receive() {
	        try {
	            serverSocket = new ServerSocket(port);
	            while (!isStopped()) {
	                Socket socket = serverSocket.accept();
	                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	                byte[] data = (byte[]) ois.readObject();
	                store(data);
	                ois.close();
	                socket.close();
	            }
	        } catch (IOException | ClassNotFoundException e) {
	            restart("Error receiving data", e);
	        }
	    }
	}
