package com.soft_sketch.hereim.usagetracker.appUsage;


import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.soft_sketch.hereim.R;
import com.soft_sketch.hereim.usagetracker.barchart.BarChartView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {

    private List<CustomUsageStats> mCustomUsageStatsList = new ArrayList<>();
    int flag1=0;
    int flag2=0;
    private Context context;
    private long total;
    UsageListAdapter(Context c)
    {
        context=c;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPackageName;
        private final TextView mLastTimeUsed;
        private final ImageView mAppIcon;
        private final TextView mPercentage;
        private final ProgressBar pb ;
        private Context mContext;

        public ViewHolder(View v) {
            super(v);

            mPackageName = (TextView) v.findViewById(R.id.textview_package_name);
            mLastTimeUsed = (TextView) v.findViewById(R.id.textview_total_time);
            mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
            mPercentage=(TextView) v.findViewById(R.id.percentage);
            pb=(ProgressBar) v.findViewById(R.id.pb);
        }


        public TextView getLastTimeUsed() {
            return mLastTimeUsed;
        }

        public TextView getPackageName() {
            return mPackageName;
        }

        public ImageView getAppIcon() {
            return mAppIcon;
        }

        public TextView getPercentage() {
            return mPercentage;
        }
        public ProgressBar getProgressBar() {
            return pb;
        }
    }

    public UsageListAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.usage_row, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        double j;

        final PackageManager pm= context.getPackageManager();
        ApplicationInfo ai=null;
        try {
            ai=pm.getApplicationInfo(mCustomUsageStatsList.get(position).usageStats.getPackageName(), 0);

        }catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        viewHolder.getPackageName().setText(applicationName);
        BarChartView.ydata.add(applicationName);

        final long timeInForeground = mCustomUsageStatsList.get(position).usageStats.getTotalTimeInForeground();
          viewHolder.getLastTimeUsed().setText(calculateTime(timeInForeground));
        double percent=timeInForeground*100.0/(double)total;
        viewHolder.getPercentage().setText(calculatePercent(timeInForeground));
        j=(timeInForeground*100.0)/(double) total;

        BarChartView.xdata.add((float) j);

        BarChartView.addDataSet();
        viewHolder.getAppIcon().setImageDrawable(mCustomUsageStatsList.get(position).appIcon);
        if(percent>10)
        {
            viewHolder.getProgressBar().setProgressTintList(ColorStateList.valueOf(Color.RED));
        }else{
            viewHolder.getProgressBar().setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
        viewHolder.getProgressBar().setProgress((int)percent);

        /* onItemClickListener() */
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DateFormat dateFormat= SimpleDateFormat.getDateTimeInstance();
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.app_dialog);
                dialog.setTitle("Usage Details");
                TextView text = (TextView) dialog.findViewById(R.id.appname);
                text.setText(applicationName);
                TextView lastused = (TextView) dialog.findViewById(R.id.last_used);
                lastused.setText("Last Used : "+dateFormat.format(new Date(mCustomUsageStatsList.get(position).usageStats.getLastTimeUsed())));
                TextView totalused = (TextView) dialog.findViewById(R.id.total_used);
                totalused.setText("Total time used : "+timeInForeground/1000 +" sec");
                ImageView image = (ImageView) dialog.findViewById(R.id.image_icon);
                image.setImageDrawable(mCustomUsageStatsList.get(position).appIcon);

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }
    private long totalTime( List<CustomUsageStats> list){
        long total=0;
        for(CustomUsageStats app:list){
            total+=app.usageStats.getTotalTimeInForeground();
        }
        return total;
    }

    private String calculateTime(long ms)
    { String total="";
        long sec=ms/1000;
        long day;
        long hour;
        long min;
        if(sec>=(86400)){
            day=sec/86400;
            sec=sec%86400;
            total=total+day+"d";
        }
        if(sec>=3600){
            hour=sec/3600;
            sec=sec%3600;
            total=total+hour+"h";
        }
        if(sec>=60){
            min=sec/60;
            sec=sec%60;
            total=total+min+"m";
        }
        if(sec>0)
        {
            total=total+sec+"s";
        }
        return total;
    }

    private String calculatePercent(long ms) {
        DecimalFormat f = new DecimalFormat("##.00");
        return f.format(ms*100.0/(double)total)+"%";
    }
    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    public void setCustomUsageStatsList(List<CustomUsageStats> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
        total=totalTime(mCustomUsageStatsList);
        System.out.println("total time :"+total);
    }
}