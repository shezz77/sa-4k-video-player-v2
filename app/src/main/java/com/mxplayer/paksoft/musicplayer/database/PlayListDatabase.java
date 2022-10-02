package com.mxplayer.paksoft.musicplayer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mxplayer.paksoft.musicplayer.MPConstants;
import com.mxplayer.paksoft.musicplayer.database.converter.CustomConverter;
import com.mxplayer.paksoft.musicplayer.model.PlayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters({CustomConverter.MusicListConverter.class})
@Database(entities = {PlayList.class}, version = MPConstants.DATABASE_VERSION, exportSchema = false)
public abstract class PlayListDatabase extends RoomDatabase {

    public static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private static volatile PlayListDatabase INSTANCE;

    public static PlayListDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PlayListDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PlayListDatabase.class, MPConstants.DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
            }
        }
        return INSTANCE;
    }
}
