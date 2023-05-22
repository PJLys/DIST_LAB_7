package dist.group2;

import dist.group2.agents.SyncAgent;
import net.minidev.json.JSONObject;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Client {
    private static final Path owned_files = Path.of(new File("").getAbsolutePath().concat("\\src\\replicated_files"));
    private final SyncAgent syncAgent;
    public Client() {
        SyncAgent syncAgent = SyncAgent.getAgent();
        Thread syncThread = new Thread(syncAgent);
        this.syncAgent = syncAgent;
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
        JSONObject body = new JSONObject();
        body.put("node", NamingClient.getName());

    }

    /**
     * Handle an incoming file request from another node
     * @param nodename name of the requesting node
     * @param filename name of the file that
     * @param method action of the requester
     */
    protected byte[] incomingRequest(String nodename, String filename, boolean method) {
        Path path_to_file = Paths.get(String.valueOf(owned_files), filename);
        if (!Files.exists(path_to_file)) {
            System.out.println("File " + filename + " not found in owned files!");
            return new byte[0];
        }
        try {
            return Files.readAllBytes(path_to_file);
        } catch (IOException e) {
            System.out.println("Failed to access file!\n\n");
            System.out.println(e.getMessage()+"\n\n");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return new byte[0];
    }


    /**
     * Check the local list for the lock of the file
     * @param filename name of file we request the lock from
     * @return Optional containing either a read (false) or write (true) lock
     */
    private Optional<Boolean> getLock(String filename) {
        return this.syncAgent.getLock(filename);
    }
}
