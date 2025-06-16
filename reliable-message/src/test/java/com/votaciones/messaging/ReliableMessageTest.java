package com.votaciones.messaging;

import com.votaciones.messaging.model.VotingMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReliableMessageTest {
    private VoteQueue voteQueue;
    private ReliableMessenger messenger;
    private MessageResender resender;
    private ReliableMessageAPI api;
    private AtomicBoolean sentFlag;

    @BeforeEach
    public void setUp() {
        voteQueue = new VoteQueue();
        sentFlag = new AtomicBoolean(false);
        ReliableMessenger.MessageSender sender = vote -> {
            sentFlag.set(true);
            return true;
        };
        messenger = new ReliableMessenger(voteQueue, sender);
        resender = new MessageResender(sender);
        api = new ReliableMessageAPI(voteQueue);
    }

    @AfterEach
    public void tearDown() {
        messenger.shutdown();
        resender.shutdown();
    }

    @Test
    public void testVoteQueue() throws InterruptedException {
        VotingMessage vote = new VotingMessage("content", "type", "source");
        voteQueue.addVote(vote);
        VotingMessage retrieved = voteQueue.getVote();
        Assertions.assertEquals(vote.getContent(), retrieved.getContent());
    }

    @Test
    public void testReliableMessengerSendsVote() throws InterruptedException {
        VotingMessage vote = new VotingMessage("content2", "type", "source");
        voteQueue.addVote(vote);
        messenger.start();
        Thread.sleep(500); // Give time for messenger to process
        Assertions.assertTrue(sentFlag.get());
    }

    @Test
    public void testReliableMessageAPI() throws InterruptedException {
        VotingMessage vote = new VotingMessage("content3", "type", "source");
        api.publishEvent(vote);
        VotingMessage retrieved = api.getVote();
        Assertions.assertEquals(vote.getContent(), retrieved.getContent());
    }

    @Test
    public void testMessageResenderRetries() throws InterruptedException {
        AtomicBoolean firstTry = new AtomicBoolean(true);
        ReliableMessenger.MessageSender flakySender = vote -> {
            if (firstTry.getAndSet(false)) return false;
            sentFlag.set(true);
            return true;
        };
        MessageResender testResender = new MessageResender(flakySender);
        VotingMessage vote = new VotingMessage("content4", "type", "source");
        testResender.addUnconfirmed("id1", vote);
        testResender.start();
        Thread.sleep(6000); // Wait for retry
        Assertions.assertTrue(sentFlag.get());
        testResender.shutdown();
    }
} 