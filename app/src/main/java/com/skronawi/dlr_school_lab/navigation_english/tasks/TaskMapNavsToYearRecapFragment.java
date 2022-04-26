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
import android.widget.TextView;
import android.widget.Toast;

import com.skronawi.dlr_school_lab.navigation_english.MainActivity;
import com.skronawi.dlr_school_lab.navigation_english.R;
import com.skronawi.dlr_school_lab.navigation_english.util.ImageDragShadowBuilder;
import com.skronawi.dlr_school_lab.navigation_english.util.MapSerializationUtil;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TaskMapNavsToYearRecapFragment extends AbstractTask {

    private static final String TMP_RESULT_KEY = "task.map-navs-to-year-recap.tmp-result";

    //current mapping state
    private Map<Integer, Integer> targetIds2SourceIds;
    private Map<ImageView, Drawable> currentSlots2Drawables;

    //original drawables of imageViews
    private Map<Drawable, Integer> drawable2origSourceId;
    private Map<Drawable, Integer> drawable2origTargetId;

    private Drawable slotEmptyDrawable;
    private Drawable slotEnteredDrawable;

    //for answers
    private Map<Integer, Integer> correctMappingSourceId2TargetId;

    private ImageView target1RightView;
    private ImageView target1WrongView;
    private ImageView target2RightView;
    private ImageView target2WrongView;
    private ImageView target3RightView;
    private ImageView target3WrongView;
    private ImageView target4RightView;
    private ImageView target4WrongView;
    private ImageView target5RightView;
    private ImageView target5WrongView;
    private ImageView target6RightView;
    private ImageView target6WrongView;

    private ImageView target1View;
    private ImageView target2View;
    private ImageView target3View;
    private ImageView target4View;
    private ImageView target5View;
    private ImageView target6View;
    private Button okButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        targetIds2SourceIds = new HashMap<>();
        currentSlots2Drawables = new HashMap<>();
        drawable2origSourceId = new HashMap<>();
        drawable2origTargetId = new HashMap<>();
        correctMappingSourceId2TargetId = new HashMap<>();

        View taskView = inflater.inflate(R.layout.fragment_station_navs_to_year, container, false);

        ImageView compassView = (ImageView) taskView.findViewById(R.id.estimate_compass);
        ImageView lighthouseView = (ImageView) taskView.findViewById(R.id.estimate_lighthouse);
        ImageView portolanView = (ImageView) taskView.findViewById(R.id.estimate_portolan);
        ImageView radarView = (ImageView) taskView.findViewById(R.id.estimate_radar);
        ImageView satelliteView = (ImageView) taskView.findViewById(R.id.estimate_satellite);
        ImageView sextantView = (ImageView) taskView.findViewById(R.id.estimate_sextant);

        target1View = (ImageView) taskView.findViewById(R.id.estimate_slot1);
        target2View = (ImageView) taskView.findViewById(R.id.estimate_slot2);
        target3View = (ImageView) taskView.findViewById(R.id.estimate_slot3);
        target4View = (ImageView) taskView.findViewById(R.id.estimate_slot4);
        target5View = (ImageView) taskView.findViewById(R.id.estimate_slot5);
        target6View = (ImageView) taskView.findViewById(R.id.estimate_slot6);

        target1RightView = (ImageView) taskView.findViewById(R.id.estimate_slot1_right);
        target1WrongView = (ImageView) taskView.findViewById(R.id.estimate_slot1_wrong);
        target2RightView = (ImageView) taskView.findViewById(R.id.estimate_slot2_right);
        target2WrongView = (ImageView) taskView.findViewById(R.id.estimate_slot2_wrong);
        target3RightView = (ImageView) taskView.findViewById(R.id.estimate_slot3_right);
        target3WrongView = (ImageView) taskView.findViewById(R.id.estimate_slot3_wrong);
        target4RightView = (ImageView) taskView.findViewById(R.id.estimate_slot4_right);
        target4WrongView = (ImageView) taskView.findViewById(R.id.estimate_slot4_wrong);
        target5RightView = (ImageView) taskView.findViewById(R.id.estimate_slot5_right);
        target5WrongView = (ImageView) taskView.findViewById(R.id.estimate_slot5_wrong);
        target6RightView = (ImageView) taskView.findViewById(R.id.estimate_slot6_right);
        target6WrongView = (ImageView) taskView.findViewById(R.id.estimate_slot6_wrong);

        //"constant" drawables
        slotEmptyDrawable = getResources().getDrawable(R.drawable.slot_empty);
        slotEnteredDrawable = getResources().getDrawable(R.drawable.slot_entered);

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

                if (targetIds2SourceIds.size() != drawable2origTargetId.size()) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.mappings_incomplete),
                            Toast.LENGTH_SHORT).show();
                } else {
                    String answer = buildAnswer();
                    taskManager.setAnswer(taskId, answer);
                    taskManager.nextTask();
                    ((MainActivity) getActivity()).onCurrentTask();
                }
            }
        });

        //touch & drag listener, only IF NOT ALREADY answered
        if (!taskManager.isTaskSolved(taskId)) {
            for (ImageView view : currentSlots2Drawables.keySet()) {
                view.setOnTouchListener(new MyTouchListener());
                view.setOnDragListener(new MyDragListener());
            }
        }

        ((TextView) taskView.findViewById(R.id.task_text)).setText(R.string.navs_to_year_recap_task);

        buildCorrectMappings(taskView);

        taskView.findViewById(R.id.resolve_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (targetIds2SourceIds.size() != drawable2origTargetId.size()) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.mappings_incomplete),
                            Toast.LENGTH_SHORT).show();
                } else {
                    setRightWrongMarkers();
                }
            }
        });

        return taskView;
    }

    private void setRightWrongMarkers() {

        for (AbstractMap.Entry<Integer, Integer> correctMappingEntry : correctMappingSourceId2TargetId.entrySet()) {

            boolean correctMappingFound = false;

            for (AbstractMap.Entry<Integer, Integer> currentMappingEntry : targetIds2SourceIds.entrySet()) {
                //compare key with value and vice-versa as the mappings are vice-versa!


                if (Objects.equals(currentMappingEntry.getKey(), correctMappingEntry.getValue())
                        && Objects.equals(currentMappingEntry.getValue(), correctMappingEntry.getKey())) {
                    //a correct mapping was found!
                    correctMappingFound = true;

                    if (currentMappingEntry.getKey().equals(target1View.getId())) {
                        target1RightView.setVisibility(View.VISIBLE);
                    } else if (currentMappingEntry.getKey().equals(target2View.getId())) {
                        target2RightView.setVisibility(View.VISIBLE);
                    } else if (currentMappingEntry.getKey().equals(target3View.getId())) {
                        target3RightView.setVisibility(View.VISIBLE);
                    } else if (currentMappingEntry.getKey().equals(target4View.getId())) {
                        target4RightView.setVisibility(View.VISIBLE);
                    } else if (currentMappingEntry.getKey().equals(target5View.getId())) {
                        target5RightView.setVisibility(View.VISIBLE);
                    } else if (currentMappingEntry.getKey().equals(target6View.getId())) {
                        target6RightView.setVisibility(View.VISIBLE);
                    }
                    break;
                }
            }
            if (!correctMappingFound) {
                if (correctMappingEntry.getValue().equals(target1View.getId())) {
                    target1WrongView.setVisibility(View.VISIBLE);
                } else if (correctMappingEntry.getValue().equals(target2View.getId())) {
                    target2WrongView.setVisibility(View.VISIBLE);
                } else if (correctMappingEntry.getValue().equals(target3View.getId())) {
                    target3WrongView.setVisibility(View.VISIBLE);
                } else if (correctMappingEntry.getValue().equals(target4View.getId())) {
                    target4WrongView.setVisibility(View.VISIBLE);
                } else if (correctMappingEntry.getValue().equals(target5View.getId())) {
                    target5WrongView.setVisibility(View.VISIBLE);
                } else if (correctMappingEntry.getValue().equals(target6View.getId())) {
                    target6WrongView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void buildCorrectMappings(View rootView) {

        ImageView compassView = (ImageView) rootView.findViewById(R.id.estimate_compass);
        ImageView lightHouseView = (ImageView) rootView.findViewById(R.id.estimate_lighthouse);
        ImageView portolanView = (ImageView) rootView.findViewById(R.id.estimate_portolan);
        ImageView radarView = (ImageView) rootView.findViewById(R.id.estimate_radar);
        ImageView satelliteView = (ImageView) rootView.findViewById(R.id.estimate_satellite);
        ImageView sextantView = (ImageView) rootView.findViewById(R.id.estimate_sextant);

        ImageView target1View = (ImageView) rootView.findViewById(R.id.estimate_slot1);
        ImageView target2View = (ImageView) rootView.findViewById(R.id.estimate_slot2);
        ImageView target3View = (ImageView) rootView.findViewById(R.id.estimate_slot3);
        ImageView target4View = (ImageView) rootView.findViewById(R.id.estimate_slot4);
        ImageView target5View = (ImageView) rootView.findViewById(R.id.estimate_slot5);
        ImageView target6View = (ImageView) rootView.findViewById(R.id.estimate_slot6);

        correctMappingSourceId2TargetId.put(lightHouseView.getId(), target1View.getId());
        correctMappingSourceId2TargetId.put(compassView.getId(), target2View.getId());
        correctMappingSourceId2TargetId.put(portolanView.getId(), target3View.getId());
        correctMappingSourceId2TargetId.put(sextantView.getId(), target4View.getId());
        correctMappingSourceId2TargetId.put(radarView.getId(), target5View.getId());
        correctMappingSourceId2TargetId.put(satelliteView.getId(), target6View.getId());
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

    private String buildAnswer() {
        return MapSerializationUtil.mapToString(buildCompleteMapping());
    }

    protected void storeState() {
        String mapToString = MapSerializationUtil.mapToString(buildCompleteMapping());
        preferencesManager.setString(TMP_RESULT_KEY, mapToString);
    }

    protected void restoreState(View rootView) {
        /*
        if loaded the very first time, use the result from the first task, otherwise the current result
         */
        String mapToString = preferencesManager.getString(TMP_RESULT_KEY);
        if (!TextUtils.isEmpty(mapToString)) {
            restoreFromMapping(MapSerializationUtil.stringToMap(mapToString), rootView);
        } else {
            mapToString = preferencesManager.getString(TaskMapNavsToYearFragment.TMP_RESULT_KEY);
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

                    removeRightWrongMarkers();

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

                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    break;

                default:
                    break;
            }
            return true;
        }

    }

    private void removeRightWrongMarkers() {
        target1RightView.setVisibility(View.INVISIBLE);
        target1WrongView.setVisibility(View.INVISIBLE);
        target2RightView.setVisibility(View.INVISIBLE);
        target2WrongView.setVisibility(View.INVISIBLE);
        target3RightView.setVisibility(View.INVISIBLE);
        target3WrongView.setVisibility(View.INVISIBLE);
        target4RightView.setVisibility(View.INVISIBLE);
        target4WrongView.setVisibility(View.INVISIBLE);
        target5RightView.setVisibility(View.INVISIBLE);
        target5WrongView.setVisibility(View.INVISIBLE);
        target6RightView.setVisibility(View.INVISIBLE);
        target6WrongView.setVisibility(View.INVISIBLE);
    }
}
