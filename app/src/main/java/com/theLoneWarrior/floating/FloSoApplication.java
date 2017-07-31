package com.theLoneWarrior.floating;

import android.app.Application;

import com.github.stkent.amplify.feedback.DefaultEmailFeedbackCollector;
import com.github.stkent.amplify.feedback.GooglePlayStoreFeedbackCollector;
import com.github.stkent.amplify.logging.AndroidLogger;
import com.github.stkent.amplify.tracking.Amplify;

public class FloSoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Amplify.setLogger(new AndroidLogger());

        final String releasePackageName = "com.github.stkent.testapp";

        Amplify.initSharedInstance(this)
               .setPositiveFeedbackCollectors(new GooglePlayStoreFeedbackCollector(releasePackageName))
               .setCriticalFeedbackCollectors(new DefaultEmailFeedbackCollector("someone@example.com"))
               .setAlwaysShow(true);
    }

}
