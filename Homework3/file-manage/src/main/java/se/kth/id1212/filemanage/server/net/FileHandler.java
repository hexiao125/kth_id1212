package se.kth.id1212.filemanage.server.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;

class FileHandler implements Runnable {
    private final FileHandlerServer server;
    private final Socket clientSocket;
    private final String dir =".\\server_disk\\";
    
    FileHandler(FileHandlerServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream inStream;
            OutputStream outFile;
            inStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            String filename = inStream.readUTF();
            outFile = new FileOutputStream(dir + filename);
            byte[] bytes = new byte[1024];
            int count;
            while ((count = inStream.read(bytes)) > 0) {
                outFile.write(bytes, 0, count);
            }
            outFile.close();
            inStream.close();
            clientSocket.close();
            server.removeHandler(this);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
        
}
