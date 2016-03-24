package app.com.example.popular_movies.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ABS - VIRUS on 2/24/2016.
 */
public class FetchMovieService extends IntentService {
    private final String LOG_TAG = FetchMovieService.class.getSimpleName();
    public static String MOVIE_QUERY_EXTRA = "MovieQueryExtra";

    public FetchMovieService() {
        super("FetchMovie");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String sortCriteria = intent.getStringExtra(MOVIE_QUERY_EXTRA);


    }

    public static class MovieAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendintent = new Intent(context, FetchMovieService.class);
            sendintent.putExtra(MOVIE_QUERY_EXTRA, intent.getStringExtra(MOVIE_QUERY_EXTRA));
            context.startService(sendintent);
        }
    }

}
