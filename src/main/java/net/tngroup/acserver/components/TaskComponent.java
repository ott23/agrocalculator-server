package net.tngroup.acserver.components;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import net.tngroup.acserver.server.components.OutputMessageComponent;
import net.tngroup.acserver.server.components.StatusComponent;
import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.models.Message;
import net.tngroup.acserver.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.Map;

@Component
public class TaskComponent {

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

    public void init() {
        new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    handleTasks();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void handleTasks() {
        Map<SocketAddress, ChannelId> channelMap = statusComponent.getChannelMap();
        ChannelGroup channels = statusComponent.getChannels();

        taskService.getAllByConfirmed(false).forEach(t -> {
            Calculator calculator = t.getCalculator();
            if (channelMap.containsKey(calculator.getAddress())) {
                Channel channel = channels.find(channelMap.get(calculator.getAddress()));

                String key = null;
                if (!t.getType().equals("key")) key = calculator.getKey();

                outputMessageComponent.sendMessage(new Message(t), channel, key);
            }
        });
    }

}
