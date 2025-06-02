package com.sistemaelectoral.common;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Properties;

public class ThreadPoolConfig {
    public static void configureThreadPool(Communicator communicator, int minSize, int maxSize) {
        Properties props = communicator.getProperties();
        props.setProperty("Ice.ThreadPool.Server.Size", String.valueOf(minSize));
        props.setProperty("Ice.ThreadPool.Server.SizeMax", String.valueOf(maxSize));
    }
} 