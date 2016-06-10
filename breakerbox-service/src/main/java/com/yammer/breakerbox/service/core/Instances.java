package com.yammer.breakerbox.service.core;

import com.google.common.collect.ImmutableList;
import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.plugins.PluginsFactory;
import com.yammer.breakerbox.store.ServiceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class Instances {
    private static final Logger LOGGER = LoggerFactory.getLogger(Instances.class);

    public static Collection<Instance> instances() {
        try {
            return PluginsFactory.getInstanceDiscovery().getInstanceList();
        } catch (Exception err) {
            LOGGER.warn("Could not fetch clusters dynamically", err);
        }

        return ImmutableList.of();
    }

    public static Set<String> clusters() {
        return instances()
                .stream()
                .map(Instance::getCluster)
                .sorted()
                .collect(Collectors.toSet());
    }

    public static Set<String> noMetaClusters(Set<String> specifiedMetaClusters) {
        return instances()
                .stream()
                .filter((instance) -> !specifiedMetaClusters.contains(instance.getCluster().toUpperCase()))
                .map(Instance::getCluster)
                .sorted()
                .collect(Collectors.toSet());
    }

    public static Set<Instance> instances(ServiceId serviceId) {
        return instances()
                .stream()
                .filter((instance) -> instance.getCluster().equals(serviceId.getId()))
                .sorted()
                .collect(Collectors.toSet());
    }

    public static URI toUri(Instance instance) {
        return URI.create(instance.getHostname());
    }
}