package edu.unicauca.uplace.utils;


import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import edu.unicauca.uplace.R;

// TODO (Step 1 : Creating a utils package and a class for feature to edit the place details in it.)
// START
// For detail explanation of this class you can look at below link.
// https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6
/**
 * A abstract class which we will use for edit feature.
 */

public abstract class EditCallback extends  ItemTouchHelper.SimpleCallback{
    Context context;
    final Drawable editIcon;
    final int backgroundColor;
    final ColorDrawable background;
    final int intrinsicHeight;
    final int intrinsicWidth;
    final Paint clearPaint;

    public EditCallback(Context context) {
        super(0, ItemTouchHelper.RIGHT);
        this.context = context;
        editIcon = ContextCompat.getDrawable(context,R.drawable.ic_edit_white_24);
        intrinsicHeight= editIcon.getIntrinsicHeight();
        intrinsicWidth= editIcon.getIntrinsicWidth();
        background = new ColorDrawable();
        backgroundColor =Color.parseColor("#24AE05");
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.getAdapterPosition() == 10) {
            return 0;
        }else{
            return super.getMovementFlags(recyclerView, viewHolder);
        }

    }


    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();
        boolean isCancelled = dX == 0f && !isCurrentlyActive;


        if (isCancelled){
            clearCanvas(c,itemView.getLeft()+dX,itemView.getTop(),itemView.getLeft(),itemView.getBottom());
            super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
            return;
        }

        // Draw the red delete background
        background.setColor(backgroundColor);
        background.setBounds(itemView.getLeft() + Math.round(dX), itemView.getTop(), itemView.getLeft(), itemView.getBottom());
        background.draw(c);

        // Calculate position of delete icon
        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) ;
        int deleteIconLeft = itemView.getLeft() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getLeft() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;

        // Draw the red delete background
        background.setColor(backgroundColor);
        background.setBounds(itemView.getLeft() + Math.round(dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);

        // Draw the delete icon
        editIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        editIcon.draw(c);

        super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
    }

    public void clearCanvas(Canvas c, float left,float top,float right, float bottom){
        c.drawRect(left,top,right,bottom,clearPaint);
    }



}
// END