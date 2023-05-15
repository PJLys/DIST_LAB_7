package dist.group2;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Client {
    private final Map<String,Character> networkFiles = new HashMap<>();
    public void readfile(String filename) {
        // Check if the file is locked
        if (this.getLock(filename)=='w') {
            //throw new Lock
        }

        // Find file location
        String ipaddr = NamingClient.findFile(filename);

        // Request the file
        this.request(ipaddr, filename, 'r');
        throw new NotYetImplementedException();
    }

    /**
     * Request a file to the remote node
     * @param ipaddr ip address of the node
     * @param filename name of requested file
     * @param method the action we want to perform on the file
     */
    private void request(String ipaddr, String filename, char method) {
        switch (method) {
            case 'r' -> {
                // Request
            }
        }
        throw new NotYetImplementedException();
    }

    /**
     * Handle an incoming file request from another node
     * @param nodename name of the requesting node
     * @param filename name of the file that
     * @param method
     */
    private void incomingRequest(String nodename, String filename, char method) {
        this.handleLock(filename, method);

        switch (method) {
            case 'r' -> {
            }
        }
        throw new NotYetImplementedException();
    }

    /**
     * Update local loc information based on the log of the filename
     * @param filename name of the file
     * @param method action that is to be performed on the file
     */
    private void handleLock(String filename, char method) {

    }

    /**
     * Check the local list for the lock of the file
     * @param filename name of file we request the lock from
     * @return either 'r' or 'w' to see how the file is being used
     */
    private char getLock(String filename) {
        return this.networkFiles.get(filename);
    }
}
