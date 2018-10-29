package me.schlaubi.votebot.core.entities;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Transient;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.schlaubi.votebot.io.database.Cassandra;
import me.schlaubi.votebot.util.NameThreadFactory;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class DatabaseEntity<T> {

    @Transient
    private final static ExecutorService executor = Executors.newCachedThreadPool(new NameThreadFactory("Database"));
    @Transient
    private final Mapper<T> mapper;
    @Transient
    private final String logPrefix;
    @Column(name = "id")
    @Getter
    private long entityId;

    public DatabaseEntity(Class<T> clazz, Cassandra cassandra, String logPrefix) {
        this.logPrefix = logPrefix;
        MappingManager mappingManager = new MappingManager(cassandra.getSession());
        this.mapper = mappingManager.mapper(clazz);
    }

    public final void delete(T entity) {
        ListenableFuture<Void> future = mapper.deleteAsync(entity);
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                log.debug(String.format("[Database] %s Entity with id %s got deleted", logPrefix, entityId));
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                log.error(String.format("[Database] %s An error occurred while deleting entity %s", logPrefix, entityId), throwable);
            }
        }, executor);
    }

    public final void save(T entity) {
        ListenableFuture<Void> future = mapper.saveAsync(entity);
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Void aVoid) {
                log.debug(String.format("[Database] %s Entity with id %s got saved", logPrefix, entityId));

            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
               log.error(String.format("[Database] %s An error occurred while deleting entity %s", logPrefix, entityId), throwable);
            }
        }, executor);
    }
}
