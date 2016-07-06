/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.topo.app.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.incubator.net.PortStatisticsService;
import org.onosproject.incubator.net.intf.InterfaceService;
import org.onosproject.net.*;
import org.onosproject.net.device.DefaultPortDescription;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.flow.*;
import org.onosproject.net.host.HostService;
import org.onosproject.net.intent.IntentService;
import org.onosproject.net.link.LinkService;
import org.onosproject.net.statistic.FlowStatisticService;
import org.onosproject.net.statistic.Load;
import org.onosproject.net.statistic.StatisticService;
import org.onosproject.net.statistic.StatisticStore;
import org.onosproject.net.topology.PathService;
import org.onosproject.net.topology.TopologyService;
import org.onosproject.ui.model.ServiceBundle;
import org.onosproject.ui.topo.BiLink;
import org.topo.app.MyService;

import java.lang.reflect.Array;
import java.util.*;


import org.json.JSONArray;
import org.json.JSONObject;

import static com.google.common.collect.Lists.asList;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;


/**
 * Sample Apache Karaf CLI command
 */
@Command(scope = "topo.app", name = "add-flow-rule",
         description = "Sample Topology test")
public class AppCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "one", description = "One host ID",
            required = true, multiValued = false)
    String one = null;

    @Argument(index = 1, name = "two", description = "Another host ID",
            required = true, multiValued = false)
    String two = null;


    public static final int PRIORITY=10;
    public static final int TIME_OUT=50;
    @Override
    protected void execute() {

//        MyService test = get(MyService.class);

//        print("%s", test.getTopology());

//        IntentService intentService = get(IntentService.class);
//        Link link=get(Link.class);
//        DeviceService deviceService = get(DeviceService.class);
//      print("%s", deviceService.getAvailableDevices());
//        HostService hostService = get(HostService.class);
//        TopologyService topologyService=get(TopologyService.class);

        HostId oneId = HostId.hostId(one);
        HostId twoId = HostId.hostId(two);
        DeviceService deviceService=get(DeviceService.class);
        HostService hostService = get(HostService.class);
        DeviceId DvcOneId = hostService.getHost(oneId).location().deviceId();
        DeviceId DvcTwoId = hostService.getHost(twoId).location().deviceId();
        TopologyService topologyService = get(TopologyService.class);
        CoreService coreService = get(CoreService.class);
        StatisticService service = get(StatisticService.class);
        ApplicationId appId = coreService.registerApplication("onos.add.flow.rule");
        print("%s",deviceService.getPorts(DvcOneId).get(2).portSpeed());



//        //print("%s\n", topologyService.getDisjointPaths(topologyService.currentTopology(),DvcOneId,DvcTwoId));
//        PathService te = get(PathService.class);
        //print("%s",te.getDisjointPaths(DvcOneId,DvcTwoId));
        Set<DisjointPath> p = topologyService.getDisjointPaths(
                topologyService.currentTopology(), DvcOneId, DvcTwoId);
//        Set<DisjointPath> pp = te.getDisjointPaths(hostService.getHost(oneId).location().elementId(),
//                hostService.getHost(twoId).location().elementId());

//        Map<DeviceId, Set<Path>> srcPath = new HashMap<>();
//        Set<Path> ppp = te.getPaths(hostService.getHost(oneId).location().elementId(),
//                hostService.getHost(twoId).location().elementId());



//        Set<Path> p1 = topologyService.getPaths(topologyService.currentTopology(), DvcOneId, DvcTwoId);
//        LinkService linkService = get(LinkService.class);
//        print("%s\n",linkService.getDeviceLinks(DvcOneId));
//        StatisticService statisticService=get(StatisticService.class);
//        //print("%s\n", deviceService.getPorts(DvcOneId));
//        FlowStatisticService flowStatisticService=get(FlowStatisticService.class);
//        StatisticStore statisticStore = get(StatisticStore.class);
//        ServiceBundle serviceBundle = get(ServiceBundle.class);
//        Load load=get(Load.class);
//        BiLink biLink=get(BiLink.class);
//        PortStatisticsService portStatisticsService = get(PortStatisticsService.class);
//        ConnectPoint connectPoint = get(ConnectPoint.class);
//        print("%s",connectPoint.deviceId());
//        ConnectPoint con=ConnectPoint.hostConnectPoint(one);
//        print("%s",con.port());
//        ConnectPoint connectPoint =get(ConnectPoint.class);
//        print("%s", connectPoint.deviceId());

//        ConnectPoint cp = new ConnectPoint(DvcOneId,PortNumber.portNumber(4));
        double lc=255,cost=0;
        long portSpeed = 0;
        long loadSrcPort = 0;
        long loadDstPort = 0;

        for(DisjointPath test: p) {

            ConnectPoint cpSrc = new ConnectPoint(test.primary().src().elementId(),
                    PortNumber.portNumber(test.primary().src().port().toLong()));
            Load srcPortLoad=service.load(cpSrc);
            ConnectPoint cpDst = new ConnectPoint(test.primary().dst().elementId(),
                    PortNumber.portNumber(test.primary().dst().port().toLong()));
            Load dstPortLoad=service.load(cpDst);

            //print("\nSrc Port Load on %s Kbps ", ((srcPortLoad.rate()*8)/1024)*2);
            //print("\nDst Port Load on %s Kbps", ((dstPortLoad.rate()*8)/1024)*2);
            loadSrcPort=((srcPortLoad.rate()*8)/1024)*2;
            loadDstPort = ((dstPortLoad.rate()*8)/1024)*2;
            portSpeed = deviceService.getPort(test.src().deviceId(), test.src().port()).portSpeed();

            //print("\nPortspeed = %s",deviceService.getPort(test.src().deviceId(), test.src().port()).portSpeed());

            cost = test.backup().cost();
            if(cost<lc){
                lc = cost;
                if(maxLoad(loadSrcPort,loadDstPort)>(portSpeed*0.02)){
                    backupPath(test.backup().links(), oneId, twoId, appId);

                    installFlow(test.src().deviceId(), test.backup().src().port(),
                            hostService.getHost(oneId).location().port(),
                            oneId.mac(), twoId.mac(), appId);
                    installFlow(test.src().deviceId(), hostService.getHost(oneId).location().port(),
                            test.backup().src().port(),
                            twoId.mac(), oneId.mac(), appId);
                    installFlow(test.dst().deviceId(), test.backup().dst().port(),
                            hostService.getHost(twoId).location().port(),
                            twoId.mac(), oneId.mac(), appId);
                    installFlow(test.dst().deviceId(), hostService.getHost(twoId).location().port(),
                            test.backup().dst().port(),
                            oneId.mac(), twoId.mac(), appId);
                    print("Backup Path %s", maxLoad(loadSrcPort,loadDstPort));
                }
                else {
                    backupPath(test.primary().links(), oneId, twoId, appId);

                    installFlow(test.src().deviceId(), test.primary().src().port(),
                            hostService.getHost(oneId).location().port(),
                            oneId.mac(), twoId.mac(), appId);
                    installFlow(test.src().deviceId(), hostService.getHost(oneId).location().port(),
                            test.primary().src().port(),
                            twoId.mac(), oneId.mac(), appId);
                    installFlow(test.dst().deviceId(), test.primary().dst().port(),
                            hostService.getHost(twoId).location().port(),
                            twoId.mac(), oneId.mac(), appId);
                    installFlow(test.dst().deviceId(), hostService.getHost(twoId).location().port(),
                            test.primary().dst().port(),
                            oneId.mac(), twoId.mac(), appId);
                    print("primary path %s", maxLoad(loadSrcPort,loadDstPort));
                }

            }
            //print("%s = %s",test.backup().src().elementId(),test.backup().src().port());
            //print("%s = %s",test.backup().dst().elementId(),test.backup().dst().port());


        }

/*        for(Path test2: p1){
            print("\n\n\n%s", test2);
        }*/

       // print("Intents = %s",intentService.getIntents());
        //topologyService.getPaths(topologyService.currentTopology(),DeviceId.NONE,DeviceId.NONE);
        //print("%s",hostService.getHosts());
        //while(true){
//        Load load1=service.load(cp);
//        print("Load on %s -> %s", cp, load1);
       // }
    }

    private long maxLoad(long a, long b){
        if(a<0){
            return b;
        }
        if(b<0){
            return a;
        }
        return  a > b ? a : b;
    }

    private void backupPath(List<Link> links, HostId oneId, HostId twoId, ApplicationId appId){

        HostService hostService = get(HostService.class);
        PortNumber lastPort=links.get(0).dst().port();
        ElementId lastId=links.get(0).dst().elementId();

        for(Link l: links){
            if(lastId.equals(l.src().elementId())){
                installFlow(l.src().deviceId(), l.src().port(), lastPort,
                        oneId.mac(), twoId.mac(), appId);
                installFlow(l.src().deviceId(), lastPort, l.src().port(),
                        twoId.mac(), oneId.mac(), appId);
                lastId = l.dst().elementId();
                lastPort = l.dst().port();
            }

            //print("%s = %s",l.src().elementId(),l.src().port());
            //print("%s = %s",l.dst().elementId(),l.dst().port());


        }

    }

    private void installFlow(DeviceId deviceId, PortNumber outPort, PortNumber inPort,
                             MacAddress srcMac, MacAddress dstMac, ApplicationId appId){

        FlowRuleService flowService = get(FlowRuleService.class);

        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .setOutput(outPort).build();

        TrafficSelector.Builder sbuilder;
        FlowRuleOperations.Builder rules = FlowRuleOperations.builder();

        sbuilder = DefaultTrafficSelector.builder();

        sbuilder.matchEthSrc(srcMac)
                .matchEthDst(dstMac).matchInPort(inPort);

        FlowRule addRule = DefaultFlowRule.builder()
                .forDevice(deviceId)
                .withSelector(sbuilder.build())
                .withTreatment(treatment)
                .withPriority(PRIORITY)
                .fromApp(appId)
                .makeTemporary(TIME_OUT)
                .build();

        rules.add(addRule);
        flowService.apply(rules.build());

    }
    /*@Override
    protected void execute() {
        print("Hello %s", "World");
    }*/

}
