package com.skronawi.dlr_school_lab.navigation_english.tasks;

import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.util.ImageDragShadowBuilder;
import com.skronawi.dlr_school_lab.navigation_english.util.MapSerializationUtil;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class TaskMapNavsToYearFragment extends AbstractTask {

    public static final String TMP_RESULT_KEY = "task.map-navs-to-year.tmp-result";

    //current mapping state
    private Map<Integer, Integer> targetIds2SourceIds;
    private Map<ImageView, Drawable> currentSlots2Drawables;

    //original drawables of imageViews
    private Map<Drawable, Integer> drawable2origSourceId;
    private Map<Drawable, Integer> drawable2origTargetId;

    //just for answers
//    private Map<Integer, String> sourceIds2Names = new HashMap<>();
//    private Map<Integer, Integer> targetIds2Numbers = new LinkedHashMap<>();

    private Drawable slotEmptyDrawable;
    private Drawable slotEnteredDrawable;
    private Button okButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        targetIds2SourceIds = new HashMap<>();
        currentSlots2Drawables = new HashMap<>();
        drawable2origSourceId = new HashMap<>();
        drawable2origTargetId = new HashMap<>();

        View taskView = inflater.inflate(R.layout.fragment_station_navs_to_year, container, false);

        ImageView compassView = (ImageView) taskView.findViewById(R.id.estimate_compass);
        ImageView lighthouseView = (ImageView) taskView.findViewById(R.id.estimate_lighthouse);
        ImageView portolanView = (ImageView) taskView.findViewById(R.id.estimate_portolan);
        ImageView radarView = (ImageView) taskView.findViewById(R.id.estimate_radar);
        ImageView satelliteView = (ImageView) taskView.findViewById(R.id.estimate_satellite);
        ImageView sextantView = (ImageView) taskView.findViewById(R.id.estimate_sextant);

        ImageView target1View = (ImageView) taskView.findViewById(R.id.estimate_slot1);
        ImageView target2View = (ImageView) taskView.findViewById(R.id.estimate_slot2);
        ImageView target3View = (ImageView) taskView.findViewById(R.id.estimate_slot3);
        ImageView target4View = (ImageView) taskView.findViewById(R.id.estimate_slot4);
        ImageView target5View = (ImageView) taskView.findViewById(R.id.estimate_slot5);
        ImageView target6View = (ImageView) taskView.findViewById(R.id.estimate_slot6);

        //"constant" drawables
        slotEmptyDrawable = getResources().getDrawable(R.drawable.slot_empty);
        slotEnteredDrawable = getResources().getDrawable(R.drawable.slot_entered);

//        targetIds2Numbers.put(R.id.estimate_slot1, 1);
//        targetIds2Numbers.put(R.id.estimate_slot2, 2);
//        targetIds2Numbers.put(R.id.estimate_slot3, 3);
//        targetIds2Numbers.put(R.id.estimate_slot4, 4);
//        targetIds2Numbers.put(R.id.estimate_slot5, 5);
//        targetIds2Numbers.put(R.id.estimate_slot6, 6);

        //the current/initial mapping of pictures to slots
        currentSlots2Drawables.put(compassView, compassView.getDrawable());
        currentSlots2Drawables.put(lighthouseView, lighthouseView.getDrawable());
        currentSlots2Drawables.put(portolanView, portolanView.getDrawable());
        currentSlots2Drawables.put(radarView, radarView.getDrawable());
        currentSlots2Drawables.put(satelliteView, satelliteView.getDrawable());
        currentSlots2Drawables.put(sextantView, sextantView.getDrawable());
        currentSlots2Drawables.put(target1View, target1View.getDrawable());
        currentSlots2Drawables.put(target2View, target2View.getDrawable());
        currentSlots2Drawables.put(target3View, target3View.getDrawable());
        currentSlots2Drawables.put(target4View, target4View.getDrawable());
        currentSlots2Drawables.put(target5View, target5View.getDrawable());
        currentSlots2Drawables.put(target6View, target6View.getDrawable());

        //the original mappings of the source slots
        drawable2origSourceId.put(compassView.getDrawable(), compassView.getId());
        drawable2origSourceId.put(lighthouseView.getDrawable(), lighthouseView.getId());
        drawable2origSourceId.put(portolanView.getDrawable(), portolanView.getId());
        drawable2origSourceId.put(radarView.getDrawable(), radarView.getId());
        drawable2origSourceId.put(satelliteView.getDrawable(), satelliteView.getId());
        drawable2origSourceId.put(sextantView.getDrawable(), sextantView.getId());

        //the original mappings of the target slots
        drawable2origTargetId.put(target1View.getDrawable(), target1View.getId());
        drawable2origTargetId.put(target2View.getDrawable(), target2View.getId());
        drawable2origTargetId.put(target3View.getDrawable(), target3View.getId());
        drawable2origTargetId.put(target4View.getDrawable(), target4View.getId());
        drawable2origTargetId.put(target5View.getDrawable(), target5View.getId());
        drawable2origTargetId.put(target6View.getDrawable(), target6View.getId());

        okButton = (Button) taskView.findViewById(R.id.buttonOk);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                taskManager.setAnswer(taskId, "OK");
                taskManager.nextTask();
                ((MainActivity) getActivity()).onCurrentTask();
            }
        });

        //touch & drag listener, only IF NOT ALREADY answered
        if (!taskManager.isTaskSolved(taskId)) {
            for (ImageView view : currentSlots2Drawables.keySet()) {
                view.setOnTouchListener(new MyTouchListener());
                view.setOnDragListener(new MyDragListener());
            }
        }

        taskView.findViewById(R.id.resolve_button).setVisibility(View.GONE);

        return taskView;
    }

    private Map<String, String> buildCompleteMapping() {

        //builds a complete mapping : currentImageViewId -> origImageViewId
        /*
        i->j means, that the slot with id 'i' now has the drawable, which was originally on slot with id 'j'.
        ALL mappings will be contained, also both a->b and b->a, which will revert them if not filtered correctly, when
        the state is restored again. it will be 6 * 2 = 12 mappings.
         */
        Map<String, String> currIdToOrigId = new HashMap<>();

        for (AbstractMap.Entry<ImageView, Drawable> pair : currentSlots2Drawables.entrySet()) {

            ImageView currentSlotView = pair.getKey();
            Drawable currentSlotDrawable = pair.getValue();

            Integer origSourceId = drawable2origSourceId.get(currentSlotDrawable);
            if (origSourceId != null) {
                //the original slot was a source slot
                currIdToOrigId.put(String.valueOf(currentSlotView.getId()), String.valueOf(origSourceId));
                continue; //as the source key was the id of a source-slot
            }

            Integer origTargetId = drawable2origTargetId.get(currentSlotDrawable);
            if (origTargetId != null) {
                //the original slot was a target slot
                currIdToOrigId.put(String.valueOf(currentSlotView.getId()), String.valueOf(origTargetId));
            }
        }

        return currIdToOrigId;
    }

    private void restoreFromMapping(Map<String, String> currIdToOrigId, View rootView) {

        /*
        only sets the current slot, not the original slot as this would revert itself. this is due
        to the fact, that the mappings contain both a->b and b->a.
         */

        for (AbstractMap.Entry<String, String> pair : currIdToOrigId.entrySet()) {

            //find the current imageview
            Integer currentSlotId = Integer.valueOf(pair.getKey());
            Integer origSlotId = Integer.valueOf(pair.getValue());

            ImageView currentSlot = (ImageView) rootView.findViewById(currentSlotId);

            //find the ORIGINAL drawable of the original imageView, NOT the current as it may have been altered already
            Drawable origSlotDrawable;
            if (drawable2origSourceId.values().contains(origSlotId)) {
                origSlotDrawable = getKeyByValue(drawable2origSourceId, origSlotId);
            } else {
                origSlotDrawable = getKeyByValue(drawable2origTargetId, origSlotId);
            }

            //set the drawable
            currentSlot.setImageDrawable(origSlotDrawable);

            currentSlots2Drawables.put(currentSlot, origSlotDrawable);

            //if currentSlot is a target slot, store the mapping as answer, but only, if the origSlotDrawable is NOT a 'X', but a real image
            if (drawable2origTargetId.values().contains(currentSlotId)
                    && drawable2origSourceId.keySet().contains(origSlotDrawable)) {
                targetIds2SourceIds.put(currentSlotId, origSlotId);
            }
        }
    }

    private Drawable getKeyByValue(Map<Drawable, Integer> drawable2origId, Integer slotId) {
        for (AbstractMap.Entry<Drawable, Integer> pair : drawable2origId.entrySet()) {
            if (pair.getValue().equals(slotId)) {
                return pair.getKey();
            }
        }
        throw new IllegalStateException("no key for value");
    }

    protected void storeState() {
        String mapToString = MapSerializationUtil.mapToString(buildCompleteMapping());
        preferencesManager.setString(TMP_RESULT_KEY, mapToString);
    }

    protected void restoreState(View rootView) {

        //in case of leftover state, clear the mapping and build them anew
        targetIds2SourceIds.clear();

        String mapToString = preferencesManager.getString(TMP_RESULT_KEY);
        if (!TextUtils.isEmpty(mapToString)) {
            restoreFromMapping(MapSerializationUtil.stringToMap(mapToString), rootView);
        }
        if (targetIds2SourceIds.size() == drawable2origTargetId.size()) {
            okButton.setEnabled(!taskManager.isTaskSolved(taskId));
        }
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");

//                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                Drawable dr = ((ImageView) view).getDrawable();
                Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 3, bitmap.getHeight() * 3, true);
                View.DragShadowBuilder shadowBuilder = ImageDragShadowBuilder.fromBitmap(getActivity(), bitmap);

                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View dragEventReceiver, DragEvent event) {

            ImageView sourceView = (ImageView) dragEventReceiver;
            ImageView targetView = (ImageView) event.getLocalState();

            Drawable currentSourceViewDrawable = currentSlots2Drawables.get(sourceView);
            Drawable currentTargetViewDrawable = currentSlots2Drawables.get(targetView);

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    targetView.setImageDrawable(slotEmptyDrawable);
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    sourceView.setImageDrawable(slotEnteredDrawable);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    sourceView.setImageDrawable(currentSourceViewDrawable);
                    break;

                case DragEvent.ACTION_DROP:

                    //exchange drawables & store current mapping
                    sourceView.setImageDrawable(currentTargetViewDrawable);
                    currentSlots2Drawables.put(sourceView, currentTargetViewDrawable);

                    targetView.setImageDrawable(currentSourceViewDrawable);
                    currentSlots2Drawables.put(targetView, currentSourceViewDrawable);

                    //if toView is a target-slot, and fromView contained a real estimateDrawable (not an 'X'), then store the
                    // mapping 'targetSlotId -> origSourceId'
                    if (drawable2origTargetId.values().contains(sourceView.getId())) {
                        Integer id = drawable2origSourceId.get(currentTargetViewDrawable);
                        if (id != null) {
                            //real estimate picture
                            targetIds2SourceIds.put(sourceView.getId(), id);
                        } else {
                            //the 'X' was dragged, so remove this mapping from the answer
                            targetIds2SourceIds.remove(sourceView.getId());
                        }
                    }

                    //maybe an exchange between target slots took place.
                    if (drawable2origTargetId.values().contains(targetView.getId())) {
                        Integer id = drawable2origSourceId.get(currentSourceViewDrawable);
                        if (id != null) {
                            targetIds2SourceIds.put(targetView.getId(), id);
                        } else {
                            targetIds2SourceIds.remove(targetView.getId());
                        }
                    }

                    //in case mappings complete activate the ok button
                    okButton.setEnabled(targetIds2SourceIds.size() == drawable2origTargetId.size());

                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    break;

                default:
                    break;
            }
            return true;
        }

    }
}
