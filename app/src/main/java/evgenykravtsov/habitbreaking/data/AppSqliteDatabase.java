package evgenykravtsov.habitbreaking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import evgenykravtsov.habitbreaking.data.Event.StatisticSavedEvent;
import evgenykravtsov.habitbreaking.domain.Utils;
import evgenykravtsov.habitbreaking.domain.model.StatisticDataEntity;
import evgenykravtsov.habitbreaking.domain.os.AppController;
import evgenykravtsov.habitbreaking.network.event.DownloadDataEvent;

public class AppSqliteDatabase extends SQLiteOpenHelper implements StatisticDataStorage {

    public static final String DATABASE_NAME = "app_sqlite_database";

    private static final int DATABASE_VERSION = 1;

    // Statistic table
    private static final String STATISTIC_TABLE_NAME = "statistic_table";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_COUNT = "count";
    private static final String COLUMN_SEND_STATUS = "send_status";

    private static AppSqliteDatabase instance;

    private SQLiteDatabase database;
    private boolean isRestored;

    //// CONSTRUCTORS

    private AppSqliteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    public static AppSqliteDatabase getInstance() {
        if (instance == null) {
            instance = new AppSqliteDatabase(AppController.getAppContext());
        }
        return instance;
    }

    //// STATISTIC DATA STORAGE INTERFACE

    @Override
    public void registerConsumption() {
        long consumptionDate = Utils
                .getDayFromDate(Calendar.getInstance().getTimeInMillis());
        int consumption = getConsumptionByDate(consumptionDate);
        boolean isNewDate = consumption == 0;

        if (isNewDate) {
            if (isStatisticPeriodLimitReached()) {
                deleteFirstRowFromTable();
            }
            markLastRowForSync();
            insertNewRow(consumptionDate);
        } else {
            incrementConsumption(consumptionDate, consumption);
        }
    }

    @Override
    public float getAverageConsumption() {
        Cursor cursor = database.rawQuery("SELECT " + COLUMN_COUNT + " FROM " +
                STATISTIC_TABLE_NAME, null);
        cursor.moveToFirst();
        float totalConsumption = 0;

        while (!cursor.isAfterLast()) {
            totalConsumption += cursor.getInt(0);
            cursor.moveToNext();
        }

        cursor.close();

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Average Consumption = " +
                totalConsumption / StatisticDataStorage.MAX_RECORDS);

//        int result = totalConsumption / StatisticDataStorage.MAX_RECORDS;
//        return result == 0 ? 1 : result;

        return totalConsumption / StatisticDataStorage.MAX_RECORDS;
    }

    @Override
    public void displayStatisticStorageContent() {
        Log.d(AppController.APP_TAG, " **** Statistic Storage Content **** ");
        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Log.d(AppController.APP_TAG, "DATE - " + cursor.getInt(0) +
                    " | COUNT - " + cursor.getInt(1) +
                    " | SYNC_STATUS - " + cursor.getInt(2));
            cursor.moveToNext();
        }

        cursor.close();
    }

    @Override
    public List<StatisticDataEntity> getUnsentStatisticData() {

        List<StatisticDataEntity> unsentStatisticData = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME +
                " WHERE " + COLUMN_SEND_STATUS + " =  1", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            unsentStatisticData.add(new StatisticDataEntity(cursor.getLong(0), cursor.getInt(1)));
            cursor.moveToNext();
        }
        cursor.close();

        return unsentStatisticData;
    }

    @Override
    public void confirmStatisticDataSync(long date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SEND_STATUS, 0);
        database.update(STATISTIC_TABLE_NAME, contentValues, COLUMN_DATE + " = " + date, null);
    }

    @Override
    public void clearStatisticStorage() {
        database.delete(STATISTIC_TABLE_NAME, null, null);
    }

    @Override
    public void saveStatistic(List<StatisticDataEntity> entities) {
        for (StatisticDataEntity entity : entities) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_DATE, entity.getDate());
            contentValues.put(COLUMN_COUNT, entity.getCount());
            database.insert(STATISTIC_TABLE_NAME, null, contentValues);
        }
        isRestored = true;

        EventBus.getDefault().post(new DownloadDataEvent(1));
        EventBus.getDefault().post(new StatisticSavedEvent());
    }

    @Override
    public List<StatisticDataEntity> getStatisticForPeriod(int numberOfDays) {
        List<StatisticDataEntity> statisticData = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME +
                " ORDER BY " + COLUMN_DATE + " DESC", null);
        cursor.moveToFirst();
        int count = 0;
        while (!cursor.isAfterLast() && count < numberOfDays) {
            statisticData.add(new StatisticDataEntity(cursor.getLong(0), cursor.getInt(1)));
            cursor.moveToNext();
            count++;
        }
        cursor.close();

        return statisticData;
    }

    @Override
    public int getStatisticDaysCount() {
        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME, null);
        int statisticDaysCount = cursor.getCount();
        cursor.close();
        return statisticDaysCount;
    }

    @Override
    public int getConsumptionCountSummary() {
        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME, null);
        int countSummary = 0;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            countSummary += cursor.getInt(1);
            cursor.moveToNext();
        }

        cursor.close();
        return countSummary;
    }

    @Override
    public void deleteStatisticEntryByIndex(int index) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME +
                " ORDER BY " + COLUMN_DATE + " ASC", null);
        cursor.moveToFirst();

        // TODO Delete test code
        Log.d("Log", "" + index);

        int i = 0;
        long dateToDelete = 0;
        while (!cursor.isAfterLast()) {
            if (i == index) dateToDelete = cursor.getLong(0);
            cursor.moveToNext();
            i++;
        }

        // TODO Delete test code
        Log.d("Log", "" + dateToDelete);

        database.delete(STATISTIC_TABLE_NAME, COLUMN_DATE + " = " + dateToDelete, null);
        cursor.close();
    }

    //// SQLITE DATABASE LIFECYCLE

    @Override
    public void onCreate(SQLiteDatabase database) {
        createStatisticTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        dropStatisticTable(database);
        onCreate(database);
    }

    //// PRIVATE METHODS

    private void createStatisticTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + STATISTIC_TABLE_NAME + "("
                + COLUMN_DATE + " INTEGER NOT NULL, "
                + COLUMN_COUNT + " INTEGER NOT NULL, "
                + COLUMN_SEND_STATUS + " INTEGER NOT NULL DEFAULT 0)");
    }

    private void dropStatisticTable(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + STATISTIC_TABLE_NAME);
    }

    private int getConsumptionByDate(long date) {
        Cursor cursor = database.rawQuery(
                "SELECT " + COLUMN_COUNT + " FROM " + STATISTIC_TABLE_NAME +
                        " WHERE " + COLUMN_DATE + " = " + date, null);
        int consumptionByDate = 0;
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            consumptionByDate = cursor.getInt(0);
            cursor.moveToNext();
        }

        cursor.close();
        return consumptionByDate;
    }

    private void incrementConsumption(long date, int oldValue) {
        int consumption = oldValue + 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_COUNT, consumption);
        database.update(STATISTIC_TABLE_NAME, contentValues, COLUMN_DATE + " = " + date, null);
    }

    private boolean isStatisticPeriodLimitReached() {
        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME, null);
        int rowCount = 0;
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            rowCount++;
            cursor.moveToNext();
        }

        cursor.close();
        return rowCount >= StatisticDataStorage.MAX_RECORDS;
    }

    private void deleteFirstRowFromTable() {
        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME +
                " ORDER BY " + COLUMN_DATE + " ASC", null);
        cursor.moveToFirst();
        long dateToDelete = cursor.getInt(0);
        cursor.close();
        database.delete(STATISTIC_TABLE_NAME, COLUMN_DATE + " = " + dateToDelete, null);
    }

    private void markLastRowForSync() {
        if (isRestored) {
            isRestored = false;
            return;
        }

        Cursor cursor = database.rawQuery("SELECT * FROM " + STATISTIC_TABLE_NAME +
                " ORDER BY " + COLUMN_DATE + " DESC", null);
        cursor.moveToFirst();

        if (cursor.getCount() <= 0) {
            cursor.close();
            return;
        }

        long dateToMark = cursor.getInt(0);
        cursor.close();

        // TODO Delete test code
        Log.d(AppController.APP_TAG, "Date To Mark - " + dateToMark);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SEND_STATUS, 1);
        database.update(STATISTIC_TABLE_NAME, contentValues, COLUMN_DATE + " = " + dateToMark, null);
    }

    private void insertNewRow(long date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_COUNT, 1);
        database.insert(STATISTIC_TABLE_NAME, null, contentValues);
    }
}
