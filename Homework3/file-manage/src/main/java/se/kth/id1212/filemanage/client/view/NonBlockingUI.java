package se.kth.id1212.filemanage.client.view;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import se.kth.id1212.filemanage.client.net.ClientFileHandler;
import se.kth.id1212.filemanage.common.Credentials;
import se.kth.id1212.filemanage.common.FileInfo;
import se.kth.id1212.filemanage.common.FileManageServer;
import se.kth.id1212.filemanage.common.FileAccessClient;

public class NonBlockingUI implements Runnable {

    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafePrint outToConsole = new ThreadSafePrint();
    private final FileAccessClient myRemoteObj;
    private FileManageServer server;
    private String myUserName = "anonymous";
    private boolean consoleActive = false;
    private final String host = "192.168.1.103";
    private final int portNo = 8888;
    private String dir = ".\\client_disk\\";

    private static final String WELCOME = "Welcome to the file catalog server! \n"
            + "If you do not have an account, please register by command: register <username> <password>. \n"
            + "If you have an account, please login by command: login <username> <password>.";
    private static final String LOGIN_SUCCESS = "You are login now. \n"
            + "You can upload a file by command(1 for true, 0 for false): upload <filename> <privacy> <read> <write> <inform>\n"
            + "You can list all your files and public files by command: list\n"
            + "You can delete a file by command: delete <filename> \n"
            + "You can update a file by command: update <filename> ";
    private static final String LOGIN_FAIL = "User not found, or incorrect password, or you have logged in to another account already!";
    private static final String LOGOUT_SUCCESS = "You are logged out now.";
    private static final String LOGOUT_FAIL = "Please login first.";
    private static final String REGISTER_SUCCESS = "Congratulations! Your registration is successful!";
    private static final String REGISTER_FAIL = "Sorry! This username is used, please specify another name!";
    private static final String UNREGISTER_SUCCESS = "You have been unregistered!";
    private static final String UNREGISTER_FAIL = "Sorry! Username or password is wrong for unregistration!";
    private static final String ADD_FILE_SUCCESS = "Congratulations! Your file is added!";
    private static final String ADD_FILE_FAIL = "Sorry! Your file is not added to the server!";
    private static final String DELETE_FILE_SUCCESS = "Your file is deleted!";
    private static final String DELETE_FILE_FAIL = "You are not allowed to delete the file, or file is not exist!";
    private static final String UPDATE_FILE_SUCCESS = "File is updated!";
    private static final String UPDATE_FILE_FAIL = "File can not be updated, or file not exist!";
    private static final String DOWNLOAD_FILE_SUCCESS = "File is downloaded!";
    private static final String DOWNLOAD_FILE_FAIL = "You can not download the file!";

    public NonBlockingUI() throws RemoteException {
        myRemoteObj = new ConsoleOutput();
    }

    public void start() {
        if (consoleActive) {
            return;
        }
        consoleActive = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (consoleActive) {
            try {
                UserConsole cmdLine = new UserConsole(readNextLine());
                switch (cmdLine.getCmd()) {
                    case CONNECT:
                        lookupServer(cmdLine.getParameter(0));
                        outToConsole.println(WELCOME);
                        break;
                    case REGISTER:
                        if (server.register(new Credentials(cmdLine.getParameter(0),
                                cmdLine.getParameter(1)))) {
                            outToConsole.println(REGISTER_SUCCESS);
                        } else {
                            outToConsole.println(REGISTER_FAIL);
                        }
                        break;
                    case UNREGISTER:
                        if (server.unregister(new Credentials(cmdLine.getParameter(0),
                                cmdLine.getParameter(1)))) {
                            outToConsole.println(UNREGISTER_SUCCESS);
                        } else {
                            outToConsole.println(UNREGISTER_FAIL);
                        }
                        break;
                    case LOGIN:
                        if (server.login(myRemoteObj,
                                new Credentials(cmdLine.getParameter(0), cmdLine.getParameter(1)))) {
                            myUserName = cmdLine.getParameter(0);
                            outToConsole.println(LOGIN_SUCCESS);
                        } else {
                            outToConsole.println(LOGIN_FAIL);
                        }
                        break;
                    case LOGOUT:
                        if (server.logout(myUserName)) {
                            outToConsole.println(LOGOUT_SUCCESS);
                        } else {
                            outToConsole.println(LOGOUT_FAIL);
                        }
                        break;
                    case UPLOAD:
                        if (server.addFile(new FileInfo(cmdLine.getParameter(0),
                                getFileSize(cmdLine.getParameter(0)),
                                myUserName,
                                cmdLine.getParameter(1),
                                cmdLine.getParameter(2),
                                cmdLine.getParameter(3),
                                cmdLine.getParameter(4)))) {
                            sendFileToServer(cmdLine.getParameter(0));
                            outToConsole.println(ADD_FILE_SUCCESS);
                        } else {
                            outToConsole.println(ADD_FILE_FAIL);
                        }
                        break;
                    case DOWNLOAD:
                        if (server.getFile(myUserName, cmdLine.getParameter(0))) {
                            outToConsole.println(DOWNLOAD_FILE_SUCCESS);
                        } else {
                            outToConsole.println(DOWNLOAD_FILE_FAIL);
                        }
                        break;
                    case DELETE:
                        if (server.deleteFile(myUserName, cmdLine.getParameter(0))) {
                            outToConsole.println(DELETE_FILE_SUCCESS);
                        } else {
                            outToConsole.println(DELETE_FILE_FAIL);
                        }
                        break;
                    case LIST:
                        outToConsole.println(server.listFiles(myUserName));
                        break;
                    case UPDATE:
                        if (server.updateFile(myUserName, new FileInfo(cmdLine.getParameter(0),
                                getFileSize(cmdLine.getParameter(0)),
                                " ",
                                cmdLine.getParameter(1),
                                cmdLine.getParameter(2),
                                cmdLine.getParameter(3),
                                cmdLine.getParameter(4)))) {
                            sendFileToServer(cmdLine.getParameter(0));
                            outToConsole.println(UPDATE_FILE_SUCCESS);
                        } else {
                            outToConsole.println(UPDATE_FILE_FAIL);
                        }
                        break;
                    case QUIT:
                        consoleActive = false;
                        boolean forceUnexport = false;
                        UnicastRemoteObject.unexportObject(myRemoteObj, forceUnexport);
                        break;
                    default:
                }
            } catch (Exception e) {
                outToConsole.println("Unrecognized operation!");
            }
        }
    }

    private void sendFileToServer(String filename) throws IOException{
        ClientFileHandler cfhd = new ClientFileHandler(host, portNo);
        cfhd.sendFile(dir, myUserName, filename);   
        cfhd.disconnect();
    }
           
    public String getFileSize(String filename) {
        File file = new File(dir + myUserName + "\\" + filename);
        long length = file.length();
        String lengthString = String.valueOf(length);
        return lengthString;
    }
    
    private void lookupServer(String host) throws NotBoundException, MalformedURLException,
            RemoteException {
        server = (FileManageServer) Naming.lookup("//" + host + "/" + FileManageServer.SERVER_NAME_IN_REGISTRY);
    }

    private String readNextLine() {
        outToConsole.print(PROMPT);
        return console.nextLine();
    }

    private class ConsoleOutput extends UnicastRemoteObject implements FileAccessClient {

        public ConsoleOutput() throws RemoteException {
        }

        @Override
        public void recvMsg(String msg) {
            outToConsole.println((String) msg);
        }
    }
}
