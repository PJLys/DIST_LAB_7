package dist.group2;


import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@RestController
@RequestMapping(
        path = "client"
)
public class ClientController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Client client;

    @Autowired
    public ClientController(Client client){
        this.client = client;
    }

    @GetMapping("/{filename}/exists")
    public boolean checkExistence(@PathVariable("filename") String filename){
        File localFile = new File(ReplicationClient.getLocalFilePath().toString() + "/" + filename);
        File replicatedFile = new File(ReplicationClient.getReplicatedFilePath().toString() + "/" + filename);
        return (localFile.exists() | replicatedFile.exists());
    }

    @GetMapping("{filename}/{request}")
    public byte[] requestFile(@PathVariable("filename") String filename,
                              @PathVariable("request") char request,
                              @RequestBody String body){
        JSONObject data = null;
        JSONParser parser = new JSONParser(1);
        try{
            data = (JSONObject) parser.parse(body);
        } catch (ParseException e) {
            System.out.println("Received message but failed to parse data!");
            System.out.println("\tRaw data received: " + body);
            System.out.println("\n\tException: \n\t"+e.getMessage());
            return new byte[0];
        }

        boolean action = request=='w';

        return client.incomingRequest((String) data.get("node"), filename, action);
    }
}
