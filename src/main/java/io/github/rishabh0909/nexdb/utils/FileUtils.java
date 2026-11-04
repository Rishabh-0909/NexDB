
package io.github.rishabh0909.nexdb.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletableFuture;

public class FileUtils {

    public static CompletableFuture<byte[]> readBytes(
            AsynchronousFileChannel asynchronousFileChannel,
            long position,
            int size) {

        CompletableFuture<byte[]> future = new CompletableFuture<>();
        ByteBuffer buffer = ByteBuffer.allocate(size);

        asynchronousFileChannel.read(
                buffer,
                position,
                buffer,
                new CompletionHandler<>() {

                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        attachment.flip();

                        byte[] data = new byte[attachment.limit()];
                        attachment.get(data);

                        future.complete(data);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        future.completeExceptionally(exc);
                    }
                }
        );

        return future;
    }

    public static CompletableFuture<Long> allocate(
            AsynchronousFileChannel asynchronousFileChannel,
            int size) throws IOException {

        CompletableFuture<Long> future = new CompletableFuture<>();
        long fileSize = asynchronousFileChannel.size();

        asynchronousFileChannel.write(
                ByteBuffer.allocate(size),
                fileSize,
                null,
                new CompletionHandler<Integer, Object>() {

                    @Override
                    public void completed(Integer result, Object attachment) {
                        future.complete(fileSize);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        future.completeExceptionally(exc);
                    }
                }
        );

        return future;
    }

    // Allocates space at a specified position in the file.
    public static CompletableFuture<Long> allocate(
            AsynchronousFileChannel asynchronousFileChannel,
            long position,
            int size) throws IOException {

        CompletableFuture<Long> future = new CompletableFuture<>();

        int readSize = (int) (asynchronousFileChannel.size() - position);

        allocate(asynchronousFileChannel, size).whenComplete(
                (allocatedBeginningPosition, throwable) -> {

                    if (throwable != null) {
                        future.completeExceptionally(throwable);
                        return;
                    }

                    FileUtils.readBytes(
                            asynchronousFileChannel,
                            position,
                            readSize
                    ).whenComplete((readBytes, throwable1) -> {

                        if (throwable1 != null) {
                            future.completeExceptionally(throwable1);
                            return;
                        }

                        FileUtils.write(
                                asynchronousFileChannel,
                                position,
                                new byte[readBytes.length]
                        ).whenComplete((integer, throwable2) -> {

                            if (throwable2 != null) {
                                future.completeExceptionally(throwable2);
                                return;
                            }

                            FileUtils.write(
                                    asynchronousFileChannel,
                                    position + size,
                                    readBytes
                            ).whenComplete((integer1, throwable3) -> {

                                if (throwable3 != null) {
                                    future.completeExceptionally(throwable3);
                                    return;
                                }

                                future.complete(position);
                            });
                        });
                    });
                }
        );

        return future;
    }

    public static CompletableFuture<Integer> write(
            AsynchronousFileChannel asynchronousFileChannel,
            long position,
            byte[] content) {

        CompletableFuture<Integer> future = new CompletableFuture<>();

        ByteBuffer byteBuffer = ByteBuffer.allocate(content.length);
        byteBuffer.put(content);
        byteBuffer.flip();

        asynchronousFileChannel.write(
                byteBuffer,
                position,
                null,
                new CompletionHandler<>() {

                    @Override
                    public void completed(Integer result, Object attachment) {
                        future.complete(result);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        future.completeExceptionally(exc);
                    }
                }
        );

        return future;
    }

    // Deletes a directory and all its contents recursively.
    public static void deleteDirectory(String directoryFilePath) throws IOException {

        Path directory = Paths.get(directoryFilePath);

        if (!Files.exists(directory)) {
            return;
        }

        Files.walkFileTree(directory, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(
                    Path file,
                    BasicFileAttributes attributes) throws IOException {

                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                    Path directory,
                    IOException exception) throws IOException {

                if (exception != null) {
                    throw exception;
                }

                Files.delete(directory);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
