package se.kth.id1212.filemanage.client.net;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

public class ClientFileHandler {
    private Socket socket = null;
    private static final int LINGER_TIME = 10000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;

    public ClientFileHandler(String host, int portNo) throws IOException {
        socket = new Socket(host, portNo);
        socket.setSoLinger(true, LINGER_TIME);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        
    }
     
    public void sendFile(String dir, String username, String filename) {
        try {
            DataOutputStream outString = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            outString.writeUTF(filename);
            outString.flush();
            InputStream inFile;
            OutputStream outStream;
            File file = new File(dir + username + "\\" + filename);
            long length = file.length();
            byte[] bytes = new byte[1024];
            inFile = new FileInputStream(file);
            outStream = socket.getOutputStream();
            
            int count;
            while ((count = inFile.read(bytes)) > 0) {
                outStream.write(bytes, 0, count);
                outStream.flush();
            }
            inFile.close();
            outStream.close();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
    
    public void disconnect() throws IOException{
        socket.close();
        socket = null;
    }
    
}
