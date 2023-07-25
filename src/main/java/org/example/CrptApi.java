package org.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class CrptApi {
    private final int requestLimit;
    private final long requestIntervalMillis;
    private final AtomicInteger requestCounter;
    private final Lock lock;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.requestLimit = requestLimit;
        this.requestIntervalMillis = timeUnit.toMillis(1);
        this.requestCounter = new AtomicInteger(0);
        this.lock = new ReentrantLock();
    }

    public void createDocument(Object document, String signature) {
        try {
            lock.lock();
            int currentRequestCount = requestCounter.incrementAndGet();
            if (currentRequestCount > requestLimit) {
                Thread.sleep(requestIntervalMillis);
                currentRequestCount = requestCounter.decrementAndGet();
            }
            System.out.println("API request - document: " + document + ", signature: " + signature);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) {
        CrptApi api = new CrptApi(TimeUnit.SECONDS, 3);

        new Thread(() -> api.createDocument("Document 1", "Signature 1")).start();
        new Thread(() -> api.createDocument("Document 2", "Signature 2")).start();
        new Thread(() -> api.createDocument("Document 3", "Signature 3")).start();
        new Thread(() -> api.createDocument("Document 4", "Signature 4")).start();
        new Thread(() -> api.createDocument("Document 5", "Signature 5")).start();
    }
}