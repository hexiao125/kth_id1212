package se.kth.id1212.filemanage.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileAccessClient extends Remote{
    void recvMsg(String msg) throws RemoteException;
}
