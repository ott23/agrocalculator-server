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
            Calculator calculator = t.getCalculator();
            if (channelMap.containsKey(calculator.getAddress())) {
                Channel channel = channels.find(channelMap.get(calculator.getAddress()));

                String key = null;
                if (!t.getType().equals(calculator.getName())) key = calculator.getEncodedKey();

                outputMessageComponent.sendMessage(new Message(t), channel, key);
            }
        });
    }

}
