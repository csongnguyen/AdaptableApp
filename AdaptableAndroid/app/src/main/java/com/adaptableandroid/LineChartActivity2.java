package com.adaptableandroid;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.adaptableandroid.DemoBase;

import java.util.ArrayList;

public class LineChartActivity2 extends ActionBarActivity implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private LineChart mChart;
//    private SeekBar mSeekBarX, mSeekBarY;
//    private TextView tvX, tvY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(true);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setBackgroundDrawable(new ColorDrawable(0xFF4697b5));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        TextView tView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbarTitle);
        tView.setText("Adaptable");

//        tvX = (TextView) findViewById(R.id.tvXMax);
//        tvY = (TextView) findViewById(R.id.tvYMax);
//        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
//        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
//
//        mSeekBarX.setProgress(45);
//        mSeekBarY.setProgress(100);
//
//        mSeekBarY.setOnSeekBarChangeListener(this);
//        mSeekBarX.setOnSeekBarChangeListener(this);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);//Color.LTGRAY);

        // add data
        setData(8, 30);

        mChart.animateX(2500);


//        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
//        l.setForm(LegendForm.LINE);
        l.setForm(LegendForm.SQUARE);
//        l.setTypeface(tf);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);


        XAxis xAxis = mChart.getXAxis();
//        xAxis.setTypeface(tf);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setTypeface(tf);
//        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaxValue(50);//200f);
//        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);
//        rightAxis.setTypeface(tf);
        rightAxis.setTextColor(Color.RED);
//        rightAxis.setAxisMaxValue(900);
//        rightAxis.setStartAtZero(false);
//        rightAxis.setAxisMinValue(-200);
        rightAxis.setDrawGridLines(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (DataSet<?> set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.isHighlightEnabled())
                    mChart.setHighlightEnabled(false);
                else
                    mChart.setHighlightEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleFilled: {

                ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
                        .getDataSets();

                for (LineDataSet set : sets) {
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
                        .getDataSets();

                for (LineDataSet set : sets) {
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                ArrayList<LineDataSet> sets = (ArrayList<LineDataSet>) mChart.getData()
                        .getDataSets();

                for (LineDataSet set : sets) {
                    if (set.isDrawCubicEnabled())
                        set.setDrawCubic(false);
                    else
                        set.setDrawCubic(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStartzero: {
                mChart.getAxisLeft().setStartAtZero(!mChart.getAxisLeft().isStartAtZeroEnabled());
                mChart.getAxisRight().setStartAtZero(!mChart.getAxisRight().isStartAtZeroEnabled());
                mChart.invalidate();
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionToggleFilter: {

                // the angle of filtering is 35°
                Approximator a = new Approximator(ApproximatorType.DOUGLAS_PEUCKER, 35);

                if (!mChart.isFilteringEnabled()) {
                    mChart.enableFiltering(a);
                } else {
                    mChart.disableFiltering();
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();

                // mChart.saveToGallery("title"+System.currentTimeMillis())
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//        tvX.setText("" + (mSeekBarX.getProgress() + 1));
//        tvY.setText("" + (mSeekBarY.getProgress()));
//
//        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());

        // redraw
        mChart.invalidate();
    }

    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 5;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals1, "Tasks Completed");
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.MediumSeaGreen));//ColorTemplate.getHoloBlue());
        set1.setCircleColor(getResources().getColor(R.color.MediumSeaGreen));
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(getResources().getColor(R.color.MediumSeaGreen));//ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
//        set1.setCircleHoleColor(Color.WHITE);

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 10;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals2.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set2 = new LineDataSet(yVals2, "Articles Read");
//        set2.setAxisDependency(AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.RED);
        set2.setLineWidth(2f);
        set2.setCircleSize(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set2);
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
}
