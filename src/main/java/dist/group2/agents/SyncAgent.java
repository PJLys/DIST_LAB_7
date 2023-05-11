package dist.group2.agents;

import org.springframework.data.util.Pair;

import java.io.Serializable;
import java.util.LinkedList;

public class SyncAgent implements Runnable, Serializable {
    private final LinkedList<Pair<String,Boolean>> networkFiles = new LinkedList<>();
    @Override
    public void run() {
        this.checkfiles()
    }

    private void checkfiles() {
        boolean updatelocal = false;
        //Loop through files
        if (notfound) {
            networkFiles.add(new Pair(filename, locked));
            updatelocal = true;
        }

        if (updatelocal)
            this.updateNode();

    }

    private void updateNode() {
        // Update the local node list
    }
}
