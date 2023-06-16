package dist.group2;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import dist.group2.agents.AgentController;
import dist.group2.agents.SyncAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.net.InetAddress;

@SpringBootApplication
public class ClientApplication {
    public static ApplicationContext context;
    private DiscoveryClient discoveryClient;
    private ReplicationClient replicationClient;
    private SyncAgent syncAgent;
    private static AgentController agentController;
    Thread replicationthread;

    @Autowired
    public ClientApplication(DiscoveryClient discoveryClient) throws IOException {
        this.discoveryClient = discoveryClient;
        this.syncAgent = SyncAgent.getAgent();
        this.replicationClient = ReplicationClient.getInstance();


        // SHOULD BE REMOVED
        // FOR TESTING PURPOSES ONLY
        // TO TEST THE FAILURE AGENT
        ReplicationClient.setFailed(true);




        String name = InetAddress.getLocalHost().getHostName();
        String IPAddress = InetAddress.getLocalHost().getHostAddress();

        String multicastIP = "224.0.0.5";       // A random IP in the 224.0.0.0 to 239.255.255.255 range (reserved for multicast)
        InetAddress multicastGroup = InetAddress.getByName(multicastIP);
        int multicastPort = 4446;
        int unicastPortDiscovery = 4449;
        int namingPort = 8080;
        int fileUnicastPort = 4451;

        Communicator.init(multicastGroup, multicastPort, fileUnicastPort, multicastIP, unicastPortDiscovery);
        this.discoveryClient.init(name, IPAddress, unicastPortDiscovery, namingPort);
        NamingClient.setName(name);

        System.out.println("<---> " + name + " Instantiated with IP " + IPAddress + " <--->");

    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        Logger logger = LoggerFactory.getLogger(ClientApplication.class);
        logger.info("Run method is executed");
        this.discoveryClient.bootstrap();
        NamingClient.setBaseUrl(this.discoveryClient.getBaseUrl());
        try {
            this.replicationClient.createDirectories();
            this.replicationClient.addFiles();
            this.replicationClient.setFileDirectoryWatchDog();
            this.replicationClient.replicateFiles();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        replicationthread = new Thread(replicationClient);
        replicationthread.start();
    }
    @PreDestroy
    public void shutdown() {
        replicationthread.stop();
        if (ReplicationClient.isFailed()) {
            failure();
        }
    }

    public static void failure() {
        ReplicationClient.setFailed(true);      // Prevent shutdown procedure from executing, as the failure agents handles it
        agentController.startFailureAgent(DiscoveryClient.getCurrentID(), DiscoveryClient.getCurrentID());
        SpringApplication.exit(context);
    }

    public static void main(String[] args) {
        // Run Client
        context = SpringApplication.run(ClientApplication.class, args);
    }
}