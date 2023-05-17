package dist.group2;

import dist.group2.agents.SyncAgent;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class Client {
    private final Map<String, Optional<Boolean>> networkFiles = new HashMap<>();

    public Client() {
        SyncAgent syncAgent = new SyncAgent(this.networkFiles);
        Thread syncThread = new Thread(syncAgent);
        syncThread.start();
    }

    public void readfile(String filename) throws RuntimeException {
        Optional<Boolean> lock = this.getLock(filename);
        if (lock.isPresent() && lock.get()) {
            throw new RuntimeException("File is currently being written to!");
        }

        // Request the file
        String ipaddr = NamingClient.findFile(filename);
        this.request(ipaddr, filename, false);
    }

    public void editfile(String filename) throws RuntimeException {
        Optional<Boolean> lock = this.getLock(filename);
        if (lock.isPresent()) {
            throw new RuntimeException("File is in use!");
        }

        String ipaddr = NamingClient.findFile(filename);
        this.request(ipaddr, filename, true);
    }


    /**
     * Request a file to the remote node
     * @param ipaddr ip address of the node
     * @param filename name of requested file
     * @param method the action we want to perform on the file
     */
    private void request(String ipaddr, String filename, boolean method) {
        throw new NotYetImplementedException();
    }

    /**
     * Handle an incoming file request from another node
     * @param nodename name of the requesting node
     * @param filename name of the file that
     * @param method action of the requester
     */
    private void incomingRequest(String nodename, String filename, boolean method) {
        this.handleLock(filename, method);
        throw new NotYetImplementedException();
    }

    /**
     * Update local loc information based on the log of the filename
     * @param filename name of the file
     * @param method action that is to be performed on the file
     */
    private void handleLock(String filename, boolean method) {
        if (method) {
            // If the caller wants to write (boolean true), set a true flag to the file
            this.networkFiles.put(filename, Optional.of(true));
        } else {
            // If the caller wants to read (boolean false), set a false flag to the file
            this.networkFiles.put(filename, Optional.of(false));
        }
    }

    /**
     * Check the local list for the lock of the file
     * @param filename name of file we request the lock from
     * @return either 'r' or 'w' to see how the file is being used
     */
    private Optional<Boolean> getLock(String filename) {
        return this.networkFiles.get(filename);
    }
}
