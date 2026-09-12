package com.google.android.play.core.review;
import android.app.Activity; import com.google.android.gms.tasks.Task;
public class ReviewManager {
 public Task<ReviewInfo> requestReviewFlow(){return new Task<ReviewInfo>(new ReviewInfo());}
 public Task<Void> launchReviewFlow(Activity a,ReviewInfo i){return new Task<Void>((Void)null);}
}
