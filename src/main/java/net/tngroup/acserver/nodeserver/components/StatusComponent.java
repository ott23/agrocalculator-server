package net.tngroup.acserver.nodeserver.components;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import net.tngroup.acserver.databases.h2.models.Node;
import net.tngroup.acserver.databases.h2.models.NodeStatus;
import net.tngroup.acserver.databases.h2.services.NodeService;
import net.tngroup.acserver.databases.h2.services.NodeStatusService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatusComponent {

    private Logger logger = LogManager.getFormatterLogger("CommonLogger");

    private NodeService nodeService;
    private NodeStatusService nodeStatusService;

    @Getter
    private ChannelGroup channels;
    @Getter
    private Map<SocketAddress, ChannelId> channelMap;

    @Autowired
    public StatusComponent(NodeService nodeService,
                           NodeStatusService nodeStatusService) {
        this.nodeService = nodeService;
        this.nodeStatusService = nodeStatusService;

        // Make all node not connection on init
        nodeService.updateAllConnection(false);
        nodeService.updateAllStatus(false);

        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelMap = new HashMap<>();
    }

    /*
    Event of connection
     */
    public void connected(Channel channel) {
        logger.info("Client with address '%s': connected", channel.remoteAddress().toString());

        // Add to channel arrays
        channels.add(channel);
        channelMap.put(channel.remoteAddress(), channel.id());
    }

    /*
    Event of disconnection
     */
    public void disconnected(Channel channel) {
        logger.info("Client with address '%s': disconnected", channel.remoteAddress().toString());

        // Remove from channel arrays
        channels.remove(channel);
        channelMap.remove(channel.remoteAddress());

        // Set status "Not connection" if node exists
        Node node = nodeService.getByAddressAndConnection(channel.remoteAddress(), true);
        if (node != null) {
            nodeService.updateConnectionById(node.getId(), false);
            nodeStatusService.save(new NodeStatus("DISCONNECTED", new Date(), node));
        }
    }

    /*
    Check code
    */
    Node checkNode(String code, String type, InetSocketAddress address) {
        // Make all node with the same address archived
        nodeService.updateAllArchiveByAddress(address, true);

        Node node = nodeService.getByCode(code);

        if (node != null) {
            if (!node.getAddress().equals(address)) node.setAddress(address);
        } else {
            node = new Node();
            node.setCode(code);
            node.setType(type);
            node.setAddress(address);
        }

        node.setConnection(true);
        node.setArchive(false);

        nodeService.save(node);

        return node;
    }





}