package comtest.example.android_team.background;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import comtest.example.android_team.AppManager;

public class WorkerTest extends Worker  {

    public WorkerTest(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("Info", "DO_WORK: Start method");
        doTask();
        return Result.success();
    }
    private void doTask() {

     //   AppManager.getInstance().createTCPConnection();

                Log.i("Info", "DO_TASK: Start EXECUTING TASK!!!!");

    //    AppManager.getInstance().closeTCPConnection();
    }
}
