package net.tngroup.acserver.nodeserver.components;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import net.tngroup.acserver.databases.h2.models.Node;
import net.tngroup.acserver.databases.h2.models.Message;
import net.tngroup.acserver.databases.h2.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.Map;

@Component
public class TaskComponent extends Thread {

    private TaskService taskService;
    private StatusComponent statusComponent;
    private OutputMessageComponent outputMessageComponent;

    @Autowired
    public TaskComponent(TaskService taskService,
                         StatusComponent statusComponent,
                         OutputMessageComponent outputMessageComponent) {
        this.taskService = taskService;
        this.statusComponent = statusComponent;
        this.outputMessageComponent = outputMessageComponent;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                handleTasks();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleTasks() {
        Map<SocketAddress, ChannelId> channelMap = statusComponent.getChannelMap();
        ChannelGroup channels = statusComponent.getChannels();

        taskService.getAllByConfirmed(false).forEach(t -> {
            Node node = t.getNode();
            if (channelMap.containsKey(node.getAddress())) {
                Channel channel = channels.find(channelMap.get(node.getAddress()));

                String key = null;
                if (!t.getType().equals("key")) key = node.getKey();

                outputMessageComponent.sendMessage(new Message(t), channel, key);
            }
        });
    }

}
