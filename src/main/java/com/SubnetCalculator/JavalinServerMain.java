package com.SubnetCalculator;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.jetty.JettyStatisticsCollector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class JavalinServerMain {
    public static final Logger logger = LoggerFactory.getLogger(JavalinServerMain.class);

    public static void main(String[] args) throws IOException {
        // Prometheus Monitoring
        StatisticsHandler statisticsHandler = new StatisticsHandler();

        Javalin app = Javalin.create(config ->
                config.server(() -> {
                    Server server = new Server();
                    server.setHandler(statisticsHandler);
                    config.addStaticFiles("/public", Location.CLASSPATH);
                    // config.enableDevLogging();
                    return server;
                })
        ).start(7080);

        initializePrometheus(statisticsHandler);

        app.post("/sameSubnet", ctx -> {
            ctx.json(executeSameSubnet(ctx.formParam("SameSubFirstIP"), ctx.formParam("SameSubSecondIP"), ctx.formParam("SameSubSubnet")).toString());
        });

        app.post("/calcCIDR", ctx -> {
            ctx.json(executeSubnetCalculator(ctx.formParam("inputIP"), ctx.formParam("inputSuffix")).toString());
        });
    }

    private static JSONObject executeSameSubnet(String sameSubFirstIP, String sameSubSecondIP, String SubnetMask) {
        JSONObject jo = SameSubnetChecker.inputValidityCheck(sameSubFirstIP, sameSubSecondIP, SubnetMask);
        if (jo.getBoolean("validityResult"))
            return jo.put("isSameSubNet", SameSubnetChecker.inSameSubnet(sameSubFirstIP, sameSubSecondIP, SubnetMask));
        else
            return jo;
    }

    private static JSONObject executeSubnetCalculator(String inputIP, String inputSuffix) {
        JSONObject jo = CIDRCalculator.inputValidityCheck(inputIP, inputSuffix);
        if (jo.getBoolean("validityResult")){
            CIDRCalculator.SubnetInfo sInf = CIDRCalculator.calculateSubnetInfo(inputIP, inputSuffix);
            return sInfToJSON(jo, sInf);
        } else
            return jo;
    }

    private static JSONObject sInfToJSON (JSONObject jo, CIDRCalculator.SubnetInfo sInf) {
        jo.put("SubnetMask", sInf.Subnetmask);
        jo.put("NetId", sInf.NetId);
        jo.put("First", sInf.First);
        jo.put("Broadcast", sInf.Broadcast);
        jo.put("Last", sInf.Last);
        jo.put("Available", sInf.Available);
        return jo;
    }

    // monitoring
    private static void initializePrometheus(StatisticsHandler statisticsHandler) throws IOException {
        new JettyStatisticsCollector(statisticsHandler).register();
        LoggerFactory.getLogger("Main").info("Prometheus is listening on: http://localhost:7070");
        HTTPServer prometheusServer = new HTTPServer(7070);
    }
}
