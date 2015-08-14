package io.indico;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import io.indico.network.IndicoCallback;
import io.indico.results.IndicoResult;
import io.indico.utils.IndicoException;

/**
 * Created by Chris on 8/14/15.
 */
public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("debugDebug", "here");
        Indico.init(this, "e5f75cc0934f3a3cc834cbfb355a22a6", null);
        try {
            Indico.sentiment.predict("chris", new IndicoCallback<IndicoResult>() {
                @Override public void handle(IndicoResult result) throws IndicoException {
                    Log.i("debugDebug", result.getSentiment() + " double");
                }
            });
        } catch (IOException | IndicoException e) {
            e.printStackTrace();
        }
    }
}
