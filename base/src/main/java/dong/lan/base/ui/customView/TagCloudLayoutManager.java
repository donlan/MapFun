/*
 *   Copyright 2016, donlan(梁桂栋)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me: stonelavender@hotmail.com
 */

package dong.lan.base.ui.customView;

import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 梁桂栋 on 17-1-16 ： 下午1:37.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: SmartTrip
 */

public class TagCloudLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = TagCloudLayoutManager.class.getSimpleName();
    private int maxWidth;
    private int maxHeight;

    public TagCloudLayoutManager() {
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {


        maxWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        maxHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (state.isPreLayout())
            return;

        myState().contentHeight = 0;
        myState().scrollY = 0;

        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        if (itemCount == 0) {
            return;
        }

        int offsetX = 0;
        int offsetY = 0;
        int maxHeight = 0;

        for (int i = 0; i < itemCount; i++) {
            View childView = recycler.getViewForPosition(i);

            addView(childView);
            measureChildWithMargins(childView, 0, 0);

            int width = getDecoratedMeasuredWidth(childView);
            int height = getDecoratedMeasuredHeight(childView);


            Rect viewFrame = myState().itemFrams.get(i);
            if (viewFrame == null) {
                viewFrame = new Rect();
            }

            if (width + offsetX > maxWidth) {
                offsetX = 0;
                offsetY += maxHeight;
                myState().contentHeight += maxHeight;
                maxHeight = height;
            }

            viewFrame.set(offsetX, offsetY, offsetX + width, offsetY + height);

            offsetX += width;
            maxHeight = Math.max(maxHeight, height);

            if (itemCount == 1 && myState().contentHeight == 0) {
                myState().contentHeight = height;
            }
            myState().itemFrams.put(i, viewFrame);
            myState().itemAttached.put(i, false);
            detachAndScrapView(childView, recycler);
        }

        layoutItems(recycler, state);

    }

    private void layoutItems(RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (state.isPreLayout())
            return;

        Rect displayFrame = new Rect(0, myState().scrollY,
                maxWidth, myState().scrollY + maxHeight);

        int count = getChildCount();

//         no recycle :

//        Rect childFrame = new Rect();
//        for (int i = 0; i < count; i++) {
//            View childView = getChildAt(i);
//            if (childView == null) {
//                myState().itemAttached.put(i, false);
//                continue;
//            }
//            childFrame.left = getDecoratedLeft(childView);
//            childFrame.top = getDecoratedTop(childView);
//            childFrame.right = getDecoratedRight(childView);
//            childFrame.bottom = getDecoratedBottom(childView);
//
//            if (!Rect.intersects(displayFrame, childFrame)) {
//                myState().itemAttached.put(i, false);
//                removeAndRecycleView(childView, recycler);
//            }
//        }

        count = getItemCount();
        for (int i = 0; i < count; i++) {
            if (Rect.intersects(displayFrame, myState().itemFrams.get(i))) {
                if (!myState().itemAttached.get(i)) {
                    View scrapView = recycler.getViewForPosition(i);
                    measureChildWithMargins(scrapView, 0, 0);
                    addView(scrapView);
                    Rect frame = myState().itemFrams.get(i);
                    layoutDecorated(scrapView, frame.left,
                            frame.top - myState().scrollY,
                            frame.right,
                            frame.bottom - myState().scrollY);
                    myState().itemAttached.put(i, true);
                }
            }
        }
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {

        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return myState().contentHeight > maxHeight;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (!canScrollVertically())
            return 0;

        int scroll = dy;
        if (myState().scrollY + dy < 0)
            scroll = -myState().scrollY;
        else if (myState().scrollY + dy > myState().contentHeight - maxHeight) {
            scroll = myState().contentHeight - maxHeight - myState().scrollY;
        }

        if (scroll == 0)
            return 0;
        myState().scrollY += scroll;
        offsetChildrenVertical(-scroll);

        layoutItems(recycler, state);

        return scroll;
    }

    private void fixOffsetPostion() {
    }

    private State state;
    private LinearSmoothScroller lss;

    private State myState() {
        if (state == null)
            state = new State();
        return state;
    }

    @Override
    public void scrollToPosition(int position) {

        if (!canScrollVertically())
            return;

        position = Math.max(0, position);
        position = Math.min(getItemCount(), position);

        myState().scrollY = myState().itemFrams.get(position).top;
        requestLayout();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        if (!canScrollVertically())
            return;

        position = Math.max(0, position);
        position = Math.min(getItemCount(), position);

        Log.d(TAG, "smoothScrollToPosition: " + position);
        if (lss == null) {
            lss = new LinearSmoothScroller(recyclerView.getContext()) {
                @Nullable
                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    int oldScrollY = myState().scrollY;

                    myState().scrollY = myState().itemFrams.get(targetPosition).top;

//                    fixScrollOffset();

                    int newScrollY = myState().scrollY;

                    myState().scrollY = oldScrollY;

                    return new PointF(0, newScrollY - oldScrollY);
                }
            };
        }
        lss.setTargetPosition(position);
        startSmoothScroll(lss);
    }

    private class State {

        SparseArray<Rect> itemFrams;
        SparseBooleanArray itemAttached;

        int scrollX;
        int scrollY;

        int contentWidth;
        int contentHeight;

        int orientation;

        public State() {
            this.itemFrams = new SparseArray<>();
            this.itemAttached = new SparseBooleanArray();
            this.scrollX = 0;
            this.scrollY = 0;
            this.contentWidth = 0;
            this.contentHeight = 0;
            this.orientation = 1;
        }
    }

}
