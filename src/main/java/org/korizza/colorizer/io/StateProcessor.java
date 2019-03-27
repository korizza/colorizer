package org.korizza.colorizer.io;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import org.apache.log4j.Logger;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class StateProcessor {
    private static final Logger log = Logger.getLogger(StateProcessor.class);

    private enum State {
        READY,
        CHANGED,
    }

    private enum EventType {
        INSERTED_SYMBOL,
        REMOVED_SYMBOL,
        TIME_EXPIRED,
    }

    private static long TIMER_DELAY_SEC = 500L;

    private final ExecutorService renderEventPool;
    private Timer timer;
    private final ReadWriteLock stateLock;

    private final Document document;
    private final StyledDocument styledDocument;
    private final RangeMap<Integer, Integer> taskRanges;
    private int taskId;

    private State state = State.READY;

    private int startOff = 0;
    private int blockLength = 0;

    private final AtomicBoolean needClosing;

    public StateProcessor(Document document, StyledDocument styledDocument) {
        this.document = document;
        this.styledDocument = styledDocument;
        this.renderEventPool = Executors.newSingleThreadExecutor();
        stateLock = new ReentrantReadWriteLock();
        taskRanges = TreeRangeMap.create();
        taskId = 0;
        needClosing = new AtomicBoolean(false);
    }

    public void close() {
        if (needClosing.get()) {
            return;
        }

        needClosing.set(true);

        try {
            renderEventPool.shutdown();
            renderEventPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } finally {
            if (!renderEventPool.isTerminated()) {
                log.warn("Executed tasks will be canceled");
            }
            renderEventPool.shutdownNow();
            log.info("Executor has been closed");
        }
    }

    public void onInsertUpdate(DocumentEvent e) {
        processState(EventType.INSERTED_SYMBOL, e);
    }

    public void onRemoveUpdate(DocumentEvent e) {
        processState(EventType.REMOVED_SYMBOL, e);

    }

    private void startIdleTimer(Document doc) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                processState(EventType.TIME_EXPIRED, null);
            }
        }, TIMER_DELAY_SEC);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private int generateTaskId() {
        return ++taskId;
    }

    private void render(int offset, int lenght) {
        try {
            final String text = document.getText(offset, lenght);

            final ColorizerTask task = new ColorizerTask(generateTaskId(), styledDocument, text, offset, (x, y) -> {
                try {
                    stateLock.readLock().lock();
                    Integer lastSymbolTask = taskRanges.get(x);
                    return (needClosing.get() || lastSymbolTask == null || !lastSymbolTask.equals(y)) ? 1 : 0;
                } finally {
                    stateLock.readLock().unlock();
                }
            });

            taskRanges.put(Range.closed(offset, offset + lenght), task.getId());
            renderEventPool.submit(() -> {
                task.run();
            });
        } catch (BadLocationException e1) {
            e1.printStackTrace();
            System.exit(1);
        }
    }

    private void processState(EventType et, DocumentEvent e) {
        if (needClosing.get()) {
            return;
        }

        stateLock.writeLock().lock();
        try {
            switch (state) {
                case READY:
                    switch (et) {
                        case INSERTED_SYMBOL:
                            if (e.getLength() > 1) {
                                startOff = e.getOffset();
                                blockLength = e.getLength();
                                render(startOff, blockLength);
                                return;
                            } else {
                                startOff = e.getOffset();
                                blockLength = 1;
                                state = State.CHANGED;
                            }
                            break;
                        case REMOVED_SYMBOL:
                        case TIME_EXPIRED:
                            taskRanges.remove(Range.closed(e.getOffset(), e.getOffset() + e.getLength()));
                            return;
                    }
                    break;
                case CHANGED:
                    switch (et) {
                        case INSERTED_SYMBOL:
                            if (startOff > e.getOffset() || (startOff + blockLength < e.getOffset())) {
                                render((startOff > e.getOffset()) ? startOff + e.getLength() : startOff, blockLength);
                                startOff = e.getOffset();
                                blockLength = (e.getLength() > 1) ? e.getLength() : 1;
                            } else {
                                blockLength += (e.getLength() > 1) ? e.getLength() : 1;
                            }
                            break;
                        case REMOVED_SYMBOL:
                            int removedEndOff = e.getOffset() + e.getLength(), endOff = startOff + blockLength;
                            taskRanges.remove(Range.closed(e.getOffset(), e.getOffset() + e.getLength()));

                            if ((startOff >= e.getOffset()) && (removedEndOff >= endOff)) {
                                stopTimer();
                                state = State.READY;
                                return;
                            } else if ((startOff >= e.getOffset() && (removedEndOff < endOff))) {
                                startOff = e.getOffset();
                                blockLength = endOff - removedEndOff;
                            } else if ((startOff < e.getOffset()) && (removedEndOff < endOff)) {
                                blockLength = blockLength - e.getLength();
                            } else if ((startOff < e.getOffset()) && ((removedEndOff >= endOff))) {
                                blockLength = e.getOffset() - startOff;
                            } else {
                                return;
                            }
                            break;
                        case TIME_EXPIRED:
                            render(startOff, blockLength);
                            state = State.READY;
                            return;
                    }
                    break;
            }
            startIdleTimer(e.getDocument());
        } finally {
            stateLock.writeLock().unlock();
        }

    }

}