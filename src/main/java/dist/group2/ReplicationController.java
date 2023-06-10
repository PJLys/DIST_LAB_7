package dist.group2;


import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path="api/node")
public class ReplicationController {
    private final ReplicationClient client;

    @Autowired  // Dependency injection, service will be automatically instantiated and injected into the constructor
    public ReplicationController(ReplicationClient client) {
        this.client = client;
    }

    @PostMapping
    public void replicateFile(HttpServletRequest request, @RequestBody JSONObject fileMessage) throws IOException {
        String senderIP = request.getRemoteAddr();
        client.replicateFile(fileMessage, senderIP);
    }
}
