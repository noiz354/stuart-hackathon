package com.stuart.hackatonproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.stuart.hackatonproject.R;
import com.stuart.hackatonproject.model.ReminderDB;
import com.stuart.hackatonproject.util.GeneralUtils;

public class ReminderHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView notifyTimeTextView;
    private TextView reminderFrom;
    private ImageView shareButton;
    private Activity context;

    public ReminderHolder(View itemView, Activity context) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.text_view_title);
        contentTextView = itemView.findViewById(R.id.text_view_content);
        notifyTimeTextView = itemView.findViewById(R.id.notify_text_view_time);
        reminderFrom = itemView.findViewById(R.id.text_reminder_from);
        shareButton = itemView.findViewById(R.id.share_button);
        this.context = context;
    }

    public void bind(final ReminderDB reminderDB) {
        titleTextView.setText(reminderDB.getTitle());
        contentTextView.setText(reminderDB.getContent());
        notifyTimeTextView.setText(GeneralUtils.generateReadableTime(reminderDB.getNotifyAt()));
        reminderFrom.setText(reminderDB.getFromUserId());
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setGoogleAnalyticsParameters(
                            new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("in-app")
                                .setMedium("social")
                                .setCampaign("sharing-team-campaign")
                                .build())
                        .setLink(Uri.parse("http://teamreminderstuart.com/detailReminder?id=" + reminderDB.getUniqueId()))
                        .setDynamicLinkDomain("cg47s.app.goo.gl")
                        .buildDynamicLink();

                Uri shortLink = dynamicLink.getUri();

                Intent sendIntent = new Intent();
                String msg = "Hey, check this out: " + shortLink;
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });
    }
}