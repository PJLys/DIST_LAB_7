package dist.group2;


import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(path="replication")
public class ReplicationController {
    private final ReplicationClient client;

    @Autowired  // Dependency injection, service will be automatically instantiated and injected into the constructor
    public ReplicationController(ReplicationClient client) {
        this.client = client;
    }

    @PostMapping("/replicateFile")
    public void replicateFile(HttpServletRequest request, @RequestBody JSONObject fileMessage) throws IOException {
        String senderIP = request.getRemoteAddr();
        client.replicateFile(fileMessage, senderIP);
    }

    @PostMapping("{filename}/log/addReplicator/{replicator}")
    public void addReplicator(@PathVariable("filename") String fileName, @PathVariable int replicator) {
        Logger.addReplicator(ReplicationClient.getLogFilePath().resolve(fileName + ".log").toString(), replicator);
    }
}
