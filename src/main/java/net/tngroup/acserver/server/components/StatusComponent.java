package net.tngroup.acserver.server.components;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import net.tngroup.acserver.models.Calculator;
import net.tngroup.acserver.models.CalculatorStatus;
import net.tngroup.acserver.services.CalculatorService;
import net.tngroup.acserver.services.CalculatorStatusService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatusComponent {

    private Logger logger = LogManager.getFormatterLogger("ConsoleLogger");

    private CalculatorService calculatorService;
    private CalculatorStatusService calculatorStatusService;

    @Getter
    private ChannelGroup channels;
    @Getter
    private Map<SocketAddress, ChannelId> channelMap;

    @Autowired
    public StatusComponent(CalculatorService calculatorService,
                           CalculatorStatusService calculatorStatusService) {
        this.calculatorService = calculatorService;
        this.calculatorStatusService = calculatorStatusService;

        // Make all calculator not connection on init
        calculatorService.updateAllConnection(false);
        calculatorService.updateAllStatus(false);

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

        // Set status "Not connection" if calculator exists
        Calculator calculator = calculatorService.getByAddressAndConnection(channel.remoteAddress(), true);
        if (calculator != null) {
            calculatorService.updateConnectionById(calculator.getId(), false);
            calculatorStatusService.add(new CalculatorStatus(calculator, "DISCONNECTED", new Date()));
        }
    }

    /*
    Check name
    */
    Calculator checkCalculator(String name, SocketAddress address) {
        // Make all calculator with the same address archived
        calculatorService.updateAllArchiveByAddress(address, true);

        Calculator calculator = calculatorService.getByName(name);

        if (calculator != null) {
            if (!calculator.getAddress().equals(address)) calculator.setAddress(address);
        } else {
            calculator = new Calculator();
            calculator.setName(name);
            calculator.setAddress(address);
        }

        calculator.setConnection(true);
        calculator.setArchive(false);

        calculatorService.addOrUpdate(calculator);

        return calculator;
    }





}