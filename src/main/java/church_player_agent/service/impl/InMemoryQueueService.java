// package church_player_agent.service.impl;

// import church_player_agent.model.QueueItem;
// import church_player_agent.model.Track;
// import church_player_agent.service.FileScannerService;
// import church_player_agent.service.FileStorageService;
// import church_player_agent.service.PlayerService;
// import church_player_agent.service.QueueService;
// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import java.io.IOException;
// import java.time.LocalDateTime;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Queue;
// import java.util.UUID;
// import java.util.concurrent.atomic.AtomicBoolean;
// import java.util.concurrent.atomic.AtomicReference;

// @Service
// @RequiredArgsConstructor
// public class InMemoryQueueService implements QueueService {

//     private final FileScannerService fileScannerService;
//     private final PlayerService playerService;
//     private final FileStorageService fileStorageService;

//     private final Queue<QueueItem> queue = new LinkedList<>();
//     private final AtomicReference<QueueItem> currentPlaying = new AtomicReference<>();
//     private final AtomicBoolean finishingTrack = new AtomicBoolean(false);

//     @PostConstruct
//     public void init() {
//         System.out.println("InMemoryQueueService initialized");

//         playerService.setOnTrackEnded(() -> {
//             System.out.println("Track ended callback triggered");
//             finishCurrentTrack();
//         });
//     }

//     private void finishCurrentTrack() {
//         if (!finishingTrack.compareAndSet(false, true)) {
//             System.out.println("finishCurrentTrack skipped: already processing");
//             return;
//         }

//         QueueItem finished = currentPlaying.getAndSet(null);

//         if (finished == null) {
//             System.out.println("No current playing item found");
//             finishingTrack.set(false);
//             return;
//         }

//         new Thread(() -> {
//             try {
//                 Thread.sleep(1000);
//                 System.out.println("Moving file to played: " + finished.getAbsolutePath());
//                 fileStorageService.moveToPlayed(finished.getAbsolutePath());
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//                 System.err.println("Move thread interrupted");
//             } catch (Exception e) {
//                 System.err.println("Failed moving finished track: " + e.getMessage());
//                 e.printStackTrace();
//             } finally {
//                 finishingTrack.set(false);
//             }
//         }, "file-move-thread").start();
//     }

//     @Override
//     public synchronized QueueItem addToQueue(String fileName) throws IOException {
//         Track track = fileScannerService.scanIncoming().stream()
//                 .filter(item -> item.getFileName().equalsIgnoreCase(fileName))
//                 .findFirst()
//                 .orElseThrow(() -> new IllegalArgumentException("File not found in incoming folder: " + fileName));

//         QueueItem queueItem = QueueItem.builder()
//                 .id(UUID.randomUUID())
//                 .fileName(track.getFileName())
//                 .absolutePath(track.getAbsolutePath())
//                 .queuedAt(LocalDateTime.now())
//                 .build();

//         queue.offer(queueItem);
//         return queueItem;
//     }

//     @Override
//     public synchronized List<QueueItem> getQueue() {
//         return List.copyOf(queue);
//     }

//     @Override
//     public synchronized QueueItem playNext() {
//         QueueItem nextItem = queue.poll();

//         if (nextItem == null) {
//             throw new IllegalStateException("Queue is empty");
//         }

//         System.out.println("Playing next item: " + nextItem.getFileName());

//         currentPlaying.set(nextItem);
//         finishingTrack.set(false);
//         playerService.play(nextItem.getAbsolutePath());
//         return nextItem;
//     }

//     @Override
//     public QueueItem getCurrentPlaying() {
//         return currentPlaying.get();
//     }

//     @Override
//     public synchronized QueueItem removeFromQueue(String id) {
//         QueueItem found = queue.stream()
//                 .filter(item -> item.getId().toString().equals(id))
//                 .findFirst()
//                 .orElseThrow(() -> new IllegalArgumentException("Queue item not found"));

//         queue.remove(found);
//         return found;
//     }

//     @Override
//     public synchronized void skipCurrent() {
//         QueueItem current = currentPlaying.getAndSet(null);

//         if (current == null) {
//             throw new IllegalStateException("No track is currently playing");
//         }

//         System.out.println("Skipping current track: " + current.getFileName());

//         playerService.stop();
//         finishingTrack.set(false);

//         if (!queue.isEmpty()) {
//             QueueItem next = queue.poll();
//             currentPlaying.set(next);
//             playerService.play(next.getAbsolutePath());
//         }
//     }

//     @Override
//     public synchronized void clearQueue() {
//         queue.clear();
//     }
// }